package org.belligero.nautilus.life.logger;

import java.util.Calendar;

import org.belligero.nautilus.LifeLogger;
import org.belligero.nautilus.life.logger.ojects.Event;
import org.belligero.nautilus.life.logger.ojects.EventIterator;
import org.belligero.nautilus.life.logger.ojects.EventType;
import org.belligero.nautilus.life.logger.ojects.EventTypeIterator;
import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.utils.EventListLoader;
import org.belligero.nautilus.life.logger.utils.Utils;
import org.belligero.nautilus.life.logger.views.LogEventLineView;
import org.belligero.nautilus.life.logger.R;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class LifeLoggerActivity extends Activity {
	@SuppressWarnings( "unused" )
	private static final String
		TAG = "LifeLogger";
	private static final int
		DIALOG_ID_DATE = 1,
		DIALOG_ID_TIME = 2;
	
	private Button btn_date,
				btn_time;
	
	private int _year, _month, _day,
				_hour, _minute;
	
	private DatabaseAdapter _dbHelper;
	private EventListLoader _eventListLoader;
	
	private ViewGroup _logButtons;
	
	private static LifeLoggerActivity instance;

	/********************************** Life cycle Functions *******************************************/
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart( this ); 
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop( this );
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_events);
		
		_dbHelper = new DatabaseAdapter(this).open();
		
		_eventListLoader = new EventListLoader(
				this,
				(ViewGroup) findViewById( R.id.container_recent )
			);
		
		// Setup the logging buttons
		_logButtons = (ViewGroup) findViewById(R.id.container_logButtons);
		
		loadData();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setupDateTime();
	}
	
	/*************************************** Public Functions ******************************************/
	public void logEvent( EventType eventType ) {
		Calendar cal = Calendar.getInstance();
		cal.set(_year, _month, _day, _hour, _minute, 0);
		
		Event event = new Event(
				eventType,
				cal.getTimeInMillis() / 1000
			);
		
		_dbHelper.eventHandler.insert( event );
		
		_eventListLoader.addRow( event, eventType, true );
	}
	
	public void showRecent() {
		showRecent(null);
	}
	
	public void showRecent( EventType eventType ) {		
		EventIterator iterator;
		if ( eventType == null ) {
			iterator = _dbHelper.eventHandler.fetchRecent();
		} else {
			iterator = _dbHelper.eventHandler.fetchRecent( eventType.getID() );
		}

		EventListLoader loader
				= new EventListLoader(
						this,
						(ViewGroup) this.findViewById( R.id.container_recent )
					);
		loader.populateList( iterator );
	}
	
	public static void refresh() {
		if ( instance != null ) LifeLoggerActivity.instance.loadData();
	}
	
	/*************************************** Helper Functions ******************************************/
	private void loadData() {
		if (LifeLoggerActivity.instance == null) LifeLoggerActivity.instance = this;
		
		_logButtons.removeAllViews();
		
		// Load the buttons
		EventTypeIterator eventTypeIterator = _dbHelper.eventTypeHandler.fetchAll();
		for (EventType eventType : eventTypeIterator) {
			if (eventType.isActive()) {
				_logButtons.addView(
						new LogEventLineView(
								this,
								eventType
							)
					);
			}
		}
		
		// Load recent logs
		showRecent();
	}
	
	private void setupDateTime() {
		btn_date = (Button)findViewById(R.id.btn_date);
		btn_time = (Button)findViewById(R.id.btn_time);
		
		final Calendar cal = Calendar.getInstance();
		_year = cal.get(Calendar.YEAR);
		_month = cal.get(Calendar.MONTH);
		_day = cal.get(Calendar.DAY_OF_MONTH);
		_hour = cal.get(Calendar.HOUR_OF_DAY);
		_minute = cal.get(Calendar.MINUTE);
		updateDateTime();
		
		btn_date.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_ID_DATE);
			}
		});
		
		btn_time.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_ID_TIME);
			}
		});
	}

	private void updateDateTime() {
		Calendar cal = Calendar.getInstance();
		cal.set( _year, _month, _day, _hour, _minute );
		
		String date = DateUtils.formatDateTime(
				LifeLogger.getContext(),
				cal.getTimeInMillis(),
				DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR
			);
		String time = DateUtils.formatDateTime(
				LifeLogger.getContext(),
				cal.getTimeInMillis(),
				DateUtils.FORMAT_SHOW_TIME
			);
		
		btn_date.setText( date );
		btn_time.setText( time );
	}
	
	private void deleteEvent( Event event ) {
		if ( event != null ) {
			_dbHelper.eventHandler.delete( event );
			
			_eventListLoader.removeRow( event );
		}
	}

	/*************************************** Handlers **************************************************/
	
	/*************************************** Context Menus *********************************************/
	private View _contextMenuView;
	
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		Event event;
		EventType eventType;
		String title = null;
		int menuResourceID = 0;
		
		switch ( v.getId() ) {
		case R.id.view_eventRow:
			event = (Event) v.getTag();
			eventType = (EventType) _dbHelper.eventTypeHandler.fetchByID( event.getEventTypeID() );

			// TODO make a single function to combine these
			title = eventType.getName()
					+ " - " + Utils.getDateString( event.getTimeStamp() )
					+ ", " + Utils.getTimeString( event.getTimeStamp() );
			menu.setHeaderTitle( title );
			
			menuResourceID = R.menu.menu_event;
			
			break;
		}
		
		
		super.onCreateContextMenu( menu, v, menuInfo );
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( menuResourceID, menu );
		_contextMenuView = v;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Event event;
		
		View v = _contextMenuView;
		
	    // TODO take into account different menus
	    
	    switch (item.getItemId()) {
	        case R.id.menu_delete:
	        	event = (Event) v.getTag();
	            deleteEvent( event );
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	/*************************************** Dialogs ***************************************************/
	// Launch the various dialogs
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ID_DATE:
			return new DatePickerDialog(this, _dateSetListener, _year, _month, _day);
		case DIALOG_ID_TIME:
			boolean is24h = DateFormat.is24HourFormat( LifeLogger.getContext() );
			return new TimePickerDialog(this, _timeSetListener, _hour, _minute, is24h);
		}
		return null;
	}
	private DatePickerDialog.OnDateSetListener _dateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			_year = year;
			_month = monthOfYear;
			_day = dayOfMonth;
			updateDateTime();
		}
	};
	
	private TimePickerDialog.OnTimeSetListener _timeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			_hour = hourOfDay;
			_minute = minute;
			updateDateTime();
		}
	};
}