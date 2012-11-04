package org.belligero.nautilus.life.logger.ojects;

public class Event {
	public static final String
		TIME = "event_time",
		EVENT_TYPE = "event_type";
	
	protected int _eventTypeID;
	protected long _timeStamp;
	
	public Event() {}
	
	public Event(EventType eventType, long timeStamp) {
		_eventTypeID = eventType.getID();
		_timeStamp = timeStamp;
	}
	
	public Event(int eventTypeID, long timeStamp) {
		_eventTypeID = eventTypeID;
		_timeStamp = timeStamp;
	}

	public int getEventTypeID() {
		return _eventTypeID;
	}

	public void setEventTypeID(int eventTypeID) {
		this._eventTypeID = eventTypeID;
	}

	public long getTimeStamp() {
		return _timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this._timeStamp = timeStamp;
	}
	
	
}
