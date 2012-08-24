package org.belligero.nautilus.life.logger.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapter {
	public static final String KEY_ID = "id",
							KEY_TYPE_NAME = "type_name",
							KEY_ACTIVE = "active",
							KEY_EVENT_TIME = "event_time",
							KEY_EVENT_TYPE = "event_type";
	public static final int RECENT_EVENT_COUNT = 50;
	
	private SQLiteDatabase _dbConnection;
	private DatabaseHelper _dbHelper;
	private final Context _context;
	
	private static final String CREATE_TABLE_EVENT_TYPE = "CREATE TABLE event_type (id integer primary key, type_name varchar(45), active integer)",
								CREATE_TABLE_EVENT = "CREATE TABLE event (id integer primary key, event_time integer, event_type integer)",
								DATABASE_NAME = "data";
	private static final String TABLE_EVENT_TYPE = "event_type",
								TABLE_EVENT = "event";
	private static final int DATABASE_VERSION = 1;
	
	/***********************************************************************************/
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_EVENT);
			db.execSQL(CREATE_TABLE_EVENT_TYPE);
			/*
			String sql = "INSERT INTO event_type(id, type_name, active) VALUES" +
					"(1, 'Drank Coffee', 1)," +
					"(2, 'Drank Tea', 1)," +
					"(3, 'Took pills', 1)," +
					"(4, 'Exercised', 1)," +
					"(5, 'Slept', 1)";
			*/
			String sql = "INSERT INTO event_type(id, type_name, active) VALUES (1, 'Drank Coffee', 1)";
			db.execSQL(sql);
			sql = "INSERT INTO event_type(id, type_name, active) VALUES (2, 'Drank Tea', 1)";
			db.execSQL(sql);
			sql = "INSERT INTO event_type(id, type_name, active) VALUES (3, 'Took Pills', 1)";
			db.execSQL(sql);
			sql = "INSERT INTO event_type(id, type_name, active) VALUES (4, 'Exercised', 1)";
			db.execSQL(sql);
			sql = "INSERT INTO event_type(id, type_name, active) VALUES (5, 'Slept', 1)";
			db.execSQL(sql);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
	
	public DatabaseAdapter(Context context) {
		_context = context;
	}
	
	public DatabaseAdapter open() throws SQLException {
		_dbHelper = new DatabaseHelper(_context);
		_dbConnection = _dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		_dbHelper.close();
	}
	
	public long editEventType(int id, String name, boolean active) {
		ContentValues values = new ContentValues();
		values.put(KEY_TYPE_NAME, name);
		values.put(KEY_ACTIVE, (active ? "1" : "0"));
		return _dbConnection.update(TABLE_EVENT_TYPE, values, KEY_ID+"="+id, null);
	}
	
	public long insertEvent(int eventType, int time) {
		ContentValues values = new ContentValues();
		values.put(KEY_EVENT_TIME, time);
		values.put(KEY_EVENT_TYPE, eventType);
		return _dbConnection.insert(TABLE_EVENT, null, values);
	}
	
	public Cursor fetchEventTypes() {
		String[] columns = new String[]{KEY_ID, KEY_TYPE_NAME, KEY_ACTIVE}; 
		Cursor cursor = 
			_dbConnection.query(TABLE_EVENT_TYPE, columns, null, null, null, null, KEY_ID);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor fetchRecentEvents() {
		String[] columns = new String[]{KEY_EVENT_TIME, KEY_EVENT_TYPE};
		Cursor cursor =
			_dbConnection.query(TABLE_EVENT, columns, null, null, null, null, KEY_EVENT_TIME+" DESC", ""+RECENT_EVENT_COUNT);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor fetchRecentEvents(int eventType) {
		String[] columns = new String[]{KEY_EVENT_TIME, KEY_EVENT_TYPE};
		Cursor cursor =
			_dbConnection.query(TABLE_EVENT, columns, KEY_EVENT_TYPE+"=?", new String[]{""+eventType}, null, null, KEY_EVENT_TIME+" DESC", ""+RECENT_EVENT_COUNT);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor fetchAllEvents() {
		String[] columns = new String[]{KEY_EVENT_TIME, KEY_EVENT_TYPE};
		Cursor cursor =
			_dbConnection.query(TABLE_EVENT, columns, null, null, null, null, KEY_EVENT_TIME);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor fetchAllEvents(int eventType) {
		String[] columns = new String[]{KEY_EVENT_TIME, KEY_EVENT_TYPE};
		Cursor cursor =
			_dbConnection.query(TABLE_EVENT, columns, KEY_EVENT_TYPE+"=?", new String[]{""+eventType}, null, null, KEY_EVENT_TIME);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor fetchAllEvents(String eventTypeName) {
		String[] columns = new String[]{KEY_ID};
		Cursor cursor =
			_dbConnection.query(TABLE_EVENT_TYPE, columns, KEY_TYPE_NAME+"=?", new String[]{eventTypeName}, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int eventType = cursor.getInt(cursor.getColumnIndex(KEY_ID)); 
			return fetchAllEvents(eventType);
		}
		return null;
	}
}
