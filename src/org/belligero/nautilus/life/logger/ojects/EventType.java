package org.belligero.nautilus.life.logger.ojects;

public class EventType {
	protected int _id;
	protected boolean _isActive;
	protected String _name;
	
	public EventType() {}
	
	public EventType(int id, int active, String name) {
		_id = id;
		_isActive = (active == 1);
		_name = name;
	}
	
	public EventType(int id, boolean isActive, String name) {
		_id = id;
		_isActive = isActive;
		_name = name;
	}

	public int getID() {
		return _id;
	}

	public void setID(int id) {
		this._id = id;
	}

	public boolean isActive() {
		return _isActive;
	}

	public void setActive(boolean active) {
		this._isActive = active;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}
}
