package org.belligero.nautilus.life.logger.ojects;

import java.util.Iterator;

import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;

import android.database.Cursor;

public class EventIterator extends ObjectIterator<Event> {

	public EventIterator(Cursor cursor) {
		super(cursor);
	}

	@Override
	public Event next() {
		if (!this.hasNext()) return null;
		
		_cursor.moveToNext();
		return new Event(
				_cursor.getInt(_cursor.getColumnIndex(DatabaseAdapter.KEY_EVENT_TYPE)),
				_cursor.getLong(_cursor.getColumnIndex(DatabaseAdapter.KEY_EVENT_TIME))
			);
	}
}
