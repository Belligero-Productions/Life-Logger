package org.belligero.nautilus.life.logger.ojects;

public class Event {
	public static final String
		ID = "id",
		TIME = "event_time",
		EVENT_TYPE = "event_type";
	
	protected long _id;
	protected long _eventTypeID;
	protected long _timeStamp;
	
	public Event() {}
	
	public Event( EventType eventType, long timeStamp ) {
		_eventTypeID = eventType.getID();
		_timeStamp = timeStamp;
	}
	
	public Event( long id, EventType eventType, long timeStamp ) {
		_id = id;
		_eventTypeID = eventType.getID();
		_timeStamp = timeStamp;
	}
	
	public Event( long eventTypeID, long timeStamp ) {
		_eventTypeID = eventTypeID;
		_timeStamp = timeStamp;
	}
	
	public Event( long id, long eventTypeID, long timeStamp ) {
		_id = id;
		_eventTypeID = eventTypeID;
		_timeStamp = timeStamp;
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( obj != null && obj instanceof Event ) {
			Event event = (Event) obj;
			return event.getID() == this.getID()
					&& event.getEventTypeID() == this.getEventTypeID()
					&& event.getTimeStamp() == this.getTimeStamp();
		}
	
		return false;
	}
	
	/**** Getters & Setters ****/
	
	public long getID() {
		return _id;
	}
	
	public Event setID( long id ) {
		_id = id;
		return this;
	}

	public long getEventTypeID() {
		return _eventTypeID;
	}

	public Event setEventTypeID( long eventTypeID ) {
		this._eventTypeID = eventTypeID;
		return this;
	}

	public long getTimeStamp() {
		return _timeStamp;
	}

	public Event setTimeStamp( long timeStamp ) {
		this._timeStamp = timeStamp;
		return this;
	}
	
	
}
