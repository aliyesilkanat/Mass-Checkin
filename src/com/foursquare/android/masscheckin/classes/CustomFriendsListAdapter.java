package com.foursquare.android.masscheckin.classes;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.foursquare.android.masscheckin.R;

public class CustomFriendsListAdapter extends BaseAdapter {
	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
	}

	private Context context;
	private List<Friends> friendList;

	public CustomFriendsListAdapter(Context context, List<Friends> friendList) {
		this.context = context;
		this.friendList = friendList;
	}

	@Override
	public int getCount() {
		return friendList.size();
	}

	@Override
	public Object getItem(int position) {
		return friendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.friends_list_item, null);

			//
			// CheckBox check = ((CheckBox)
			// convertView.findViewById(R.id.checkTagFriendForGroup));
			// check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//
			// @Override
			// public void onCheckedChanged(CompoundButton buttonView, boolean
			// isChecked) {
			// // TODO Auto-generated method stub
			// int getPosition = (Integer) buttonView.getTag(); // Here we get
			// the position that we have set for the checkbox using setTag.
			// CreateGroup.listFriends.get(getPosition).isSelected=buttonView.isChecked();
			// // Set the value of checkbox to maintain its state.
			// }
			// });
			//
			// if(CreateGroup.listFriends.get(position).isSelected)
			// {
			//
			// check.setChecked(true);
			// }
			// else{
			//
			// check.setChecked(false);
			// }
			// check.setTag(position);
			// }
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.friend_name);
			viewHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.checkTagFriendForGroup);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							int getPosition = (Integer) buttonView.getTag(); // Here
																				// we
																				// get
																				// the
																				// position
																				// that
																				// we
																				// have
																				// set
																				// for
																				// the
																				// checkbox
																				// using
																				// setTag.
							friendList.get(getPosition).isSelected = buttonView
									.isChecked(); // Set the value of checkbox
													// to maintain its state.
						}
					});
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.friend_name, viewHolder.text);
			convertView
					.setTag(R.id.checkTagFriendForGroup, viewHolder.checkbox);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		convertView.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (friendList.get(position).isSelected)
							friendList.get(position).isSelected = false;
						else
							friendList.get(position).isSelected = true;
						notifyDataSetChanged();
					}
				});

		viewHolder.checkbox.setTag(position); // This line is important.

		viewHolder.text.setText(friendList.get(position).firstName + " "
				+ friendList.get(position).LastName);
		viewHolder.checkbox.setChecked(friendList.get(position).isSelected);

		this.notifyDataSetChanged();

		return convertView;
	}

}