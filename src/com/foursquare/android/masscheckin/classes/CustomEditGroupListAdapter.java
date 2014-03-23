package com.foursquare.android.masscheckin.classes;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.foursquare.android.masscheckin.CreateGroup;
import com.foursquare.android.masscheckin.EditGroups;
import com.foursquare.android.masscheckin.R;

public class CustomEditGroupListAdapter extends BaseAdapter {
	static class ViewHolder {
		protected ImageButton image;
		protected TextView text;
	}

	private SQLiteGroups sqlGroup;
	private Context context;
	private List<Group> groupsList;

	public CustomEditGroupListAdapter(Context context, List<Group> groupsList) {
		this.context = context;
		this.groupsList = groupsList;
		sqlGroup = new SQLiteGroups(context);
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
			convertView = mInflater.inflate(R.layout.editgroup_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.edited_group_name);
			viewHolder.image = (ImageButton) convertView
					.findViewById(R.id.imgDeleteGroup);
			viewHolder.image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							EditGroups.act);

					builder.setMessage(
							"Are you sure you want to delete this group")
							.setTitle(
									groupsList.get(position).name
											+ " ("
											+ groupsList.get(position).friendList
													.size() + " friends)");
					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									sqlGroup.deleteGroup(groupsList
											.get(position));
									groupsList.remove(position);
									CustomEditGroupListAdapter.this
											.notifyDataSetChanged();

								}
							});
					builder.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();

				}
			});
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.edited_group_name, viewHolder.text);
			convertView.setTag(R.id.imgDeleteGroup, viewHolder.image);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CreateGroup.class);
				CreateGroup.ACTION_MODE = CreateGroup.CONSTANT_EDIT_GROUP;
				
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				CreateGroup.editingGroup=groupsList.get(position);
				context.startActivity(intent);
				
			}
		});
		// viewHolder.checkbox.setTag(position); // This line is important.

		viewHolder.text.setText(groupsList.get(position).name + " ("
				+ groupsList.get(position).friendList.size() + " friends)");
		// viewHolder.checkbox
		// .setChecked(groupsList.get(position).isSelectedToDeletion);

		this.notifyDataSetChanged();

		return convertView;
	}
}