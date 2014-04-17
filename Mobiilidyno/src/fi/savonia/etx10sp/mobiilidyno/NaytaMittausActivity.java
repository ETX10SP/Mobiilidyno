package fi.savonia.etx10sp.mobiilidyno;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by s06863 on 17.4.2014.
 */
public class NaytaMittausActivity extends Activity {

    MittausDataArray linearAcceleroArray;
    ArrayList<Mittaus> a;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nayta_mittaus);

        Bundle b = getIntent().getExtras();

        this.a = (ArrayList<Mittaus>)b.getSerializable("mittaus");

        HashMap<String, String> asetukset = (HashMap<String, String>)b.getSerializable("asetukset");

        this.linearAcceleroArray = new MittausDataArray(asetukset.get("kuski"), asetukset.get("pyora"), asetukset.get("renkaat"), asetukset.get("valitykset"), asetukset.get("date"));

        this.linearAcceleroArray.addAll(a);

        drawGraph();
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

            double massa = Double.parseDouble(this.linearAcceleroArray.kuskinPaino) + Double.parseDouble(this.linearAcceleroArray.pyoranPaino);

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

        LinearLayout layout = (LinearLayout) findViewById(R.id.main);
        layout.addView(graphView);
    }
}