package com.foursquare.android.masscheckin.asynctasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.masscheckin.CreateGroup;
import com.foursquare.android.masscheckin.R;
import com.foursquare.android.masscheckin.classes.CustomFriendsListAdapter;
import com.foursquare.android.masscheckin.classes.DateSingleton;
import com.foursquare.android.masscheckin.classes.Friends;
import com.foursquare.android.masscheckin.classes.Venue;

public class LoadFriends extends AsyncTask<Object, View, Activity> {
	private List<Friends> listFriends;
	private JSONArray arrayFriends;
	private AndroidHttpClient defaultClient;
	private Activity act;
	private ProgressBar progLoading;
	private TextView txtLoading;
	private ListView lvFriends;
	private Button btnCreateGroup;
	private String loadFriendsURL = "https://api.foursquare.com/v2/users/self/friends?oauth_token="
			+ Venue.ACCESS_TOKEN + "&v=" + DateSingleton.getDate();

	@Override
	protected Activity doInBackground(Object... params) {
		listFriends = (List<Friends>) params[0];
		act = (Activity) params[1];

		progLoading = (ProgressBar) act.findViewById(R.id.progFriends);
		txtLoading = (TextView) act.findViewById(R.id.txtLoadingFriends);
		lvFriends = (ListView) act.findViewById(R.id.lvFriends);
		btnCreateGroup = (Button) act.findViewById(R.id.btnCreateGroup);

		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				lvFriends.setVisibility(View.GONE);
				progLoading.setVisibility(View.VISIBLE);
				txtLoading.setVisibility(View.VISIBLE);
				btnCreateGroup.setEnabled(false);
			}
		});
		try {
			defaultClient = AndroidHttpClient.newInstance("Android");
			HttpGet httpGetRequest = new HttpGet(loadFriendsURL);
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();

			JSONObject jsonObject = new JSONObject(json);
			String s = jsonObject.getString("response");

			JSONObject responseJson = new JSONObject(s);
			s = responseJson.getString("friends");
			responseJson = new JSONObject(s);
			arrayFriends = responseJson.getJSONArray("items");

			for (int i = 0; i < arrayFriends.length(); i++) {
				JSONObject jObj = arrayFriends.getJSONObject(i);

				Friends friend = new Friends();
				friend.id = jObj.getString("id");
				friend.firstName = jObj.getString("firstName");
				if (jObj.has("lastName"))
					friend.LastName = jObj.getString("lastName");
				else
					friend.LastName = "";
				if (jObj.has("photo")) {
					friend.hasPhoto = true;
					String photoStr = jObj.getString("photo");
					JSONObject responsePhoto = new JSONObject(photoStr);
					friend.photoPrefix = responsePhoto.getString("prefix");
					friend.photoSuffix = responsePhoto.getString("suffix");
				}
				listFriends.add(friend);
			}
		} catch (final Exception e) {
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(act.getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			});
		} finally {
			defaultClient.close();
		}
		return act;
	}

	@Override
	protected void onPostExecute(final Activity result) {
		Collections.sort(listFriends);
		CustomFriendsListAdapter adap = new CustomFriendsListAdapter(
				result.getApplicationContext(), listFriends);
		lvFriends.setAdapter(adap);

		result.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progLoading.setVisibility(View.GONE);
				txtLoading.setVisibility(View.GONE);
				lvFriends.setVisibility(View.VISIBLE);
				if (((EditText) CreateGroup.currentAct
						.findViewById(R.id.txtGroupName)).getText().length() > 0)
					btnCreateGroup.setEnabled(true);
				if (CreateGroup.ACTION_MODE == CreateGroup.CONSTANT_EDIT_GROUP) {
					for (Friends friend : listFriends) {
						for (Friends f : CreateGroup.editingGroup.friendList) {
							if (friend.id.equals(f.id))
								friend.isSelected = true;
						}
					}
					((EditText)result.findViewById(R.id.txtGroupName)).setText(CreateGroup.editingGroup.name);
					CustomFriendsListAdapter adapter = new CustomFriendsListAdapter(result, listFriends);
					((ListView)result.findViewById(R.id.lvFriends)).setAdapter(adapter);
				}

			}
		});
		super.onPostExecute(result);
	}
}
