
package no.slomic.body.measurements.entities;

public class LengthUnit extends BaseUnit {
    // SI units
    public static final LengthUnit M = new LengthUnit("m", "meter", 1.0E+2);
    public static final LengthUnit CM = new LengthUnit("cm", "centimeter", 1.0);

    // US customary units
    public static final LengthUnit IN = new LengthUnit("in", "inch", 2.54);
    public static final LengthUnit FT = new LengthUnit("ft", "foot", 30.48);
   

    // Base SI unit
    public static final LengthUnit REF_UNIT = CM; // reference Unit

    public LengthUnit(String symbol, String name, double multipleFactor) {
        super(symbol, name, multipleFactor);
    }

    @Override
    public Unit getSystemUnit() {
        return REF_UNIT;
    }

}
