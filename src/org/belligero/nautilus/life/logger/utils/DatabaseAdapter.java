package org.belligero.nautilus.life.logger.utils;

import org.belligero.nautilus.life.logger.ojects.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapter {
	public static final int RECENT_EVENT_COUNT = 50;
	public final EventHandler eventHandler;
	public final EventTypeHandler eventTypeHandler;
	
	private SQLiteDatabase _dbConnection;
	private DatabaseHelper _dbHelper;
	private final Context _context;
	
	private static final String
		CREATE_TABLE_EVENT_TYPE = "CREATE TABLE event_type (id integer primary key, type_name varchar(45), active integer)",
		CREATE_TABLE_EVENT = "CREATE TABLE event (id integer primary key, event_time integer, event_type integer)",
		DATABASE_NAME = "data";
	
	private static final String
		TABLE_EVENT_TYPE = "event_type",
		TABLE_EVENT = "event";
	
	private static final int
		DATABASE_VERSION = 1;
	
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
			if (oldVersion < 2) {
				// Do stuff for going from 1 to 2
			}
		}
	}
	
	public DatabaseAdapter(Context context) {
		_context = context;
		this.open();
		
		this.eventHandler = new EventHandler();
		this.eventTypeHandler = new EventTypeHandler();
	}
	
	public DatabaseAdapter open() throws SQLException {
		_dbHelper = new DatabaseHelper(_context);
		_dbConnection = _dbHelper.getWritableDatabase();
		
		return this;
	}
	
	public void close() {
		_dbHelper.close();
	}
	
	public class EventTypeHandler {
		/********************************** Insert, Update, Delete *****************************************/
		public EventType insert( EventType eventType ) {
			ContentValues values = new ContentValues();
			values.put(
					EventType.NAME,
					eventType.getName()
				);
			values.put(
					EventType.ACTIVE,
					( eventType.isActive() ? 1 : 0 )
				);
			
			long id = _dbConnection.insert(
					TABLE_EVENT_TYPE,
					null,
					values
				);
			
			if ( id < 0 ) {
				throw new RuntimeException( "Error occured inserting new EventType" );
			}
			
			return eventType.setID( id );
		}
		
		public long update( EventType eventType ) {
			ContentValues values = new ContentValues();
			values.put(
					EventType.NAME,
					eventType.getName()
				);
			values.put(
					EventType.ACTIVE,
					(eventType.isActive() ? 1 : 0)
				);
			
			return _dbConnection.update(
					TABLE_EVENT_TYPE,
					values,
					EventType.ID + "=" + eventType.getID(),
					null
				);
		}
		
		public long delete( EventType eventType ) {
			eventHandler.deleteByType( eventType.getID() );
			
			return _dbConnection.delete(
					TABLE_EVENT_TYPE,
					EventType.ID + " = ?",
					new String[] {
							Long.toString( eventType.getID() )
						}
				);
		}
		
		/********************************** Select, Retrieve ***********************************************/
		public EventTypeIterator fetchAll() {
			String[] columns = new String[]{
					EventType.ID,
					EventType.NAME,
					EventType.ACTIVE
				};
			
			return new EventTypeIterator( 
					_dbConnection.query(
							TABLE_EVENT_TYPE,
							columns,
							null,
							null,
							null,
							null,
							EventType.ID
						)
				);
		}
		
		public EventType fetchByID( long eventTypeID ) {
			String[] columns = new String[]{
					EventType.ID,
					EventType.NAME,
					EventType.ACTIVE
				};
			
			return new EventTypeIterator(
					_dbConnection.query(
							TABLE_EVENT_TYPE,
							columns,
							EventType.ID + " = ?",
							new String[] {
								Long.toString( eventTypeID )	
							},
							null,
							null,
							null,
							"1"
						)
				).next();
		}
	} // EventTypeHandler
	
	public class EventHandler {
		/********************************** Insert, Update, Delete *****************************************/
		public Event insert( Event event ) {
			ContentValues values = new ContentValues();
			values.put(
					Event.TIME,
					event.getTimeStamp()
				);
			values.put(
					Event.EVENT_TYPE,
					event.getEventTypeID()
				);
			
			long id = _dbConnection.insert(
					TABLE_EVENT,
					null,
					values
				);
			
			if ( id < 0 ) {
				throw new RuntimeException( "Error occured inserting new Event" );
			}
			
			return event.setID( id );
		}
		
		public long deleteByType( long eventTypeID ) {
			return _dbConnection.delete(
					TABLE_EVENT,
					Event.EVENT_TYPE + " = ?",
					new String[] {
							Long.toString( eventTypeID )
						}
				);
		}
		
		public long delete( Event event ) {
			String whereClause =
					Event.ID + " = ?";
			
			return _dbConnection.delete(
					TABLE_EVENT,
					whereClause,
					new String[] {
							Long.toString( event.getID() )
						}
				);
		}
		
		/********************************** Select, Retrieve ***********************************************/
		public EventIterator fetchAll() {
			String[] columns = new String[]{
					Event.ID,
					Event.TIME,
					Event.EVENT_TYPE
				};
			
			return new EventIterator(
					_dbConnection.query(
							TABLE_EVENT,
							columns,
							null,
							null,
							null,
							null,
							Event.TIME
						)
				);
		}
		
		public EventIterator fetchByType( int eventTypeID ) {
			String[] columns = new String[]{
					Event.ID,
					Event.TIME,
					Event.EVENT_TYPE
				};
			
			return new EventIterator(
					_dbConnection.query(
							TABLE_EVENT,
							columns,
							Event.EVENT_TYPE + " = ?",
							new String[]{
									Integer.toString( eventTypeID )
								},
							null,
							null,
							Event.TIME
						)
				);
		}
		
		public EventIterator fetchByType( String eventTypeName ) {
			String[] columns = new String[]{
					EventType.ID
				};
			
			Cursor cursor =
				_dbConnection.query(
						TABLE_EVENT_TYPE,
						columns,
						EventType.NAME + "=?",
						new String[]{
								eventTypeName
							},
						null,
						null,
						null
					);
			
			if (cursor != null) {
				cursor.moveToFirst();
				
				int eventTypeID = cursor.getInt(cursor.getColumnIndex(EventType.ID));
				
				return fetchByType(eventTypeID);
			} else return new EventIterator(null);
		}
		
		public EventIterator fetchRecent() {
			String[] columns = new String[]{
					Event.ID,
					Event.TIME,
					Event.EVENT_TYPE
				};
			
			return new EventIterator(
					_dbConnection.query(
							TABLE_EVENT,
							columns,
							null,
							null,
							null,
							null,
							Event.TIME + " DESC",
							Integer.toString( RECENT_EVENT_COUNT )
						)
				);
		}
		
		public EventIterator fetchRecent( long eventTypeID ) {
			String[] columns = new String[]{
					Event.ID,
					Event.TIME,
					Event.EVENT_TYPE
				};
			
			return new EventIterator(
					_dbConnection.query(
							TABLE_EVENT,
							columns,
							Event.EVENT_TYPE + "=?",
							new String[]{
									Long.toString( eventTypeID )
								},
							null,
							null,
							Event.TIME + " DESC",
							Integer.toString( RECENT_EVENT_COUNT )
						)
				);
		}
	} // EventHandler
} // DatabaseAdapter
