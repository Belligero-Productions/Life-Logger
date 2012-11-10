package org.belligero.nautilus.life.logger.utils;

import org.belligero.nautilus.life.logger.ojects.Event;
import org.belligero.nautilus.life.logger.ojects.EventType;

import android.view.ContextMenu.ContextMenuInfo;

public class LoggerContextMenuInfo implements ContextMenuInfo {
	protected Event _event;
	protected EventType _eventType;
	
	public LoggerContextMenuInfo() {
		_event = null;
		_eventType = null;
	}
	
	public LoggerContextMenuInfo( Event event, EventType eventType ) {
		_event = event;
		_eventType = eventType;
	}
	
	public Event getEvent() {
		return _event;
	}
	
	public LoggerContextMenuInfo setEvent( Event event ) {
		_event = event;
		return this;
	}
	
	public EventType getEventType() {
		return _eventType;
	}
	
	public LoggerContextMenuInfo setEventType( EventType eventType ) {
		_eventType = eventType;
		return this;
	}
}
