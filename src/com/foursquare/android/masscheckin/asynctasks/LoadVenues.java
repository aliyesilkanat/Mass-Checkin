/**
 * 
 */
package com.foursquare.android.masscheckin.asynctasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.masscheckin.MainActivity;
import com.foursquare.android.masscheckin.R;
import com.foursquare.android.masscheckin.classes.DateSingleton;
import com.foursquare.android.masscheckin.classes.Venue;

/**
 * @author smsng
 * 
 */
public class LoadVenues extends AsyncTask<Object, View, Activity> {
	List<Venue> venueList;
	JSONArray arrayVenues;
	public static final int CONST_LOADVENUES = 0;
	public static final int CONST_SUGGESTVENUES = 1;
	int loadType;
	String query;
	private AndroidHttpClient defaultClient;

	@Override
	protected Activity doInBackground(Object... params) {

		Location ll = (Location) params[0];
		venueList = (List<Venue>) params[1];

		final Activity act = (Activity) params[2];
		loadType = (Integer) params[3];
		final ProgressBar prog;
		String venueSearchUrl = "";

		ListView nonFinalLv = null;
		ProgressBar nonFinalProg = null;

		if (loadType == CONST_LOADVENUES) {

			nonFinalProg = (ProgressBar) act.findViewById(R.id.progressBar);
			nonFinalLv = (ListView) act.findViewById(R.id.lvVenues);
			venueSearchUrl = "https://api.foursquare.com/v2/venues/search?ll="
					+ ll.getLatitude() + "," + ll.getLongitude()
					+ "&llAcc=1&altAcc=1&limit=20&intent=checkin&oauth_token="
					+ Venue.ACCESS_TOKEN + "&v=" + DateSingleton.getDate();
		} else if (loadType == CONST_SUGGESTVENUES) {
			nonFinalProg = (ProgressBar) act
					.findViewById(R.id.progressBarSearch);
			nonFinalLv = (ListView) act.findViewById(R.id.lvSearchVenues);
			query = (String) params[4];
			venueSearchUrl = "https://api.foursquare.com/v2/venues/search?ll="
					+ ll.getLatitude() + "," + ll.getLongitude()
					+ "&llAcc=10&altAcc=10&query=" + query
					+ "&intent=checkin&limit=20&oauth_token="
					+ Venue.ACCESS_TOKEN + "&v=" + DateSingleton.getDate();
		}
		prog = nonFinalProg;
		final ListView lv = nonFinalLv;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				prog.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
			}
		});

		try {

			defaultClient = AndroidHttpClient.newInstance("Android");
			HttpGet httpGetRequest = new HttpGet(venueSearchUrl);
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));
			Log.i("GETURL", venueSearchUrl);
			String json = reader.readLine();

			JSONObject jsonObject = new JSONObject(json);
			String s = jsonObject.getString("response");

			JSONObject responseJson = new JSONObject(s);
			arrayVenues = responseJson.getJSONArray("venues");
		
			if (arrayVenues.length() == 0)

			{
				act.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(act.getApplicationContext(),
								R.string.venue_not_found, Toast.LENGTH_LONG)
								.show();

					}
				});
			} else {
				if (loadType == CONST_LOADVENUES) {
					MakeCheckIn.initializeCheckedInArrays(CONST_LOADVENUES,
							arrayVenues.length());
				} else if (loadType == CONST_SUGGESTVENUES) {
					MakeCheckIn.initializeCheckedInArrays(CONST_SUGGESTVENUES,
							arrayVenues.length());
				}
				for (int i = 0; i < arrayVenues.length(); i++) {
					JSONObject jObj = arrayVenues.getJSONObject(i);
					venueList.add(new Venue());
					venueList.get(i).name = jObj.getString("name");
					venueList.get(i).venueId = jObj.getString("id");
					venueList.get(i).address = "";
					venueList.get(i).category = "";
					if (jObj.has("categories")) {
						JSONArray jCategoryArr = jObj
								.getJSONArray("categories");
						if (!jCategoryArr.isNull(0)) {
							JSONObject jCategoryObj = jCategoryArr
									.getJSONObject(0);
							if (jCategoryObj.has("shortName"))
								venueList.get(i).category = jCategoryObj
										.getString("shortName");
						}
						// else
						// venueList[i].category= "";
					}
					String locationStr = jObj.getString("location");
					JSONObject responseLocation = new JSONObject(locationStr);

					venueList.get(i).distance = responseLocation
							.getString("distance");
					if (responseLocation.has("address"))
						venueList.get(i).address = responseLocation
								.getString("address");
					else if (responseLocation.has("city"))
						venueList.get(i).address = responseLocation
								.getString("city");
					else if (responseLocation.has("country"))
						venueList.get(i).address = responseLocation
								.getString("country");
					// else venueList[i].address="";

				}
			}
		} catch (final Exception e) {
			act.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(act.getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();

				}
			});

		}finally {
			defaultClient.close();
		}

		return act;
	}

	@Override
	protected void onPostExecute(final Activity result) {
		super.onPostExecute(result);
		final ListView listView;
		ListView nonFinalLv = null;
		try {
			if (loadType == CONST_LOADVENUES) {
				nonFinalLv = (ListView) result.findViewById(R.id.lvVenues);
			} else if (loadType == CONST_SUGGESTVENUES) {
				nonFinalLv = (ListView) result
						.findViewById(R.id.lvSearchVenues);
			}
			listView = nonFinalLv;
			if (arrayVenues.length() > 0) {
				final List<Map<String, String>> data = new ArrayList<Map<String, String>>();

				for (Venue v : venueList) {
					Map<String, String> datum = new HashMap<String, String>(2);
					datum.put("First Line", v.name);
					datum.put("Second Line", v.category + " / " + v.address
							+ " (" + v.distance + "m)");
					data.add(datum);
				}
				result.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						SimpleAdapter adapter = new SimpleAdapter(result, data,
								android.R.layout.simple_list_item_2,
								new String[] { "First Line", "Second Line" },
								new int[] { android.R.id.text1,
										android.R.id.text2 }) {

							@Override
							public View getView(int position, View convertView,
									ViewGroup parent) {

								if (convertView != null) {
									TextView text1 = ((TextView) convertView
											.findViewById(android.R.id.text1));
									TextView text2 = ((TextView) convertView
											.findViewById(android.R.id.text2));
									if (loadType == CONST_LOADVENUES
											&& MakeCheckIn.checkedInVenuesIds[position]) {
										convertView
												.setBackgroundResource(R.color.custom1);
										text1.setTextColor(Color.BLACK);
										text2.setTextColor(Color.BLACK);
									} else if (loadType == CONST_SUGGESTVENUES
											&& MakeCheckIn.checkedInSuggestedVenuesIds[position]) {
										convertView
												.setBackgroundResource(R.color.custom1);
										text1.setTextColor(Color.BLACK);
										text2.setTextColor(Color.BLACK);
									} else {
										convertView
												.setBackgroundColor(Color.TRANSPARENT);
										text1.setTextColor(Color.WHITE);
										text2.setTextColor(Color.WHITE);
									}
								}
								this.notifyDataSetChanged();

								return super.getView(position, convertView,
										parent);
							}

						};

						listView.setAdapter(adapter);
						listView.setVisibility(View.VISIBLE);
					}
				});
			}
			// if koslu disina yapilarak liste bos oldugunda da progressBar in
			// donmesi onlendi
			result.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (loadType == CONST_LOADVENUES) {
						result.findViewById(R.id.progressBar).setVisibility(
								View.GONE);

					} else if (loadType == CONST_SUGGESTVENUES) {
						result.findViewById(R.id.progressBarSearch)
								.setVisibility(View.GONE);
					}

				}
			});
		} catch (final Exception ex) {
			Log.i("TOKEN", ex.getMessage());

			Intent intent = new Intent(result, MainActivity.class);
			result.startActivity(intent);
			result.finish();
			result.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(result.getApplicationContext(),
							ex.getMessage(), Toast.LENGTH_LONG).show();

				}
			});
		} 
	}
}