package fi.savonia.etx10sp.mobiilidyno;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;


public class Helper {

	/**
	 * Return date in specified format.
	 * @param milliSeconds Date in milliseconds
	 * @param dateFormat Date format 
	 * @return String representing date in specified format
	 */
	public static String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat);
	
	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}
	
	public static void showToast(String message, Context c)
	{
		Toast.makeText(c, message, Toast.LENGTH_LONG).show();
	}
	
	public static void appendValuesToTextBox(TextView t, float[] values)
	{
		String s = t.getText().toString();
		
		String f1 = String.format("%.2f", values[0]);
		String f2 = String.format("%.2f", values[1]);
		String f3 = String.format("%.2f", values[2]);
		
		t.setText(s+"\n"+ f1 + " " + f2 + " " + f3);
	}

}
