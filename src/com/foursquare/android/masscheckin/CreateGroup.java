package com.foursquare.android.masscheckin;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foursquare.android.masscheckin.asynctasks.LoadFriends;
import com.foursquare.android.masscheckin.classes.Friends;
import com.foursquare.android.masscheckin.classes.Group;
import com.foursquare.android.masscheckin.classes.SQLiteGroups;

public class CreateGroup extends Activity {

	private List<Friends> listFriends = new ArrayList();
	private SQLiteGroups sqlGroups;
	private EditText txtGroupName;
	private Button btnCreateGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		sqlGroups = new SQLiteGroups(getApplicationContext());
		findViewById(R.id.txtLoadingFriends).setVisibility(View.GONE);
		findViewById(R.id.progFriends).setVisibility(View.GONE);
		try {
			new LoadFriends().execute(listFriends, this);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}
		btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
		txtGroupName = (EditText) findViewById(R.id.txtGroupName);
		txtGroupName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (txtGroupName.length() > 0)
					btnCreateGroup.setEnabled(true);
				else
					btnCreateGroup.setEnabled(false);
			}
		});
		btnCreateGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean noFriendTagged = true;
				for (Friends friend : listFriends) {
					if (friend.isSelected) {
						noFriendTagged = false;
						break;
					}
				}
				if (noFriendTagged)
					Toast.makeText(getApplicationContext(),
							"Please tag at least one of your friends",
							Toast.LENGTH_LONG).show();
				else {
					List<Friends> taggedFriendList = new ArrayList();
					for (Friends friend : listFriends) {
						if (friend.isSelected) {
							taggedFriendList.add(friend);
						}
					}
					Group group = new Group();
					group.friendList = taggedFriendList;
					group.name = ((EditText) findViewById(R.id.txtGroupName))
							.getText().toString();
					group.id = sqlGroups.addGroup(group);
					ArrangeGroups.groupList.add(group);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_group, menu);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
