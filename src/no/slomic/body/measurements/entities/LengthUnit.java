package no.slomic.body.measurements.entities;



public class LengthUnit extends BaseUnit {
	// SI units
	public static final LengthUnit m = new LengthUnit("m", "meter", 1.0E+2); 
    public static final LengthUnit cm = new LengthUnit("cm", "centimeter", 1.0);
	
	// US customary units
    public static final LengthUnit in = new LengthUnit("in", "inch", 2.54);
	
	// Base SI unit
	public static final LengthUnit REF_UNIT = cm; // reference Unit

	public LengthUnit(String symbol, String name, double multipleFactor) {
		super(symbol, name, multipleFactor);
	}

	@Override
	public Unit getSystemUnit() 
	{
		return REF_UNIT;
	}

}
