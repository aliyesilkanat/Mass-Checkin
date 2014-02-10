/*
 * Copyright (C) 2013 Foursquare Labs, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foursquare.android.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareInvalidRequestException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.FoursquareUnsupportedVersionException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

/**
 * A sample activity demonstrating usage of the Foursquare auth library.
 * 
 * @date 2013-06-01
 */
public class MainActivity extends FragmentActivity {

	private static final int REQUEST_CODE_FSQ_CONNECT = 200;
	private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201;
	private GoogleMap myMap;
	/**
	 * Obtain your client id and secret from:
	 * https://foursquare.com/developers/apps
	 */
	private static final String CLIENT_ID = "3CNG5BVSYUPI0DZYGW1RV0UF4K12QX3DL5GCZHXK4KQPXATU";
	private static final String CLIENT_SECRET = "YQG0U3D3GCYCTBER2E2HW24FP0WO3UWIREG1W0BHNYKHQEVH";
	private String ACCESS_TOKEN;
	final String[] names = new String[20];
	final String[] venuesId = new String[20];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		ensureUi();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_FSQ_CONNECT:
			onCompleteConnect(resultCode, data);
			break;

		case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
			onCompleteTokenExchange(resultCode, data);
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * Update the UI. If we already fetched a token, we'll just show a success
	 * message.
	 */
	private void ensureUi() {
		boolean isAuthorized = !TextUtils.isEmpty(ExampleTokenStore.get()
				.getToken());

		TextView tvMessage = (TextView) findViewById(R.id.tvMessage);
		tvMessage.setVisibility(isAuthorized ? View.VISIBLE : View.GONE);

		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setVisibility(isAuthorized ? View.GONE : View.VISIBLE);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start the native auth flow.

				Intent intent = FoursquareOAuth.getConnectIntent(
						MainActivity.this, CLIENT_ID);

				// If the device does not have the Foursquare app installed,
				// we'd
				// get an intent back that would open the Play Store for
				// download.
				// Otherwise we start the auth flow.
				if (FoursquareOAuth.isPlayStoreIntent(intent)) {
					toastMessage(MainActivity.this,
							getString(R.string.app_not_installed_message));
					startActivity(intent);
				} else {
					startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
				}
			}
		});
		if (isAuthorized) {
			setContentView(R.layout.test1);

			Location ll = parseVenues();
			adjustMap(ll);
		}
	}

	private Location parseVenues() {
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location ll = locationManager
				.getLastKnownLocation(locationProvider);
			
		String venueSearchUrl = "https://api.foursquare.com/v2/venues/search?ll="
				+ ll.getLatitude()
				+ ","
				+ ll.getLongitude()
				+ "&llAcc=1&altAcc=1&limit=20&oauth_token=" + ACCESS_TOKEN+"&v=20140210";  
		try 
		{
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			HttpGet httpGetRequest = new HttpGet(venueSearchUrl);
			HttpResponse httpResponse = defaultClient
					.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(httpResponse.getEntity()
							.getContent(), "UTF-8"));
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

	private void onCompleteConnect(int resultCode, Intent data) {
		AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(
				resultCode, data);
		Exception exception = codeResponse.getException();

		if (exception == null) {
			// Success.
			String code = codeResponse.getCode();
			performTokenExchange(code);

		} else {
			if (exception instanceof FoursquareCancelException) {
				// Cancel.
				toastMessage(this, "Canceled");

			} else if (exception instanceof FoursquareDenyException) {
				// Deny.
				toastMessage(this, "Denied");

			} else if (exception instanceof FoursquareOAuthException) {
				// OAuth error.
				String errorMessage = exception.getMessage();
				String errorCode = ((FoursquareOAuthException) exception)
						.getErrorCode();
				toastMessage(this, errorMessage + " [" + errorCode + "]");

			} else if (exception instanceof FoursquareUnsupportedVersionException) {
				// Unsupported Fourquare app version on the device.
				toastError(this, exception);

			} else if (exception instanceof FoursquareInvalidRequestException) {
				// Invalid request.
				toastError(this, exception);

			} else {
				// Error.
				toastError(this, exception);
			}
		}
	}

	private void onCompleteTokenExchange(int resultCode, Intent data) {
		AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(
				resultCode, data);
		Exception exception = tokenResponse.getException();

		if (exception == null) {
			String accessToken = tokenResponse.getAccessToken();
			// Success.
			toastMessage(this, "Access token: " + accessToken);
			ACCESS_TOKEN = accessToken;
			// Persist the token for later use. In this example, we save
			// it to shared prefs.
			ExampleTokenStore.get().setToken(accessToken);

			// Refresh UI.
			ensureUi();

		} else {
			if (exception instanceof FoursquareOAuthException) {
				// OAuth error.
				String errorMessage = ((FoursquareOAuthException) exception)
						.getMessage();
				String errorCode = ((FoursquareOAuthException) exception)
						.getErrorCode();
				toastMessage(this, errorMessage + " [" + errorCode + "]");

			} else {
				// Other exception type.
				toastError(this, exception);
			}
		}
	}

	/**
	 * Exchange a code for an OAuth Token. Note that we do not recommend you do
	 * this in your app, rather do the exchange on your server. Added here for
	 * demo purposes.
	 * 
	 * @param code
	 *            The auth code returned from the native auth flow.
	 */
	private void performTokenExchange(String code) {
		Intent intent = FoursquareOAuth.getTokenExchangeIntent(this, CLIENT_ID,
				CLIENT_SECRET, code);
		startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
	}

	public static void toastMessage(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void toastError(Context context, Throwable t) {
		Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
	}
}
