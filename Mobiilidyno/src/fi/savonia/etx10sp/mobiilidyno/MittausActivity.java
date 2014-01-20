package fi.savonia.etx10sp.mobiilidyno;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MittausActivity extends Activity {

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
	
	private SensorManagerSimulator mSensorManager;
	
	private SensorEventListener mEventListenerAccelerometer;
	private SensorEventListener mEventListenerGravity;
	private SensorEventListener mEventListenerLinearAcceleration;
	private SensorEventListener mEventListenerLight;
	private SensorEventListener mEventListenerTemperature;
	private SensorEventListener mEventListenerOrientation;
	private SensorEventListener mEventListenerMagneticField;
	private SensorEventListener mEventListenerPressure;
	private SensorEventListener mEventListenerRotationVector;
	private SensorEventListener mEventListenerBarcode;
	
	private TextView mTextViewAccelerometer;
	
	private TextView tvLaskuri;
	private TextView tvNopeus;
	private Button lopetaMittaus;
	private long laskuri = 0;
	private long nopeus = 0;
	
	ArrayList<Mittaus> acceleroArray = new ArrayList<Mittaus>();
	ArrayList<Mittaus> gravityArray = new ArrayList<Mittaus>();
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
		lopetaMittaus = (Button)findViewById(R.id.button_lopetaMittaus);
		tvLaskuri = (TextView)findViewById(R.id.textView_laskuri);
		tvNopeus = (TextView)findViewById(R.id.textView_nopeus);
		
		mTextViewAccelerometer = (TextView)findViewById(R.id.text_accelerometer);
		
		this.laskuri = System.currentTimeMillis();
		this.mHandler.postDelayed(mRunnable, 0);
		
		tvNopeus.setText("Nopeus : " + nopeus);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		
		mSensorManager = SensorManagerSimulator.getSystemService(this,
				SENSOR_SERVICE);

		// 5) Connect to the sensor simulator, using the settings
		// that have been set previously with SensorSimulatorSettings
		mSensorManager.connectSimulator();

		// The rest of your application can stay unmodified.
		// //////////////////////////////////////////////////////////////

		initListeners();
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
	
	private void initListeners() {
		mEventListenerAccelerometer = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				//Log.w("a","now" + TimeUnit.MILLISECONDS.toDays(now)  + " now+1" + TimeUnit.MILLISECONDS.toDays(now+TimeUnit.MILLISECONDS.toSeconds(1000))  );
				
				if(acceleroArray.isEmpty())
				{
					acceleroArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
					Log.w("accelero", Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
				}
				else
				{
					Mittaus last = acceleroArray.get(acceleroArray.size()-1);

					long now = System.currentTimeMillis();
					
					long temp = last._TimeStamp + 1000;
					
					//Log.w("asd", now + " " + temp);
					
					if( now > temp)
					{
						acceleroArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
						Log.w("accelero", Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
						//mTextViewAccelerometer.setText("Accelerometer: " + Math.round(values[0] * 1000.0) / 1000.0 + ", " + Math.round(values[1] * 1000.0) / 1000.0 + ", " + Math.round(values[2] * 1000.0) / 1000.0 + ", " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
					}
					
					//val[1] = values[1]; val[0] = values[0]; val[2] = values[2];
					//mTextViewAccelerometer.setText("Accelerometer: " + Math.round(values[0] * 1000.0) / 1000.0 + ", " + Math.round(values[1] * 1000.0) / 1000.0 + ", " + Math.round(values[2] * 1000.0) / 1000.0 + ", " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		
		mEventListenerGravity = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				
				float[] values = event.values;
				//Log.w("a","now" + TimeUnit.MILLISECONDS.toDays(now)  + " now+1" + TimeUnit.MILLISECONDS.toDays(now+TimeUnit.MILLISECONDS.toSeconds(1000))  );
				
				if(gravityArray.isEmpty())
				{
					gravityArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
					Log.w("gravity", Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
				}
				else
				{
					Mittaus last = gravityArray.get(gravityArray.size()-1);

					long now = System.currentTimeMillis();
					
					long temp = last._TimeStamp + 1000;
					
					//Log.w("asd", now + " " + temp);
					
					if( now > temp)
					{
						gravityArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
						Log.w("gravity", Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
					}
					
					//mTextViewAccelerometer.setText("Accelerometer: " + Math.round(values[0] * 1000.0) / 1000.0 + ", " + Math.round(values[1] * 1000.0) / 1000.0 + ", " + Math.round(values[2] * 1000.0) / 1000.0 + ", " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		
		mEventListenerLinearAcceleration = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				
				float[] values = event.values;
				//Log.w("a","now" + TimeUnit.MILLISECONDS.toDays(now)  + " now+1" + TimeUnit.MILLISECONDS.toDays(now+TimeUnit.MILLISECONDS.toSeconds(1000))  );
				
				if(linearAcceleroArray.isEmpty())
				{
					linearAcceleroArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
					Log.w("linear", Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
				}
				else
				{
					Mittaus last = linearAcceleroArray.get(linearAcceleroArray.size()-1);

					long now = System.currentTimeMillis();
					
					long temp = last._TimeStamp + 1000;
					
					//Log.w("asd", now + " " + temp);
					
					if( now > temp)
					{
						linearAcceleroArray.add(new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]));
						Log.w("linear", Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
					}
					
					//mTextViewAccelerometer.setText("Accelerometer: " + Math.round(values[0] * 1000.0) / 1000.0 + ", " + Math.round(values[1] * 1000.0) / 1000.0 + ", " + Math.round(values[2] * 1000.0) / 1000.0 + ", " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mEventListenerAccelerometer,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mEventListenerGravity,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mEventListenerLinearAcceleration,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mEventListenerAccelerometer);
		mSensorManager.unregisterListener(mEventListenerGravity);
		mSensorManager.unregisterListener(mEventListenerLinearAcceleration);
		super.onStop();
	}
	
}
