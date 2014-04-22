package fi.savonia.etx10sp.mobiilidyno;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.jjoe64.graphview.GraphViewSeries;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MittaustenSelausActivity extends Activity implements OnItemSelectedListener {
	private static final String TAG = "MittaustenSelausActivity";
	private String suure = "Kaikki";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mittausten_selaus);

        ListView list = (ListView)findViewById(R.id.list);

        Spinner spinner = (Spinner)findViewById(R.id.spinner_suure);
        ArrayAdapter<CharSequence> arr_adapter = ArrayAdapter.createFromResource(this, R.array.suureet_array, android.R.layout.simple_spinner_item);
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arr_adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	protected Adapter initializedAdapter = null;
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(initializedAdapter != parent.getAdapter()) {
					initializedAdapter = parent.getAdapter();
					return;
				}
				
				suure = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        ObjectInput in;
        File f;
        HashMap<String, MittausDataArray> hashMapMittausData = null;
        try {
            f = new File(Environment.getExternalStorageDirectory(), "mittaukset.data");

            if(f.exists()) {
                in = new ObjectInputStream(new FileInputStream(f));
                hashMapMittausData = (HashMap<String, MittausDataArray>) in.readObject();
                in.close();
            } else {
                // MITTAUKSIA EI OLE
            }
        } catch (Exception e) {e.printStackTrace();}

        final HashMap<String, MittausDataArray> Data = hashMapMittausData;

        ArrayList<HashMap<String, String>> array = new ArrayList<HashMap<String, String>>();

        if(Data != null) {
            for (String s : hashMapMittausData.keySet()) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("time", Data.get(s).date);
                array.add(item);
            }
        } else {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("time", "Ei mittauksia");
            array.add(item);
        }

        SimpleAdapter sadapter = new SimpleAdapter(this, array, android.R.layout.simple_list_item_1, new String[] { "time" }, new int[] { android.R.id.text1 } );

        list.setAdapter(sadapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clicked = (TextView) view;

                //Toast.makeText(MittaustenSelausActivity.this, "Item with id [" + id + "] - Position [" + position + "] - asd [" + clicked.getText() + "]", Toast.LENGTH_LONG).show();

                if(!clicked.getText().equals("Ei mittauksia")) {
                    Intent intent = new Intent(MittaustenSelausActivity.this, NaytaMittausActivity.class);
                    intent.putExtra("suure", suure);
                    Bundle b = new Bundle();

                    /*
                    Jostain syyst� ei MittausDataArray. Ei mene sellaisenaan vaan ainoastaan ArrayList<Mittaus> menee
                    Asetukset siis laitettava erikseen
                     */
                    MittausDataArray mittausData = Data.get(clicked.getText());
                    HashMap<String, String> asetukset = new HashMap<String, String>();
                    asetukset.put("kuski", mittausData.kuskinPaino);
                    asetukset.put("pyora", mittausData.pyoranPaino);
                    asetukset.put("renkaat", mittausData.renkaat);
                    asetukset.put("valitykset", mittausData.valitykset);
                    asetukset.put("date", mittausData.date);

                    b.putSerializable("mittaus", mittausData);
                    b.putSerializable("asetukset", asetukset);

                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });
	}
	
	
	public void warn(String msg)
	{
		Log.w(TAG, msg);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mittausten_selaus, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		//Ei mene koskaan t�nne
		suure = parent.getItemAtPosition(position).toString();
	}	

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}
