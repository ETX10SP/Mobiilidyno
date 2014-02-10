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
import android.view.Menu;
import android.view.View;
//import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MittausActivity extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	
	private Sensor sAccelerometer;
	private Sensor sLinear;

	private TextView linear;

	private TextView accelero;
	
	private String Date;
	
	private TextView tvLaskuri;
	private TextView tvLinear;
	private TextView tvAcce;
	private TextView tvLasketut;
	//private Button lopetaMittaus;
	private long laskuri = 0;
	//private long nopeus = 0;
	
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
		tvLinear = (TextView)findViewById(R.id.textView_linear);
		tvAcce = (TextView)findViewById(R.id.textView_acce);
		tvLasketut = (TextView)findViewById(R.id.textView_Lasketut);
		
		accelero = (TextView) findViewById(R.id.text_accelerometer);
		linear = (TextView) findViewById(R.id.text_linear);
		
		this.laskuri = System.currentTimeMillis();
		this.mHandler.postDelayed(mRunnable, 0);
		
		//tvLinear.setText("Nopeus : " + nopeus);
		
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
		//finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mSensorManager.registerListener(this, sAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, sLinear, SensorManager.SENSOR_DELAY_NORMAL);
		
		this.Date = Helper.getDate(System.currentTimeMillis(), "dd_MM_yyyy_hh_mm_ss");
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
			Mittaus m = new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]);
			
			acceleroArray.add(m);
			
			Helper.writeToFile(m.toString(), "accelero_arvot_" + this.Date + ".txt");
			
			accelero.setText("" + Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
		}
		else
		{
			Mittaus m = new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]);
			
			acceleroArray.add(m);
			
			Helper.writeToFile(m.toString(), "accelero_arvot_" + this.Date + ".txt");
			
			accelero.setText("" + Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
		}
	}
	
	public void LinearAcceleroMeter(SensorEvent event)
	{
		float[] values = event.values;
		
		if(linearAcceleroArray.isEmpty())
		{
			Mittaus m = new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]);
			
			linearAcceleroArray.add(m);
			
			linear.setText(linearAcceleroArray.get(linearAcceleroArray.size()-1).toString());
			
			Helper.writeToFile(m.toString(), "linear_arvot_" + this.Date  + ".txt");
			
			double t = Math.sqrt(values[0] * values [0] + values[1] * values[1] + values[2] * values[2]);
			
			Helper.appendValueToTextBox(tvLasketut, t);
		}
		else
		{
			Mittaus m = new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]);
			
			linearAcceleroArray.add(m);
			
			linear.setText("" + Helper.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS") + " : " + values[0] + " " +values[1] + " " + values[2]);
			
			Helper.writeToFile(m.toString(), "linear_arvot_" + this.Date  + ".txt");
			
			double t = Math.sqrt(values[0] * values [0] + values[1] * values[1] + values[2] * values[2]);
			
			Helper.appendValueToTextBox(tvLasketut, t);
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
