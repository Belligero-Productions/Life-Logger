package org.belligero.nautilus.life.logger.ojects;

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
				this.getLong( Event.ID ),
				this.getLong( Event.EVENT_TYPE ),
				this.getLong( Event.TIME )
			);
	}
}
