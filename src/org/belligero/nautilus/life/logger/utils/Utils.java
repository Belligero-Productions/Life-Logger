package org.belligero.nautilus.life.logger.utils;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Utils {
	// TODO Format the output of these better
	public static final String getTimeString( long timestamp ) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( timestamp * 1000 );
		return getTimeString(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}
	
	public static final String getTimeString(Calendar cal) {
		return getTimeString(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}
	
	public static final String getTimeString(int hour, int minute) {
		return hour + ":" + (minute <= 9 ? "0"+minute : minute);
	}
	
	public static final String getDateString( long timestamp ) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( timestamp * 1000 );
		return getDateString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
	
	public static final String getDateString(Calendar cal) {
		return getDateString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
	
	public static final String getDateString(int year, int month, int day) {
		return year + "-" + (month < 9 ? "0"+(month+1) : month+1) + "-" + (day <= 9 ? "0"+day : day);
	}
	
	/**
	 * Create a confirmation dialog which will use a ConfirmResultListener to return the results.
	 * 
	 * @param context The activity context to create a dialog with
	 * @param titleResId String resource ID of the title for the dialog
	 * @param messageResId String resource ID of the message to show in the dialog
	 * @param confirmResultListener A ConfirmResultListener to call the "confirm" or "deny" methods of
	 */
	public static final void confirm( Context context, int titleResId, int messageResId, final ConfirmResultListener confirmResultListener ) {
		confirm(
				context,
				context.getString( titleResId ),
				context.getString( messageResId ),
				confirmResultListener
			);
	}
	
	/**
	 * Create a confirmation dialog which will use a ConfirmResultListener to return the results.
	 * 
	 * @param context The activity context to create a dialog with
	 * @param title The title for the dialog
	 * @param message The message to display in the dialog
	 * @param confirmResultListener A ConfirmResultListener to call the "confirm" or "deny" methods of
	 */
	public static final void confirm( Context context, String title, String message, final ConfirmResultListener confirmResultListener ) {
		
		AlertDialog dialog = new AlertDialog.Builder( context ).create();
		dialog.setTitle( title );
		dialog.setMessage( message );
		dialog.setCancelable( false );
		
		dialog.setButton(
				DialogInterface.BUTTON_POSITIVE,
				context.getText( android.R.string.yes ),
				new DialogInterface.OnClickListener() {	
					public void onClick( DialogInterface dialog, int which ) {
						confirmResultListener.confirm();
					}
				}
			);
		
		dialog.setButton(
				DialogInterface.BUTTON_NEGATIVE,
				context.getText( android.R.string.no ),
				new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int which ) {
						confirmResultListener.deny();
					}
				}
			);
		
		dialog.setIcon( android.R.drawable.ic_dialog_alert );
		dialog.show();
	}
}
