package fi.savonia.etx10sp.mobiilidyno;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
//import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MittausActivity extends Activity implements SensorEventListener {

	public class Mittaus
	{
		long _TimeStamp;
		float _X;
		float _Y;
		float _Z;
		
		public Mittaus(long TimeStamp, float X, float Y, float Z)
		{
			this._TimeStamp = TimeStamp;
			this._X = X;
			this._Y = Y;
			this._Z = Z;
		}
	}
	
	private SensorManager mSensorManager;
	
	private Sensor sAccelerometer;
	private Sensor sLinear;

	private TextView linear;

	private TextView accelero;
	
	private TextView tvLaskuri;
	private TextView tvNopeus;
	//private Button lopetaMittaus;
	private long laskuri = 0;
	private long nopeus = 0;
	
	ArrayList<Mittaus> acceleroArray = new ArrayList<Mittaus>();
	ArrayList<Mittaus> linearAcceleroArray = new ArrayList<Mittaus>();
	
	private Handler mHandler = new Handler();
	
	private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - laskuri;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            tvLaskuri.setText(String.format("%d:%02d:%03d", minutes, seconds, millis));

            mHandler.postDelayed(this, 1);
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mittaus);
		//lopetaMittaus = (Button)findViewById(R.id.button_lopetaMittaus);
		tvLaskuri = (TextView)findViewById(R.id.textView_laskuri);
		tvNopeus = (TextView)findViewById(R.id.textView_nopeus);
		
		accelero = (TextView) findViewById(R.id.text_accelerometer);
		linear = (TextView) findViewById(R.id.text_linear);
		
		this.laskuri = System.currentTimeMillis();
		this.mHandler.postDelayed(mRunnable, 0);
		
		tvNopeus.setText("Nopeus : " + nopeus);
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		Boolean accelerometer = false;
		List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i< deviceSensors.size(); i++) {
            if (deviceSensors.get(i).getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            	accelerometer = true;
                break;
            }
        }
        
        // Suljetaan näkymä jos kiihtyvuusanturi ei ole käytettävissä
        if (accelerometer == false)
        {
        	Toast.makeText(getApplicationContext(), "Kiihtyvyysanturi ei käytettävissä!", Toast.LENGTH_LONG).show();
        	//this.finish();
        }
        else
        {
        	Toast.makeText(getApplicationContext(), "Kiihtyvyysanturi löytyy", Toast.LENGTH_LONG).show();
        }
		
		sAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mittaus, menu);
		return true;
	}
	
	public void OnClickOnMittausActivity(View v)
	{
		onStop();
		mHandler.removeCallbacks(mRunnable);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mSensorManager.registerListener(this, sAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, sLinear, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		mSensorManager.unregisterListener(this, sAccelerometer);
		mSensorManager.unregisterListener(this,sLinear);
	}
	
	public void AcceleroMeter(SensorEvent event)
	{
		float[] values = event.values;
		
		if(acceleroArray.isEmpty())
		{
			acceleroArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
			accelero.setText("" + Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
		}
		else
		{
			Mittaus last = acceleroArray.get(acceleroArray.size()-1);

			long now = System.currentTimeMillis();
			
			long temp = last._TimeStamp + 500;
			
			if( now > temp)
			{
				acceleroArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
				accelero.setText("" + Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
			}
		}
	}
	
	public void LinearAcceleroMeter(SensorEvent event)
	{
		float[] values = event.values;
		
		if(linearAcceleroArray.isEmpty())
		{
			linearAcceleroArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
			linear.setText("" + Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
		}
		else
		{
			Mittaus last = linearAcceleroArray.get(linearAcceleroArray.size()-1);

			long now = System.currentTimeMillis();
			
			long temp = last._TimeStamp + 500;
							
			if( now > temp)
			{
				linearAcceleroArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
				linear.setText("" + Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
			}
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			AcceleroMeter(event);
		}
			
		if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
		{
			LinearAcceleroMeter(event);
		}
	}
}
