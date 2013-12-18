package fi.savonia.etx10sp.mobiilidyno;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	Button asetuksetButton;
	Button selaaButton;
	Button tietojaButton;
	
	void initializeComponents()
	{
		this.asetuksetButton = (Button) findViewById(R.id.asetuksetButton);
		this.asetuksetButton.setOnClickListener(this);
		
		this.selaaButton = (Button) findViewById(R.id.selaaButton);
		this.selaaButton.setOnClickListener(this);
		
		this.tietojaButton = (Button) findViewById(R.id.tietojaButton);
		this.tietojaButton.setOnClickListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initializeComponents();
		
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
               
        // Tarkastus onko GPS p‰‰ll‰
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        	kytkeGPSDialog();
        }
        
        // K‰yd‰‰n l‰pi k‰ytett‰viss‰ olevat sensorit
        Boolean accelerometer = false;
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i< deviceSensors.size(); i++) {
            if (deviceSensors.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {
            	accelerometer = true;
                break;
            }
        }
        
        // Suljetaan sovellus jos kiihtyvuusanturi ei ole k‰ytett‰viss‰
        if (accelerometer == false)
        {
        	showToast("Kiihtyvyysanturi ei k‰ytett‰viss‰");
        	this.finish();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		
		if(v.equals(this.asetuksetButton))
		{
			intent = new Intent(this, AsetuksetActivity.class);		
		}
		else if (v.equals(this.selaaButton))
		{
			intent = new Intent(this, MittaustenSelausActivity.class);	
		}
		else if (v.equals(this.tietojaButton))
		{
			intent = new Intent(this, TiedotActivity.class);	
		}
		
		if (intent != null)
		{
			startActivity(intent);
		}
	}
	
	public void showToast(String message)
    {
    	Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
	
	// Dialogin avulla voidaan kytke‰ GPS p‰‰lle
    private void kytkeGPSDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS on pois p‰‰lt‰. Haluatko kytke‰ sen p‰‰lle?")
        .setCancelable(false)
        .setPositiveButton("Kytke GPS p‰‰lle",
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Peruuta",
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
	

}
