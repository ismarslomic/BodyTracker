
package no.slomic.body.measurements.entities;

public class WeightUnit extends BaseUnit {
    // SI units
    public static final WeightUnit G = new WeightUnit("g", "gram", 1.0);
    public static final WeightUnit KG = new WeightUnit("kg", "kilogram", 1.0e+3);

    // US customary units
    public static final WeightUnit LB = new WeightUnit("lb", "pound", 453.59237);
    public static final WeightUnit OZ = new WeightUnit("oz", "ounce", 28.349523125);
    
    // Base SI unit
    public static final WeightUnit REF_UNIT = G; // reference Unit

    public WeightUnit(String symbol, String name, double multipleFactor) {
        super(symbol, name, multipleFactor);
    }

    @Override
    public Unit getSystemUnit() {
        return REF_UNIT;
    }

}
