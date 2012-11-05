package org.belligero.nautilus.life.logger.ojects;

public class EventType {
	public static final String
		ID = "id",
		NAME = "type_name",
		ACTIVE = "active";
	
	protected long _id;
	protected boolean _isActive;
	protected String _name;
	
	public EventType() {}
	
	public EventType(long id, int active, String name) {
		_id = id;
		_isActive = (active == 1);
		_name = name;
	}
	
	public EventType(long id, boolean isActive, String name) {
		_id = id;
		_isActive = isActive;
		_name = name;
	}

	public long getID() {
		return _id;
	}

	public EventType setID(long id) {
		this._id = id;
		return this;
	}

	public boolean isActive() {
		return _isActive;
	}

	public EventType setActive(boolean active) {
		this._isActive = active;
		return this;
	}

	public String getName() {
		return _name;
	}

	public EventType setName(String name) {
		this._name = name;
		return this;
	}
}
