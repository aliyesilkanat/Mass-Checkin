package com.foursquare.android.masscheckin.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteFriends extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "GroupDB";
	private static final String TABLE_FRIENDS = "friends";

	private static final String KEY_ID = "_id"; // DB Key degil, FSQ Key
	private static final String KEY_FIRST_NAME = "firstName";
	private static final String KEY_LAST_NAME = "lastName";
	private static final String KEY_PHOTO_PREFIX = "photoPrefix;";
	private static final String KEY_PHOTO_SUFFIX = "photoSuffix;";
	private static final String KEY_HAS_PHOTO = "hasPhoto";
	private static final String[] COLUMNS = { KEY_ID, KEY_FIRST_NAME,
			KEY_LAST_NAME, KEY_PHOTO_PREFIX, KEY_PHOTO_SUFFIX, KEY_HAS_PHOTO };

	public SQLiteFriends(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("CreateFriend", "Before Table Friend ");
		String CREATE_FRIEND_TABLE = "CREATE TABLE friends ( " + "_id TEXT, "
				+ "firstName TEXT, " + "lastName TEXT, " + "photoPrefix TEXT, "
				+ "photoSuffix TEXT, " + "hasPhoto INTEGER )";
		db.execSQL(CREATE_FRIEND_TABLE);
		Log.d("Create Friend", "After Table Friend ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS friends");
		this.onCreate(db);
	}

	public void addFriend(Friends friend) {
		Log.d("addFriends", friend.firstName + " " + friend.LastName);

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// Daha once olusturulan friend mevcut mu sorgusu
		Cursor cursor = db.query(TABLE_FRIENDS, // a. table
				COLUMNS, // b. column names
				"_id = ?", // c. selections
				new String[] { friend.id }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();
		else { // daha once bu id'de (fsq id) olusturulan friend yoksa yeni ekle
				// 2. create ContentValues to add key "column"/value
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
		}
		db.close();
	}

	public Friends getFriend(String id) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. build query
		Cursor cursor = db.query(TABLE_FRIENDS, // a. table
				COLUMNS, // b. column names
				" id = ?", // c. selections
				new String[] { id }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

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
