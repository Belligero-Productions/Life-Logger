package org.belligero.nautilus.life.logger.views;

import java.util.Calendar;

import org.belligero.nautilus.life.logger.R;
import org.belligero.nautilus.life.logger.ojects.Event;
import org.belligero.nautilus.life.logger.ojects.EventType;
import org.belligero.nautilus.life.logger.utils.Utils;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventListRowView extends RelativeLayout {
	private Event _event;
	private EventType _eventType;
	
	public EventListRowView( Context context, Event event, EventType eventType ) {
		super( context );
		addView(
				View.inflate(
						context,
						R.layout.view_event_list_row,
						null
					)
			);
		
		_event = event;
		_eventType = eventType;
		
		// Get our controls
		TextView text_eventName = (TextView) findViewById( R.id.text_eventName );
		TextView text_eventTime = (TextView) findViewById( R.id.text_eventTime );
		TextView text_eventDate = (TextView) findViewById( R.id.text_eventDate );
		
		// Set their values
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( event.getTimeStamp() * 1000 );
		
		text_eventName.setText( eventType.getName() );
		text_eventTime.setText( Utils.getTimeString( cal ) );
		text_eventDate.setText( Utils.getDateString( cal ) );
	}

	public Event getEvent() {
		return _event;
	}
	
	public EventType getEventType() {
		return _eventType;
	}
}
