package fi.savonia.etx10sp.mobiilidyno;

import java.io.Serializable;

public class Mittaus implements Serializable
{
	long TimeStamp;
	float X;
	float Y;
	float Z;
	
	public Mittaus(long TimeStamp, float X, float Y, float Z)
	{
		this.TimeStamp = TimeStamp;
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}
	
	@Override
	public String toString()
	{
		return new StringBuilder()
	    .append(this.TimeStamp + " ")
	    .append(this.X + " ")
	    .append(this.Y + " ")
	    .append(this.Z).toString();
	}
}
