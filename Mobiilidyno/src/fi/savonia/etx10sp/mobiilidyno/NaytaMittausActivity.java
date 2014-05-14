package fi.savonia.etx10sp.mobiilidyno;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by s06863 on 17.4.2014.
 * Edited by s04101 on 19.4.2014.
 */
public class NaytaMittausActivity extends Activity {
	private static final String TAG = "NaytaMittausActivity";
    MittausDataArray linearAcceleroArray;
    ArrayList<Mittaus> a;
    private String suure = "Kaikki"; //N�ytet��n kaikki oletuksena
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nayta_mittaus);

        Bundle b = getIntent().getExtras();

        this.a = (ArrayList<Mittaus>)b.getSerializable("mittaus");

        HashMap<String, String> asetukset = (HashMap<String, String>)b.getSerializable("asetukset");

        this.linearAcceleroArray = new MittausDataArray(asetukset.get("kuski"), asetukset.get("pyora"), asetukset.get("date"));

        this.linearAcceleroArray.addAll(a);
        
        Bundle extras = getIntent().getExtras();
        suure = extras != null ? extras.getString("suure") : suure;

        drawGraph();
    }

    public void drawGraph() {

        ArrayList<GraphView.GraphViewData> kiihtyvyys = new ArrayList<GraphView.GraphViewData>();
        ArrayList<GraphView.GraphViewData> nopeus = new ArrayList<GraphView.GraphViewData>();
        ArrayList<GraphView.GraphViewData> teho = new ArrayList<GraphView.GraphViewData>();
        //ArrayList<GraphView.GraphViewData> matka = new ArrayList<GraphView.GraphViewData>();
        //ArrayList<GraphView.GraphViewData> teho2 = new ArrayList<GraphView.GraphViewData>();
        //ArrayList<GraphView.GraphViewData> kiihtyvyys2 = new ArrayList<GraphView.GraphViewData>();

        Mittaus prev = null;
        double prevNopeus = 0;
        double prevTime = 0;
        double prevKok = 0;
        //double prevMatka = 0;

        for(Mittaus mittaus : this.linearAcceleroArray)
        {
            double t_xAkselille = (mittaus.TimeStamp - this.linearAcceleroArray.get(0).TimeStamp) / 1000;

            double t = (mittaus.TimeStamp - this.linearAcceleroArray.get(0).TimeStamp - prevTime) / 1000;

            double a = Kaavat.laskeKokonaiskiihtyvyys(mittaus.X, mittaus.Y, mittaus.Z);

            double v = Kaavat.laskeNopeus(prevNopeus, (a + prevKok) / 2, t);

            double m = Double.parseDouble(this.linearAcceleroArray.kuskinPaino) + Double.parseDouble(this.linearAcceleroArray.pyoranPaino);

            double w = Kaavat.laskeTehoWatteina(m, a, v);

            //double s = Kaavat.laskeMatka(a, t-prevTime);

            //double w2 = Kaavat.laskeTeho(m, a, s);

            nopeus.add(new GraphView.GraphViewData(t_xAkselille, v));
            kiihtyvyys.add(new GraphView.GraphViewData(t_xAkselille, a));
	        teho.add(new GraphView.GraphViewData(t_xAkselille, w));
            //matka.add(new GraphView.GraphViewData(t_xAkselille, s+prevMatka));
            //teho2.add(new GraphView.GraphViewData(t_xAkselille, w2));
            //kiihtyvyys2.add(new GraphView.GraphViewData(t_xAkselille, a + (a - prevKok)));

            prev = mittaus;
            prevNopeus = v;
            prevTime = t;
            prevKok = a;
            //prevMatka = s;
        }

        GraphView graphView;

        GraphViewSeries.GraphViewSeriesStyle k = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(0, 255, 0), 1);
        GraphViewSeries.GraphViewSeriesStyle n = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(0, 0, 255), 1);
        GraphViewSeries.GraphViewSeriesStyle t = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(255, 0, 0), 1);
        //GraphViewSeries.GraphViewSeriesStyle t2 = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(255, 255, 255), 1);
        //GraphViewSeries.GraphViewSeriesStyle m = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(0, 0, 0), 1);

        GraphViewSeries kiihtyvyysSeries = new GraphViewSeries("Kiihtyvyys", k, kiihtyvyys.toArray(new GraphView.GraphViewData[kiihtyvyys.size()]));
        GraphViewSeries nopeusSeries = new GraphViewSeries("Nopeus", n, nopeus.toArray(new GraphView.GraphViewData[nopeus.size()]));
        GraphViewSeries tehoSeries = new GraphViewSeries("Teho", t, teho.toArray(new GraphView.GraphViewData[teho.size()]));
        //GraphViewSeries teho2Series = new GraphViewSeries("Teho2", t2, teho2.toArray(new GraphView.GraphViewData[teho2.size()]));
        //GraphViewSeries matkaSeries = new GraphViewSeries("Matka", m, matka.toArray(new GraphView.GraphViewData[matka.size()]));
        //GraphViewSeries kiihtyvyys2Series = new GraphViewSeries("Kiihtyvyys2", k, kiihtyvyys2.toArray(new GraphView.GraphViewData[kiihtyvyys2.size()]));

        if(!suure.equals("Kaikki"))
        {
            graphView = new LineGraphView(
                    this // context
                    , suure // heading
            );

            if(suure.equals("Kiihtyvyys"))
                graphView.addSeries(kiihtyvyysSeries);
            else if(suure.equals("Nopeus"))
                graphView.addSeries(nopeusSeries);
            else if(suure.equals("Teho"))
                graphView.addSeries(tehoSeries);
            /*
            else if(suure.equals("Teho2"))
                graphView.addSeries(teho2Series);
            else if(suure.equals("Matka"))
                graphView.addSeries(matkaSeries);
            else if(suure.equals("Kiihtyvyys2"))
                graphView.addSeries(matkaSeries);
                */
        }
        else
        {
            graphView = new LineGraphView(
                    this // context
                    , "MittausData" // heading
            );
            graphView.addSeries(kiihtyvyysSeries);
            graphView.addSeries(nopeusSeries);
            graphView.addSeries(tehoSeries);
            //graphView.addSeries(teho2Series);
            //graphView.addSeries(matkaSeries);
            //graphView.addSeries(kiihtyvyys2Series);

            graphView.setShowLegend(true);
            graphView.setLegendAlign(GraphView.LegendAlign.TOP);
            graphView.setLegendWidth(200);
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.main);
        layout.addView(graphView);
    }
}