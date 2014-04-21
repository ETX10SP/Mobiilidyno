package fi.savonia.etx10sp.mobiilidyno;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AsetuksetActivity extends Activity implements OnClickListener {
	
	Button asetuksetTallennaButton;
	EditText pyoranPaino;
	EditText kuskinPaino;
	EditText renkaat;
	EditText valitysSuhteet;
	
	void initializeComponents()
	{
		this.asetuksetTallennaButton = (Button) findViewById(R.id.asetuksetTallennaButton);
		this.asetuksetTallennaButton.setOnClickListener(this);
		this.pyoranPaino = (EditText) findViewById(R.id.pyoranPainoEditText);
		this.kuskinPaino = (EditText) findViewById(R.id.kuskinPainoEditText);
		//this.renkaat = (EditText) findViewById(R.id.renkaidenKokoEditText);
		//this.valitysSuhteet = (EditText) findViewById(R.id.valitysSuhteetEditText);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asetukset);
		
		initializeComponents();
		
		load();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void save()
	{
		ObjectOutput out;
		
		File f;
		
		HashMap<String, String> asetukset = new HashMap<String, String>();
		
		asetukset.put("kuski", this.kuskinPaino.getText().toString());
		asetukset.put("pyora", this.pyoranPaino.getText().toString());
		//asetukset.put("renkaat", this.renkaat.getText().toString());
		//asetukset.put("valitykset", this.valitysSuhteet.getText().toString());
		
		try {
			f = new File(Environment.getExternalStorageDirectory(), "asetukset.data");
	        out = new ObjectOutputStream(new FileOutputStream(f));        
	        out.writeObject(asetukset);
	        out.close();
	    } catch (Exception e) {e.printStackTrace();}
		
		showToast("Asetukset tallennettu");
	}
	
	public void showToast(String message)
    {
    	Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
	
	@SuppressWarnings("unchecked")
	private void load()
	{
		ObjectInput in;
		File f;
	    HashMap<String, String> ss=null;
	    try {
	    	f = new File(Environment.getExternalStorageDirectory(), "asetukset.data");
	        in = new ObjectInputStream(new FileInputStream(f));  
	        ss=(HashMap<String, String>) in.readObject();
	        in.close();
	    } catch (Exception e) {e.printStackTrace();}
	    
	    if(ss != null)
	    {
	    	this.kuskinPaino.setText(ss.get("kuski"));
	    	this.pyoranPaino.setText(ss.get("pyora"));
	    	//this.renkaat.setText(ss.get("renkaat"));
	    	//this.valitysSuhteet.setText(ss.get("valitykset"));
	    }
	}
	
	@Override
	public void onClick(View v) {
		
		if(v.equals(this.asetuksetTallennaButton))
		{
			save();
		}
	}
}
