package fi.savonia.etx10sp.mobiilidyno;

public class Kaavat {
	
	public static double laskeKokonaiskiihtyvyys(float x, float y, float z)
	{		
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double laskeNopeus(double alkunopeus, double kiihtyvyys_keskiarvo, double aika_s)
	{
		return alkunopeus + kiihtyvyys_keskiarvo * aika_s;
	}
	
	public static double laskeTeho(double kokonais_massa, double kiihtyvyys, double nopeus)
	{
		return (kokonais_massa * kiihtyvyys * nopeus) / (2 * Math.PI);
	}

}
