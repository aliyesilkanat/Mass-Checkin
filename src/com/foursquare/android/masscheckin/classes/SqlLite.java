package com.foursquare.android.masscheckin.classes;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlLite extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "GroupDB";

	// Books table name
	private static final String TABLE_GROUPS = "groups";

	// Books Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_FRIENDS = "friends";

	private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_FRIENDS };

	public SqlLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
		String CREATE_GROUP_TABLE = "CREATE TABLE groups ( "
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
				+ "friends TEXT )";

		// create books table
		db.execSQL(CREATE_GROUP_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS groups");

		// create fresh books table
		this.onCreate(db);
	}

	public void addGroup(Group group) {
		Log.d("addGroup", group.name);

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, group.name);

		String strFriends = "";
		for (Friends friend : group.friendList) {
			strFriends += friend.id + "#";
			
			//FRIENDLER veritabanina eklenmeli
		}
		strFriends = strFriends.substring(0, strFriends.length() - 1);
		Log.d("addGroup", strFriends);
		values.put(KEY_FRIENDS, strFriends);

		// 3. insert
		db.insert(TABLE_GROUPS, // table
				null, // nullColumnHack
				values); // key/value -> keys = column names/ values = column
							// values

		// 4. close
		db.close();
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
		Log.d("getGroup",cursor.getString(2));
		String[] strFriendsIds = cursor.getString(2).split("#");
		for (String string : strFriendsIds) {
			Friends f = new Friends();
			// FRIENDS ICIN TABLO YAPILMALI
			f.id = string;
			group.friendList.add(f);
		}
		// log
		Log.d("getGroup(" + id + ")", group.name);
		return group;
	}
}
