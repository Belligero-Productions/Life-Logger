package org.belligero.nautilus.life.logger.ojects;

import java.util.Iterator;

import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;

import android.database.Cursor;

public class EventTypeIterator extends ObjectIterator<EventType> {
	
	public EventTypeIterator(Cursor cursor) {
		super(cursor);
	}

	public EventType next() {
		if (!this.hasNext()) return null;
		
		_cursor.moveToNext();
		
		return new EventType(
				_cursor.getInt(_cursor.getColumnIndex(DatabaseAdapter.KEY_ID)),
				_cursor.getInt(_cursor.getColumnIndex(DatabaseAdapter.KEY_ACTIVE)),
				_cursor.getString(_cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE_NAME))
			);
	}
}
