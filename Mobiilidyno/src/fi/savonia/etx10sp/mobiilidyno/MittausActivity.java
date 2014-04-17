package fi.savonia.etx10sp.mobiilidyno;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MittausActivity extends Activity implements SensorEventListener {
	private static final String TAG = "MittausActivity";
	private SensorManager mSensorManager;
	private Sensor sLinear;
	private String Date;
	
	private TextView tvLaskuri;
    private TextView linear;
	private long laskuri = 0;
    private LinearLayout leiska;
    private HashMap<String, String> asetukset;

	MittausDataArray linearAcceleroArray;
	
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

		tvLaskuri = (TextView)findViewById(R.id.textView_laskuri);
        leiska = (LinearLayout)findViewById(R.id.leiska);
        this.Date = Helper.getDate(System.currentTimeMillis(), "dd_MM_yyyy_hh_mm_ss");
		linear = (TextView) findViewById(R.id.text_linear);

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
        	this.finish();
        }
        else
        {
        	Toast.makeText(getApplicationContext(), "Kiihtyvyysanturi löytyy", Toast.LENGTH_LONG).show();
        }

        this.asetukset = this.getAsetukset(true);
        if(this.asetukset.containsKey("error"))
        {
        	//jos sisältää sen, tuli virhe.
        	if(this.asetukset.get("error") == "no_settings")
        	{
        		//TODO: Toimiiko jos osa vain välttämättömistä asetuksista on tehty?
        		Toast.makeText(getApplicationContext(), "Asetuksia ei ollut. Ne täytyy tehdä ennen kuin voidaan mittailla", Toast.LENGTH_LONG).show();
        		Intent intent = new Intent(this, AsetuksetActivity.class);
        		this.onDestroy(); //TODO: ei lopeta. Pitäisikö vanha activity lopettaa?
        		startActivity(intent);
        		//this.finish();	
        	}
        	else
        	{
        		//joku muu virhe, varoitetaan siitä. laitetaan rivi, jolta lähdetään, nii se löytyy nopeammin
        		String lineNumber = Integer.toString(new Throwable().getStackTrace()[0].getLineNumber());
        		warn("Line: "+lineNumber+" "+this.asetukset.get("error"));
        	}
        }      
        this.linearAcceleroArray = new MittausDataArray(this.asetukset.get("kuski"), this.asetukset.get("pyora"), this.asetukset.get("renkaat"), this.asetukset.get("valitykset"), this.Date);

		sLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	}
	
	private void warn(String msg)
	{
		Log.w(TAG, msg);
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

        if(this.laskuri == 0) {
            this.laskuri = System.currentTimeMillis();
            this.mHandler.postDelayed(mRunnable, 0);
            mSensorManager.registerListener(this, sLinear, SensorManager.SENSOR_DELAY_NORMAL);
        }
	}

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

	@Override
	protected void onStop() {
		super.onStop();

        String linear = "";

        for (Mittaus m : this.linearAcceleroArray)
        {
            linear += m.toString() + "\n";
        }

        //Helper.writeToFile(linear.trim(), "linear_" + this.Date + ".txt");

		mSensorManager.unregisterListener(this);

        drawGraph();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
		{
            float[] values = event.values;

            linear.setText(values[0] + " " + values[1] + " " + values[2]);

            Mittaus m = new Mittaus(System.currentTimeMillis(), values[0], values[1], values[2]);

            linearAcceleroArray.add(m);
		}
	}

    public void drawGraph() {

        ArrayList<GraphView.GraphViewData> acc = new ArrayList<GraphView.GraphViewData>();

        Mittaus prev = null;
        double prevNopeus = 0;
        double prevTime = 0;
        double prevKok = 0;

        for(Mittaus m : this.linearAcceleroArray)
        {
            double time = m.TimeStamp - this.linearAcceleroArray.get(0).TimeStamp;

            double kok = Kaavat.laskeKokonaiskiihtyvyys(m.X - this.linearAcceleroArray.get(0).X, m.Y - this.linearAcceleroArray.get(0).Y, m.Z - this.linearAcceleroArray.get(0).Z);

            double nopeus;

            if(prev == null)
            {
                nopeus = Kaavat.laskeNopeus(0, (kok / 2), time);
            }
            else
            {
                nopeus = Kaavat.laskeNopeus(prevNopeus, (kok - prevKok) / 2, (time - prevTime) / 1000);
            }

            double massa = Double.parseDouble(this.asetukset.get("kuski")) + Double.parseDouble(this.asetukset.get("pyora"));

            double teho = Kaavat.laskeTeho(massa, kok, nopeus);

            acc.add(new GraphView.GraphViewData(time, teho));

            prev = m;
            prevNopeus = nopeus;
            prevTime = time;
            prevKok = kok;
        }

        GraphView graphView = new LineGraphView(
                this // context
                , "Teho" // heading
        );

        GraphViewSeries accSeries = new GraphViewSeries(acc.toArray(new GraphView.GraphViewData[acc.size()]));

        graphView.addSeries(accSeries); // data

        save(this.linearAcceleroArray);

        LinearLayout layout = (LinearLayout) findViewById(R.id.main);
        leiska.addView(graphView);
    }

    private HashMap<String, String> getAsetukset() {
    	return getAsetukset(false);
    }
    
    private HashMap<String, String> getAsetukset(boolean showError) {
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

    private void save(MittausDataArray dataArray)
    {
        ObjectInputStream in;
        ObjectOutputStream out;
        FileInputStream fin;
        FileOutputStream fout;
        File f;
        HashMap<String, MittausDataArray> ss = null;

        try {
            f = new File(Environment.getExternalStorageDirectory(), "mittaukset.data");

            if(f.exists()) {
                in = new ObjectInputStream(new FileInputStream(f));
                ss = (HashMap<String, MittausDataArray>) in.readObject();
                ss.put(this.Date, dataArray);
                in.close();
            }
            else {
                ss = new HashMap<String, MittausDataArray>();
                ss.put(this.Date, dataArray);
            }

            out = new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(ss);
            out.close();

        } catch (Exception e) {
            Log.w("error", e.toString());
        }
    }
}
