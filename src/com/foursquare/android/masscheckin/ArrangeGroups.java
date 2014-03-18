package com.foursquare.android.masscheckin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.foursquare.android.masscheckin.classes.CustomGroupsListAadapter;
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
	private Button deleteGroups;
	public static Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arrange_groups);
		setNavDrawer();
		act = this;
		lvGroups = (ListView) findViewById(R.id.lvGroups);
		deleteGroups = (Button) findViewById(R.id.btnDeleteGroups);
		deleteGroups.setEnabled(false);
		deleteGroups.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SQLiteGroups sql = new SQLiteGroups(act);
				Queue<Group> deletionQueue = new LinkedList<Group>();
				for (Group g : groupList) {
					if (g.isSelectedToDeletion) {
						sql.deleteGroup(g);
						deletionQueue.add(g);
					}
				}
				while (deletionQueue.size() != 0) {
					groupList.remove(deletionQueue.poll());
				}
				CustomGroupsListAadapter adap = new CustomGroupsListAadapter(
						act, groupList);
				lvGroups.setAdapter(adap);
				deleteGroups.setEnabled(false);
			}
		});
		findViewById(R.id.btnCreateNewGroup).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						CreateGroup.ACTION_MODE=CreateGroup.CONSTANT_CREATE_GROUP;
						Intent intent = new Intent(
								getApplicationContext(),
								com.foursquare.android.masscheckin.CreateGroup.class);
						startActivity(intent);

					}
				});

		loadGroups();

		findViewById(R.id.btnDeleteDB).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						deleteDatabase("GroupDB");
						lvGroups.setAdapter(null);
					}
				});
		
		findViewById(R.id.btnEditGroups).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(act,EditGroups.class);
				navDrawerLayout.closeDrawers();
				startActivity(intent);
				
			}
		});
	}

	private void loadGroups() {

		SQLiteGroups sql = new SQLiteGroups(this);
		groupList = new ArrayList();
		for (Group g : sql.getAllGroups()) {
			groupList.add(g);
		}
		CustomGroupsListAadapter adap = new CustomGroupsListAadapter(this,
				groupList);
		lvGroups.setAdapter(adap);
		adap.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		android.os.Process.killProcess(android.os.Process.myPid());
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
					Intent intent = new Intent(
							getApplicationContext(),
							com.foursquare.android.masscheckin.CheckInActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					navDrawerLayout.closeDrawers();
					startActivity(intent);
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
