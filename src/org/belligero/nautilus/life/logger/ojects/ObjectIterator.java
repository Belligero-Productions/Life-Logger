package org.belligero.nautilus.life.logger.ojects;


import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.TargetApi;
import android.database.Cursor;

public abstract class ObjectIterator<E> implements Iterable<E>, Iterator<E> {
	private Cursor _cursor;
	
	/********************************** Life cycle Functions *******************************************/
	public ObjectIterator(Cursor cursor) {
		if (cursor != null) {
			_cursor = cursor;
			_cursor.moveToPosition( -1 ); // Move to -1 so that moveToNext() doesn't skip the first one
		}
	}

	
	/********************************** Iterator Function **********************************************/
	public boolean hasNext() {
		boolean ret = !(
					_cursor == null
					|| _cursor.isAfterLast()
					|| _cursor.isLast()
					|| _cursor.getCount() == 0
				)
				|| _cursor.isClosed();
		if (!ret && !_cursor.isClosed()) {
			_cursor.close();
		}
		
		return ret;
	}
	
	public void remove() {
		
	}

	public Iterator<E> iterator() {
		return this;
	}

	/********************************** Other Public Functions *****************************************/
	public int count() {
		return _cursor.getCount();
	}
	
	public void reset() {
		_cursor.moveToPosition( -1 );
	}
	
	public ArrayList<E> toArrayList() {
		ArrayList<E> list = new ArrayList<E>( this.count() );
		
		_cursor.moveToPosition( -1 );
		for ( E obj : this ) {
			list.add( obj );
		}
		
		return list;
	}
	
	/********************************** Abstract Functions *********************************************/
	public abstract E next();
	
	
	/********************************** Protected Functions ********************************************/
	/**
	 * Move the internal pointer to the next element.
	 * 
	 * Wrapper for the Cursor.moveToNext() function, to hide the Cursor object
	 * 
	 * @return true if there are more records, false if there are not.
	 */
	protected boolean moveToNext() {
		return _cursor.moveToNext();
	}
	
	/**
	 * Get a column value without specifying a type.
	 * You will need to know what type it is to use it in code, however.
	 * 
	 * @param columnName The String name of the column to retrieve.
	 * @return The value for the given column.
	 */
	@TargetApi( 11 )
	protected Object get(String columnName) {
		int columnIndex = _cursor.getColumnIndex( columnName );
		
		switch (_cursor.getType( columnIndex )) {			
		case Cursor.FIELD_TYPE_INTEGER:
			return Integer.valueOf( _cursor.getInt( columnIndex ) );
			
		case Cursor.FIELD_TYPE_FLOAT:
			return Float.valueOf( _cursor.getFloat( columnIndex ) );
			
		case Cursor.FIELD_TYPE_STRING:
			return _cursor.getString( columnIndex );
		
		case Cursor.FIELD_TYPE_BLOB:
			return _cursor.getBlob( columnIndex );

		case Cursor.FIELD_TYPE_NULL:
		default:
			return null;	
		}
	}
	
	protected int getInt(String columnName) {
		return _cursor.getInt( _cursor.getColumnIndex( columnName ) );
	}
	
	protected long getLong(String columnName) {
		return _cursor.getLong( _cursor.getColumnIndex( columnName ) );
	}
	
	protected String getString(String columnName) {
		return _cursor.getString( _cursor.getColumnIndex( columnName ) );
	}
	
	protected float getFloat(String columnName) {
		return _cursor.getFloat( _cursor.getColumnIndex( columnName ) );
	}
	
	protected double getDouble(String columnName) {
		return _cursor.getDouble( _cursor.getColumnIndex( columnName ) );
	}
	
	protected byte[] getBlob(String columnName) {
		return _cursor.getBlob( _cursor.getColumnIndex( columnName ) );
	}
}
