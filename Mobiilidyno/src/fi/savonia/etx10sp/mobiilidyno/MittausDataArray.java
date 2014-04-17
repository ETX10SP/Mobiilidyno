package fi.savonia.etx10sp.mobiilidyno;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by s06863 on 17.4.2014.
 */
public class MittausDataArray extends ArrayList<Mittaus> implements Serializable {
    public String kuskinPaino;
    public String pyoranPaino;
    public String renkaat;
    public String valitykset;
    public String date;

    public MittausDataArray(String kPaino, String pPaino, String renkaat, String valitykset, String date) {
        this.kuskinPaino = kPaino;
        this.pyoranPaino = pPaino;
        this.renkaat = renkaat;
        this.valitykset = valitykset;
        this.date = date;
    }
}
