package com.foursquare.android.masscheckin.classes;

import java.util.LinkedList;
import java.util.List;

import com.foursquare.android.masscheckin.CheckInActivity;
import com.google.android.gms.drive.internal.f;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteGroups extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "GroupDB";
	private static final String TABLE_GROUPS = "groups";

	private static final String KEY_ID = "rowId";
	private static final String KEY_NAME = "name";
	private static final String KEY_FRIENDS = "friends";
	private static Context context;
	private static final String[] COLUMNSGroup = { KEY_ID, KEY_NAME,
			KEY_FRIENDS };

	private static final String TABLE_FRIENDS = "friends";
	private static final String KEY_FIRST_NAME = "firstName";
	private static final String KEY_LAST_NAME = "lastName";
	private static final String KEY_PHOTO_PREFIX = "photoPrefix";
	private static final String KEY_PHOTO_SUFFIX = "photoSuffix";
	private static final String KEY_HAS_PHOTO = "hasPhoto";
	private static final String[] COLUMNSFriends = { KEY_ID, KEY_FIRST_NAME,
			KEY_LAST_NAME, KEY_PHOTO_PREFIX, KEY_PHOTO_SUFFIX, KEY_HAS_PHOTO };

	public SQLiteGroups(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_GROUP_TABLE = "CREATE TABLE groups ( "
				+ "rowId INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
				+ "friends TEXT )";

		db.execSQL(CREATE_GROUP_TABLE);
		String CREATE_FRIEND_TABLE = "CREATE TABLE friends ( " + "rowId TEXT, "
				+ "firstName TEXT, " + "lastName TEXT, " + "photoPrefix TEXT, "
				+ "photoSuffix TEXT, " + "hasPhoto INTEGER )";
		db.execSQL(CREATE_FRIEND_TABLE);
		Log.d("DB", CREATE_GROUP_TABLE);
		Log.d("DB", CREATE_FRIEND_TABLE);
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
			addFriend(friend);
		}
		strFriends = strFriends.substring(0, strFriends.length() - 1);
		Log.d("addGroup", strFriends);
		values.put(KEY_FRIENDS, strFriends);
		db = this.getWritableDatabase();
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
				COLUMNSGroup, // b. column names
				" rowId = ?", // c. selections
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

			group.friendList.add(getFriend(string));
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
					group.friendList.add(getFriend(string));

				}
				groupList.add(group);
				Log.d("getAllBooks()", group.name);
			} while (cursor.moveToNext());
		}
		db.close();
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
			addFriend(friend);
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

	public void addFriend(Friends friend) {
		Log.d("addFriends", friend.firstName + " " + friend.LastName);

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_FRIENDS + " WHERE "
				+ KEY_ID + "=?;";
		Cursor cursor = db.rawQuery(selectQuery, new String[] { friend.id });

		//
		// // Daha once olusturulan friend mevcut mu sorgusu
		// Cursor cursor = db.query(TABLE_FRIENDS, // a. table
		// COLUMNSFriends, // b. column names
		// " rowId = ?", // c. selections
		// new String[] { friend.id }, // d. selections args
		// null, // e. group by
		// null, // f. having
		// null, // g. order by
		// null); // h. limit

		// 3. if we got results get the first one
		if (cursor.getCount() <= 0) {
			// cursor.moveToFirst();
			// else { // daha once bu id'de (fsq id) olusturulan friend yoksa
			// yeni
			// ekle
			// // 2. create ContentValues to add key "column"/value
			db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_ID, friend.id);
			values.put(KEY_FIRST_NAME, friend.firstName);
			values.put(KEY_LAST_NAME, friend.LastName);
			values.put(KEY_PHOTO_PREFIX, friend.photoPrefix);
			values.put(KEY_PHOTO_SUFFIX, friend.photoSuffix);
			values.put(KEY_HAS_PHOTO, friend.hasPhoto ? 1 : 0); // bool - int
																// conversion
			// 3. insert
			db.insert(TABLE_FRIENDS, // table
					null, // nullColumnHack
					values); // key/value -> keys = column names/ values =
								// column
								// values
			Log.d("addFriend", friend.firstName + " " + friend.LastName);
			// 4. close
			// }
			db.close();
		}

	}

	public Friends getFriend(String id) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. build query

		String selectQuery = "SELECT * FROM " + TABLE_FRIENDS + " WHERE "
				+ KEY_ID + "=?;";
		Cursor cursor = db.rawQuery(selectQuery, new String[] { id });

		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();
		Friends friend = new Friends();
		friend.id = cursor.getString(0);
		friend.firstName = cursor.getString(1);
		friend.LastName = cursor.getString(2);
		friend.photoPrefix = cursor.getString(3);
		friend.photoSuffix = cursor.getString(4);
		friend.hasPhoto = cursor.getString(5).equals("1") ? true : false;
		db.close();
		// log
		Log.d("getFriend(" + id + ")", friend.firstName + " " + friend.LastName);
		return friend;
	}

	public int updateFriend(Friends friend) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();

		values.put(KEY_FIRST_NAME, friend.firstName);
		values.put(KEY_LAST_NAME, friend.LastName);
		values.put(KEY_PHOTO_PREFIX, friend.photoPrefix);
		values.put(KEY_PHOTO_SUFFIX, friend.photoSuffix);
		values.put(KEY_HAS_PHOTO, friend.hasPhoto ? 1 : 0); // bool - int
		// conversion
		Log.d("updateFriend", friend.firstName + " " + friend.LastName);
		// 3. updating row
		int i = db.update(TABLE_FRIENDS, // table
				values, // column/value
				KEY_ID + " = ?", // selections
				new String[] { friend.id }); // selection args

		// 4. close
		db.close();

		return i;
	}

	public void deleteFriend(Friends friend) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		// 2. delete
		db.delete(TABLE_FRIENDS, // table name
				KEY_ID + " = ?", // selections
				new String[] { friend.id }); // selections args
		// 3. close
		db.close();
		// log
		Log.d("deleteFriend", friend.firstName + " " + friend.LastName);
	}

}
