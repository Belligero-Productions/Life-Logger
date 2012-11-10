package org.belligero.nautilus;

import android.app.Application;
import android.content.Context;

public class LifeLogger extends Application {
	protected static Context _context;
	
	public void onCreate() {
		super.onCreate();
		
		LifeLogger._context = getApplicationContext();
	}
	
	public static Context getContext() {
		return _context;
	}
}
