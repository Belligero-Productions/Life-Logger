package org.belligero.nautilus.life.logger.utils;

import java.util.ArrayList;

import org.belligero.nautilus.life.logger.R;
import org.belligero.nautilus.life.logger.ojects.*;
import org.belligero.nautilus.life.logger.views.EventListRowView;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class EventListLoader extends BaseAdapter {
	private DatabaseAdapter _dbHelper;
	private Context _context;
	private Event[] _events;
	
	public EventListLoader( Context context, Event[] events ) {
		super();
		
		_context = context;
		_dbHelper = new DatabaseAdapter( context );
		_events = events;
	}

	public View getView( int position, View convertView, ViewGroup parent ) {
		Log.d( "EventListLoader::getView()", "Position: "+position );
		Event event = (Event) this.getItem( position );
		EventType eventType = _dbHelper.eventTypeHandler.fetchEventTypeWithID( event.getEventTypeID() ); // TODO Cache these
		
		View rowView = convertView;
		if (convertView == null) {
			convertView = new EventListRowView(
					_context,
					event,
					eventType
				);
		}
		
		return convertView;
	}

	public int getCount() {
		return _events.length;
	}

	public Object getItem( int position ) {
		return _events[position];
	}

	public long getItemId( int position ) {
		return position;
	}
}
