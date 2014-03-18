package com.foursquare.android.masscheckin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foursquare.android.masscheckin.asynctasks.LoadVenues;
import com.foursquare.android.masscheckin.asynctasks.MakeCheckIn;
import com.foursquare.android.masscheckin.classes.CustomLocation;
import com.foursquare.android.masscheckin.classes.Venue;
import com.foursquare.android.masscheckin.navdrawer.NavDrawerItem;
import com.foursquare.android.masscheckin.navdrawer.NavDrawerListAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

public class CheckInActivity extends FragmentActivity {
	List<Venue> venueList = new ArrayList<Venue>();
	public static Context context;
	private GoogleMap myMap;
	private SharedPreferences.Editor prefsEditor;
	public ProgressBar prog;
	public View row;

	private DrawerLayout navDrawerLayout;
	private ListView navDrawerList;
	private ActionBarDrawerToggle navDrawerToggle;

	private String[] navMenuTitles;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private TypedArray navMenuIcons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_in);
		context=this.getApplicationContext();
		setNavDrawer();
		Log.i("override", "onCreate");
		SharedPreferences sharedPref = getSharedPreferences(
				"massCheckInTokenFile", MODE_PRIVATE);
		prefsEditor = sharedPref.edit();
		if (savedInstanceState != null) {
			Venue.ACCESS_TOKEN = sharedPref.getString("accessToken", "");
			Log.i("override", "ACCESS_TOKEN=" + Venue.ACCESS_TOKEN);
		}
		new CustomLocation(this);
		adjustMap(); // initialize ui for map
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

	private void setNavDrawer() {
		navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_checkin);

		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navDrawerList = (ListView) findViewById(R.id.leftDrawerListView);
		navDrawerItems = new ArrayList<NavDrawerItem>();

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));

		navMenuIcons.recycle();

		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		navDrawerList.setAdapter(adapter);

		navDrawerToggle = new ActionBarDrawerToggle(this, navDrawerLayout,
				R.drawable.ic_drawer, R.string.open_drawer,
				R.string.close_drawer);
		navDrawerLayout.setDrawerListener(navDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		navDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					navDrawerLayout.closeDrawers();
					break;
				case 1:
					Intent in = new Intent(
							getApplicationContext(),
							com.foursquare.android.masscheckin.ArrangeGroups.class);
					in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
					navDrawerLayout.closeDrawers();
					startActivity(in);
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		navDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		navDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.check_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (navDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
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
