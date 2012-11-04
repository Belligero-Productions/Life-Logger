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
		
		this.moveToNext();
		return new EventType(
				this.getInt( EventType.ID ),
				this.getInt( EventType.ACTIVE ),
				this.getString( EventType.NAME )
			);
	}
}
