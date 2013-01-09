// Restrukturert: ok

package no.slomic.body.measurements.entities;

public class WeightUnit extends BaseUnit {
    // Metric units http://en.wikipedia.org/wiki/Metric_system
    public static final WeightUnit G = new WeightUnit("g", "gram", 1.0);
    public static final WeightUnit KG = new WeightUnit("kg", "kilogram", 1.0e+3);

    // Imperial units http://en.wikipedia.org/wiki/Imperial_units
    public static final WeightUnit LB = new WeightUnit("lb", "lbs", "pound", "pounds", 453.59237);
    public static final WeightUnit OZ = new WeightUnit("oz", "ounce", 28.349523125);

    // Base Metric unit
    public static final WeightUnit REF_UNIT = G; // reference Unit

    public WeightUnit(String symbol, String name, double multipleFactor) {
        super(symbol, name, multipleFactor);
    }

    public WeightUnit(String symbol, String symbolPlural, String name, String namePlural,
            double multipleFactor) {
        super(symbol, symbolPlural, name, namePlural, multipleFactor);
    }

    @Override
    public Unit getSystemUnit() {
        return REF_UNIT;
    }
}
