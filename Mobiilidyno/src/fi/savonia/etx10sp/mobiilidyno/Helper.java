package fi.savonia.etx10sp.mobiilidyno;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import fi.savonia.etx10sp.mobiilidyno.Mittaus;

import android.content.Context;
import android.os.Environment;
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
	
	public static String arrayToString(ArrayList<Mittaus> a)
	{
		String s = "";
		
		for(Mittaus m : a)
		{
			s.concat(m.TimeStamp + " " + m.X + " " + m.Y + " " + m.Z + "\n");
		}
		
		//s.trim();
		
		return s;
	}
	
	public static void writeToFile(String data, String fileName) {
	    try {
	    	File file = new File(Environment.getExternalStorageDirectory(), fileName);
	        FileWriter fw = new FileWriter(file, true);
	        fw.write(data);
	        fw.flush();
	        fw.close();
	    }
	    catch (IOException e) {
	        //Log.e("Exception", "File write failed: " + e.toString());
	    } 
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
	
	public static void appendValueToTextBox(TextView t, double value, String format)
	{
		String s = t.getText().toString();
		
		String f1 = String.format(format, value);
		
		t.setText(s+"\n"+ f1);
	}
	
	public static void appendValueToTextBox(TextView t, double value)
	{
		String s = t.getText().toString();
		
		String f1 = String.format("%.2f", value);
		
		t.setText(s+"\n"+ f1);
	}
	
	public static void appendValueToTextBox(TextView t, String value)
	{
		String s = t.getText().toString();
		
		String f1 = String.format("%.2f", value);
		
		t.setText(s+"\n"+ f1);
	}

    public static HashMap<String, String> getAsetukset(boolean showError)
    {
        ObjectInput in;
        File f;
        HashMap<String, String> ss = new HashMap<String, String>();
        try {
            f = new File(Environment.getExternalStorageDirectory(), "asetukset.data");
            if(f.exists())
            {
                in = new ObjectInputStream(new FileInputStream(f));
                ss=(HashMap<String, String>) in.readObject();
                in.close();
            }
            else
            {
                ss.put("error", "no_settings");
            }
            //} catch (FileNotFoundException e) {
            //} catch (IOException e) {
        } catch (Exception e) {

            //e.printStackTrace()
            if(showError == true)
            {
                ss.put("error", e.toString());
            }
            else
            {
                ss.put("error", "true");
            }
        }

        return ss;
    }
}
