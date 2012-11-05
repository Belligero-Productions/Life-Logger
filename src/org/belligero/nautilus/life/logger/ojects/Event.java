package org.belligero.nautilus.life.logger.ojects;

public class Event {
	public static final String
		TIME = "event_time",
		EVENT_TYPE = "event_type";
	
	protected long _eventTypeID;
	protected long _timeStamp;
	
	public Event() {}
	
	public Event( EventType eventType, long timeStamp ) {
		_eventTypeID = eventType.getID();
		_timeStamp = timeStamp;
	}
	
	public Event( long eventTypeID, long timeStamp ) {
		_eventTypeID = eventTypeID;
		_timeStamp = timeStamp;
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
