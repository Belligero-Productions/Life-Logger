package org.belligero.nautilus.life.logger.utils;

import java.util.Calendar;

import org.belligero.nautilus.life.logger.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Utils {
	public static final String getTimeString(Calendar cal) {
		return getTimeString(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}
	
	public static final String getTimeString(int hour, int minute) {
		return hour + ":" + (minute <= 9 ? "0"+minute : minute);
	}
	
	public static final String getDateString(Calendar cal) {
		return getDateString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
	
	public static final String getDateString(int year, int month, int day) {
		return year + "-" + (month < 9 ? "0"+(month+1) : month+1) + "-" + (day <= 9 ? "0"+day : day);
	}
	
	// TODO Make this have passback functions, so it can be made more generic
	public static final boolean confirm( Context context, String title, String message ) {
		final boolean[] confirm = new boolean[1];
		
		AlertDialog dialog = new AlertDialog.Builder( context ).create();
		dialog.setTitle( title );
		dialog.setMessage( message );
		dialog.setCancelable( false );
		
		dialog.setButton(
				DialogInterface.BUTTON_POSITIVE,
				context.getText( android.R.string.yes ),
				new DialogInterface.OnClickListener() {	
					public void onClick( DialogInterface dialog, int which ) {
						confirm[0] = true;
					}
				}
			);
		
		dialog.setButton(
				DialogInterface.BUTTON_NEGATIVE,
				context.getText( android.R.string.no ),
				new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int which ) {
						confirm[0] = false;
					}
				}
			);
		
		dialog.setIcon( android.R.drawable.ic_dialog_alert );
		dialog.show();
		
		return confirm[0];
	}
}
