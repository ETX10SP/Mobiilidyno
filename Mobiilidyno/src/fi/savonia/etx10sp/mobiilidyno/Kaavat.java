package fi.savonia.etx10sp.mobiilidyno;

public class Kaavat {
	
	public static double laskeKokonaiskiihtyvyys(float x, float y, float z)
	{		
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	// Palauttaa nopeuden yksik�ss� m/s
	// alkunopeus = m/s
	// kiihtyvyys_keskiarvo = m/s^2
	// aika_s = s
	public static double laskeNopeus(double alkunopeus, double kiihtyvyys_keskiarvo, double aika_s)
	{
		return alkunopeus + kiihtyvyys_keskiarvo * aika_s;
	}

	// palauttaa tehon watteina (W)
	// kokonais_massa = kg
	// kiihtyvyys m/s^2
	// nopeus m/s
	public static double laskeTehoWatteina(double kokonais_massa, double kiihtyvyys, double nopeus)
	{
		return (kokonais_massa * kiihtyvyys * nopeus) / (2 * Math.PI);
	}

    public static double laskeMatka(double kiihtyvyys, double aika)
    {
        return kiihtyvyys * 0.5 * aika * aika;
    }

    public static double laskeTeho(double massa, double kiihtyvyys, double matka)
    {
        return massa * kiihtyvyys * ( matka / 2);
    }

}
