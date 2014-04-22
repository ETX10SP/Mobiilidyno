package fi.savonia.etx10sp.mobiilidyno;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends Activity implements OnClickListener {

	Button aloitaButton;
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
		
		this.aloitaButton = (Button)findViewById(R.id.aloitaMittausButton);
		this.aloitaButton.setOnClickListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initializeComponents();
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
		
		if(v.equals(this.aloitaButton))
		{


            HashMap<String, String> asetukset = Helper.getAsetukset(false);
            if(asetukset.containsKey("error"))
            {
                Helper.showToast("Tarkasta asetukset!", this);
            }
            else
            {
                intent = new Intent(this, MittausActivity.class);
            }
		}
		else if(v.equals(this.asetuksetButton))
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
}
