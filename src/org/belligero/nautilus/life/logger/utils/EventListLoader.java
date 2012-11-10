package org.belligero.nautilus.life.logger.utils;

import java.util.Calendar;
import java.util.HashMap;

import org.belligero.nautilus.life.logger.R;
import org.belligero.nautilus.life.logger.ojects.*;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class EventListLoader {	
	private static final int
		ROW_VIEW_RESOURCE_ID = R.layout.view_event_list_row;
	
	private DatabaseAdapter _dbHelper;
	private Context _context;
	private ViewGroup _container;
	
	public EventListLoader( Context context, ViewGroup containerView ) {
		super();
		_context = context;
		
		_dbHelper = new DatabaseAdapter( context );
		_container = containerView;
	}

	public void populateList( EventIterator eventIterator ) {
		populateList( eventIterator, null, null );
	}
	
	public void populateList( EventIterator eventIterator, OnClickListener onClickListener, OnLongClickListener onLongClickListener ) {
		// TODO make this load in a separate thread
		eventIterator.reset();
		_container.removeAllViews();
		
		// Get a ID-eventType mapping
		EventTypeIterator typeIterator = _dbHelper.eventTypeHandler.fetchAllEventTypes();
		HashMap<Long, EventType> typeMapping
				= new HashMap<Long, EventType>( typeIterator.count() );
		
		for ( EventType eventType : typeIterator ) {
			typeMapping.put( eventType.getID(), eventType );
		}
		
		// Actually add the views
		View rowView;
		
		for ( Event event : eventIterator ) {
			rowView = addRow( event, typeMapping.get( event.getEventTypeID() ), false );
//			rowView.setOnClickListener( onClickListener );
//			rowView.setOnLongClickListener( onLongClickListener );
		}
	}
	
	private View _rowView;
	
	public View addRow( Event event, EventType eventType, boolean toStart ) {
		_rowView = getRow( event, eventType );
		Activity parent = (Activity) _context;
		
		if ( toStart ) {
			_container.addView( _rowView, 0 );
		} else {
			_container.addView( _rowView );
		}
		
		parent.registerForContextMenu( _rowView );
		
		return _rowView;
	}
	
	public void removeRow( Event event ) {

		// TODO If possible, put this in a thread
		this.removeRow( _container.findViewWithTag( event ) );
	}
	
	public void removeRow( View rowView ) {
		if ( rowView != null ) _container.removeView( rowView );
	}
	
	private View getRow( Event event, EventType eventType ) {
		View rowView = View.inflate( _context, ROW_VIEW_RESOURCE_ID, null );
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( event.getTimeStamp() * 1000 );
		
		TextView text_name = (TextView) rowView.findViewById( R.id.text_eventName );
		TextView text_date = (TextView) rowView.findViewById( R.id.text_eventDate );
		TextView text_time = (TextView) rowView.findViewById( R.id.text_eventTime );
		
		text_name.setText( eventType.getName() );
		text_date.setText( Utils.getDateString( cal ) );
		text_time.setText( Utils.getTimeString( cal ) );
		
		rowView.setTag( event );
		
		return rowView;
	}
}
