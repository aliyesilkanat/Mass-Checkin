package com.foursquare.android.masscheckin.classes;

import java.util.LinkedList;
import java.util.List;

import com.foursquare.android.masscheckin.CheckInActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteGroups extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "GroupDB";
	private static final String TABLE_GROUPS = "groups";

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_FRIENDS = "friends";

	private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_FRIENDS };
	private SQLiteFriends sqlFriends = new SQLiteFriends(
			CheckInActivity.context);

	public SQLiteGroups(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_GROUP_TABLE = "CREATE TABLE groups ( "
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
				+ "friends TEXT )";

		db.execSQL(CREATE_GROUP_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS groups");

		this.onCreate(db);
	}

	public int addGroup(Group group) {
		Log.d("addGroup", group.name);

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, group.name);

		String strFriends = "";
		for (Friends friend : group.friendList) {
			strFriends += friend.id + "#";
			sqlFriends.addFriend(friend);
		}
		strFriends = strFriends.substring(0, strFriends.length() - 1);
		Log.d("addGroup", strFriends);
		values.put(KEY_FRIENDS, strFriends);

		// 3. insert
		int id = (int) db.insert(TABLE_GROUPS, // table
				null, // nullColumnHack
				values); // key/value -> keys = column names/ values = column
							// values

		// 4. close
		db.close();
		return id;
	}

	public Group getGroup(int id) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. build query
		Cursor cursor = db.query(TABLE_GROUPS, // a. table
				COLUMNS, // b. column names
				" id = ?", // c. selections
				new String[] { String.valueOf(id) }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();
		Group group = new Group();
		group.id = Integer.parseInt(cursor.getString(0));
		group.name = cursor.getString(1);
		Log.d("getGroup", cursor.getString(2));
		String[] strFriendsIds = cursor.getString(2).split("#");
		for (String string : strFriendsIds) {

			group.friendList.add(sqlFriends.getFriend(string));
		}
		// log
		Log.d("getGroup(" + id + ")", group.name);
		return group;
	}

	public List<Group> getAllGroups() {
		List<Group> groupList = new LinkedList<Group>();

		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_GROUPS;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build book and add it to list
		Group group = null;
		if (cursor.moveToFirst()) {
			do {
				group = new Group();
				group.id = Integer.parseInt(cursor.getString(0));
				group.name = cursor.getString(1);
				Log.d("getGroup", cursor.getString(2));
				String[] strFriendsIds = cursor.getString(2).split("#");
				for (String string : strFriendsIds) {
					group.friendList.add(sqlFriends.getFriend(string));

				}
				groupList.add(group);
				Log.d("getAllBooks()", group.name);
			} while (cursor.moveToNext());
		}

		return groupList;
	}

	public int updateGroup(Group group) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, group.name); // get title
		String strFriends = "";
		for (Friends friend : group.friendList) {
			strFriends += friend.id + "#";
			sqlFriends.addFriend(friend);
		}
		strFriends = strFriends.substring(0, strFriends.length() - 1);
		Log.d("updateGroup", strFriends);
		values.put(KEY_FRIENDS, strFriends);
		// 3. updating row
		int i = db.update(TABLE_GROUPS, // table
				values, // column/value
				KEY_ID + " = ?", // selections
				new String[] { String.valueOf(group.id) }); // selection args

		// 4. close
		db.close();

		return i;

	}

	public void deleteGroup(Group group) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		// 2. delete
		db.delete(TABLE_GROUPS, // table name
				KEY_ID + " = ?", // selections
				new String[] { String.valueOf(group.id) }); // selections args
		// 3. close
		db.close();
		// log
		Log.d("deleteGroup", group.name);
	}
}
