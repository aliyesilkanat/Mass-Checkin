package com.foursquare.android.masscheckin.classes;

import java.util.List;

import com.foursquare.android.masscheckin.R;
import com.foursquare.android.masscheckin.classes.CustomFriendsListAdapter.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CustomGroupsListAadapter extends BaseAdapter {
	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
	}

	private Context context;
	private List<Group> groupsList;

	public CustomGroupsListAadapter(Context context, List<Group> groupsList) {
		this.context = context;
		this.groupsList = groupsList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.friends_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.friend_name);
			viewHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.checkTagFriendForGroup);
		}
		return null;
	}

}
