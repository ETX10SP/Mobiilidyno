package fi.savonia.etx10sp.mobiilidyno;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TiedotActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tiedot);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tiedot, menu);
		return true;
	}

}
