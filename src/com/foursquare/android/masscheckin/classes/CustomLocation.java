package com.foursquare.android.masscheckin.classes;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class CustomLocation {
	private static Location ll;
	private static Activity act;

	public CustomLocation(Activity act) {
		ll = new Location("");
		CustomLocation.act = act;
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		LocationManager locationManager = (LocationManager) act
				.getSystemService(Context.LOCATION_SERVICE);
		ll.setLatitude(locationManager.getLastKnownLocation(locationProvider)
				.getLatitude());
		ll.setLongitude(locationManager.getLastKnownLocation(locationProvider)
				.getLongitude());

	}

	public static Location getRefreshedLocation() {
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		LocationManager locationManager = (LocationManager) act
				.getSystemService(Context.LOCATION_SERVICE);
		ll.setLatitude(locationManager.getLastKnownLocation(locationProvider)
				.getLatitude());
		ll.setLongitude(locationManager.getLastKnownLocation(locationProvider)
				.getLongitude());
		return ll;
	}

	public static Location getLocation() {
		return ll;

	}

}
