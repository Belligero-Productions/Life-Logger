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
		
		this.moveToNext();
		return new Event(
				this.getInt( Event.EVENT_TYPE ),
				this.getLong( Event.TIME )
			);
	}
}
