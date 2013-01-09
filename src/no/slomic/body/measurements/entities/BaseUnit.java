// Restrukturert: ok

package no.slomic.body.measurements.entities;

public abstract class BaseUnit implements Unit {
    private String mSymbol; // e.g. "A"
    private String mName; // e.g. "Angstrom"
    private String mSymbolPlural;
    private String mNamePlural; // e.g. "Angstrom"
    private double mMultipleFactor; // e.g. 1E-10

    public BaseUnit(String symbol, String name, double multipleFactor) {
        this.mSymbol = symbol;
        this.mName = name;
        this.mSymbolPlural = symbol;
        this.mNamePlural = name;
        this.mMultipleFactor = multipleFactor;
    }

    /**
     * @param symbol - the symbol of the unit
     * @param symbolPlural - the symbol of the unit in plural form
     * @param name - the name of the unit
     * @param namePlural - the name of the unit in plural form
     * @param multipleFactor - factor to multiple
     */
    public BaseUnit(String symbol, String symbolPlural, String name, String namePlural,
            double multipleFactor) {
        this.mSymbol = symbol;
        this.mName = name;
        this.mSymbolPlural = symbolPlural;
        this.mNamePlural = namePlural;
        this.mMultipleFactor = multipleFactor;
    }

    @Override
    public String getSymbol() {
        return this.mSymbol;
    }

    public String getSymbolPlural() {
        return this.mSymbolPlural;
    }

    @Override
    public double getMultipleFactor() {
        return this.mMultipleFactor;
    }

    @Override
    public String getName() {
        return this.mName;
    }

    public String getNamePlural() {
        return this.mNamePlural;
    }

    @Override
    public Unit getSystemUnit() {
        return this;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof BaseUnit))
            return false;
        BaseUnit thatUnit = (BaseUnit) that;
        return this.mSymbol.equals(thatUnit.mSymbol);
    }

    @Override
    public int hashCode() {
        return this.mSymbol.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getSymbol();
    }
}
