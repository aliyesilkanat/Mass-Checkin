package com.foursquare.android.masscheckin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.foursquare.android.masscheckin.classes.Group;
import com.foursquare.android.masscheckin.classes.SQLiteGroups;
import com.foursquare.android.masscheckin.navdrawer.NavDrawerItem;
import com.foursquare.android.masscheckin.navdrawer.NavDrawerListAdapter;

public class ArrangeGroups extends Activity {

	private DrawerLayout navDrawerLayout;
	private ListView navDrawerList;
	private ActionBarDrawerToggle navDrawerToggle;
	private String[] navMenuTitles;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter navAdapter;
	private TypedArray navMenuIcons;

	private ListView lvGroups;
	public static List<Group> groupList = new ArrayList();
	private List<String> groupNames = new ArrayList<String>();
	private ArrayAdapter<String> groupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arrange_groups);
		setNavDrawer();
		lvGroups = (ListView) findViewById(R.id.lvGroups);
		findViewById(R.id.btnCreateNewGroup).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								getApplicationContext(),
								com.foursquare.android.masscheckin.CreateGroup.class);
						startActivity(intent);

					}
				});

		loadGroups();
		
		
		
		findViewById(R.id.btnDeleteDB).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			deleteDatabase("GroupDB");
			lvGroups.setAdapter(null);			}
		});
	}
	private void loadGroups(){
		groupNames = new ArrayList<String>();
		SQLiteGroups sql = new SQLiteGroups(this);
		for (Group g : sql.getAllGroups()) {
			groupNames.add(g.name);
		}
		groupAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				groupNames);
		lvGroups.setAdapter(groupAdapter);
	}
	@Override
	protected void onResume() {
		super.onResume();
		loadGroups();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.arrange_groups, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (navDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setNavDrawer() {
		navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_groups);

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

		navAdapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		navDrawerList.setAdapter(navAdapter);

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
					Intent in = new Intent(
							getApplicationContext(),
							com.foursquare.android.masscheckin.CheckInActivity.class);
					in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					navDrawerLayout.closeDrawers();
					startActivity(in);
					break;
				case 1:
					navDrawerLayout.closeDrawers();

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

}
