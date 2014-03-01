package com.foursquare.android.masscheckin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

public class CheckInActivity extends FragmentActivity {
	List<Venue> venueList = new ArrayList<Venue>();
	private GoogleMap myMap;
	private SharedPreferences.Editor prefsEditor;
	public ProgressBar prog;
	public View row;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_in);
		Log.i("override", "onCreate");
		SharedPreferences sharedPref = getSharedPreferences(
				"massCheckInTokenFile", MODE_PRIVATE);
		prefsEditor = sharedPref.edit();
		if (savedInstanceState != null) {
			Venue.ACCESS_TOKEN = sharedPref.getString("accessToken", "");
			Log.i("override", "ACCESS_TOKEN=" + Venue.ACCESS_TOKEN);
		}
		new CustomLocation(this);
		adjustMap(); //initialize ui for map
		parseVenues(CustomLocation.getRefreshedLocation());
		final ListView lv = (ListView) findViewById(R.id.lvVenues);
		prog = (ProgressBar) findViewById(R.id.progressBar);
		prog.setVisibility(View.GONE);
	
	
		lv.setClickable(true);
		final Activity act = this;

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				arg1.requestFocusFromTouch();

				new MakeCheckIn().execute(venueList, position, arg1, act,
						LoadVenues.CONST_LOADVENUES);

			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.check_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_search:
			Intent in = new Intent(this,
					com.foursquare.android.masscheckin.SearchActivity.class);		
			startActivity(in);
			return true;
		case R.id.action_refresh:
			parseVenues(CustomLocation.getRefreshedLocation());
			return true;		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void parseVenues(Location ll) {

		double lat = ll.getLatitude();
		double lng = ll.getLongitude();
		LatLng latlng = new LatLng(lat, lng);
		myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
		ListView lv = (ListView) findViewById(R.id.lvVenues);
		venueList = new ArrayList<Venue>();
		try {

			new LoadVenues().execute(ll, venueList, this,
					LoadVenues.CONST_LOADVENUES);
		} catch (Exception e) {

			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT)
					.show();			
		}
		return;
	}

	private void adjustMap() {
		android.support.v4.app.FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);

		myMap = mySupportMapFragment.getMap();
		myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		UiSettings ui = myMap.getUiSettings();

		ui.setAllGesturesEnabled(false);
		ui.setMyLocationButtonEnabled(false);
		ui.setZoomControlsEnabled(false);
		myMap.setMyLocationEnabled(true);

	}

	@Override
	protected void onRestart() {
		Log.i("override", "onRestart");

		super.onRestart();
	}

	@Override
	protected void onStop() {
		Log.i("override", "onStop");
		// this.finish();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i("override", "onDestroy");
		super.onDestroy();
	}
}
