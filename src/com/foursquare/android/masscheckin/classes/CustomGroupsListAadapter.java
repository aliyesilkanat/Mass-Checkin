package com.foursquare.android.masscheckin.classes;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.foursquare.android.masscheckin.ArrangeGroups;
import com.foursquare.android.masscheckin.R;

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
		return groupsList.size();
	}

	@Override
	public Object getItem(int position) {
		return groupsList.get(position);
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
			convertView = mInflater.inflate(R.layout.groups_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.group_name);
			viewHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.checkTagGroupDeletion);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							int getPosition = (Integer) buttonView.getTag();
							groupsList.get(getPosition).isSelectedToDeletion = buttonView
									.isChecked();
							if (ArrangeGroups.act != null) {
								boolean atLeastOneDeletion = false;
								Button btnDelete = (Button) ArrangeGroups.act
										.findViewById(R.id.btnDeleteGroups);
								for (Group group : groupsList) {
									if (group.isSelectedToDeletion) {
										atLeastOneDeletion = true;
										btnDelete.setEnabled(true);
										break;
									}
								}
								if (!atLeastOneDeletion)
									btnDelete.setEnabled(false);
							}
						}
					});
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.group_name, viewHolder.text);
			convertView.setTag(R.id.checkTagGroupDeletion, viewHolder.checkbox);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}
		convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (groupsList.get(position).isSelectedToDeletion)
							groupsList.get(position).isSelectedToDeletion = false;
						else
							groupsList.get(position).isSelectedToDeletion = true;
						notifyDataSetChanged();
					}
				});

		viewHolder.checkbox.setTag(position); // This line is important.

		viewHolder.text.setText(groupsList.get(position).name + " ("
				+ groupsList.get(position).friendList.size() + " friends)");
		viewHolder.checkbox
				.setChecked(groupsList.get(position).isSelectedToDeletion);

		this.notifyDataSetChanged();

		return convertView;
	}
}
