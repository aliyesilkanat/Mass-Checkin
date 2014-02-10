package com.foursquare.android.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

public class CheckIn extends FragmentActivity {
	final String[] names = new String[20];
	final String[] venuesId = new String[20];
	private GoogleMap myMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_in);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		Location ll = parseVenues();
		adjustMap(ll);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.check_in, menu);
		return true;
	}

	private Location parseVenues() {
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location ll = locationManager.getLastKnownLocation(locationProvider);

		String venueSearchUrl = "https://api.foursquare.com/v2/venues/search?ll="
				+ ll.getLatitude()
				+ ","
				+ ll.getLongitude()
				+ "&llAcc=1&altAcc=1&limit=20&oauth_token="
				+ MainActivity.ACCESS_TOKEN + "&v=20140210";
		try {
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			HttpGet httpGetRequest = new HttpGet(venueSearchUrl);
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();

			JSONObject jsonObject = new JSONObject(json);
			String s = jsonObject.getString("response");

			JSONObject responseJson = new JSONObject(s);
			JSONArray arrayVenues = responseJson.getJSONArray("venues");
			for (int i = 0; i < arrayVenues.length(); i++) {
				JSONObject jObj = arrayVenues.getJSONObject(i);
				names[i] = jObj.getString("name");
				venuesId[i] = jObj.getString("id");
			}
			ListView listView = (ListView) findViewById(R.id.lvVenues);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, names);

			listView.setAdapter(adapter);

		} catch (Exception e) {
			// In your production code handle any errors and catch the
			// individual exceptions
			e.printStackTrace();
		}
		return ll;
	}

	private LatLng adjustMap(Location lastKnownLocation) {
		android.support.v4.app.FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);

		myMap = mySupportMapFragment.getMap();

		UiSettings ui = myMap.getUiSettings();

		ui.setAllGesturesEnabled(false);
		ui.setMyLocationButtonEnabled(false);
		ui.setZoomControlsEnabled(false);
		myMap.setMyLocationEnabled(true);

		double lat = lastKnownLocation.getLatitude();
		double lng = lastKnownLocation.getLongitude();
		LatLng ll = new LatLng(lat, lng);
		myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));
		return ll;
	}
}
