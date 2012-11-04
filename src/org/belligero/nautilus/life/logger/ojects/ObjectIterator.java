package org.belligero.nautilus.life.logger.ojects;


import java.util.Iterator;

import android.database.Cursor;

public abstract class ObjectIterator<E> implements Iterable<E>, Iterator<E> {
	protected Cursor _cursor;
	
	public ObjectIterator(Cursor cursor) {
		if (cursor != null) {
			_cursor = cursor;
			_cursor.moveToFirst();
		}
	}

	public boolean hasNext() {
		boolean ret = !(
					_cursor == null
					|| _cursor.isAfterLast()
					|| _cursor.isLast()
				)
				|| _cursor.isClosed();
		if (!ret && !_cursor.isClosed()) {
			_cursor.close();
		}
		
		return ret;
	}

	public Iterator<E> iterator() {
		return this;
	}
	
	public void remove() {}
	
	
	public abstract E next();
}
