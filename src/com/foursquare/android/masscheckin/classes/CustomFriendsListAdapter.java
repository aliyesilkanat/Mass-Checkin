package com.foursquare.android.masscheckin.classes;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foursquare.android.masscheckin.R;
 
public class CustomFriendsListAdapter extends BaseAdapter {
     
    private Context context;
    private List<Friends> friendList;
     
    public CustomFriendsListAdapter(Context context, List<Friends> friendList){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.friends_list_item, null);
        }
          
//        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.friend_image);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.friend_name);
  
          
//        imgIcon.setImageResource(friendList.get(position).getIcon());        
        txtTitle.setText(friendList.get(position).firstName+ " "+friendList.get(position).LastName );
         
        // displaying count
        // check whether it set visible or not
      
         
        return convertView;
    }
 
}