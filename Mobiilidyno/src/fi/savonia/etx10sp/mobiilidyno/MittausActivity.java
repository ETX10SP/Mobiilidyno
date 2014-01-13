package fi.savonia.etx10sp.mobiilidyno;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MittausActivity extends Activity {

	private TextView tvLaskuri;
	private TextView tvNopeus;
	private Button lopetaMittaus;
	private long laskuri = 0;
	private long nopeus = 0;
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
		this.laskuri = System.currentTimeMillis();
		this.mHandler.postDelayed(mRunnable, 0);
		
		tvNopeus.setText("Nopeus : " + nopeus);
		
        // K‰yd‰‰n l‰pi k‰ytett‰viss‰ olevat sensorit
        Boolean accelerometer = false;
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i< deviceSensors.size(); i++) {
            if (deviceSensors.get(i).getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            	accelerometer = true;
                break;
            }
        }
        
        // Suljetaan n‰kym‰ jos kiihtyvuusanturi ei ole k‰ytett‰viss‰
        if (accelerometer == false)
        {
        	Toast.makeText(getApplicationContext(), "Kiihtyvyysanturi ei k‰ytett‰viss‰!", Toast.LENGTH_LONG).show();
        	//this.finish();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mittaus, menu);
		return true;
	}
	
	public void OnClickOnMittausActivity(View v)
	{
		mHandler.removeCallbacks(mRunnable);
		finish();
	}
}
