package com.foursquare.android.masscheckin;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.foursquare.android.masscheckin.classes.CustomEditGroupListAdapter;

public class EditGroups extends Activity {
	private ListView lvEditGroups;
	private ImageButton imgDeleteGroup;
	public static Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_groups);
		act = this;
		lvEditGroups = (ListView) findViewById(R.id.lvEditGroups);
		CustomEditGroupListAdapter adapter = new CustomEditGroupListAdapter(
				getApplicationContext(), ArrangeGroups.groupList);
		lvEditGroups.setAdapter(adapter);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_groups, menu);
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
