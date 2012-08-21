
package no.slomic.body.measurements.entities;

public abstract class BaseUnit implements Unit {
    private String symbol; // e.g. "A"
    private String name; // e.g. "Angstrom"
    private double multipleFactor; // e.g. 1E-10

    public BaseUnit(String symbol, String name, double multipleFactor) {
        this.symbol = symbol;
        this.name = name;
        this.multipleFactor = multipleFactor;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public double getMultipleFactor() {
        return this.multipleFactor;
    }

    public String getName() {
        return this.name;
    }

    public Unit getSystemUnit() {
        return this;
    }

    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof BaseUnit))
            return false;
        BaseUnit thatUnit = (BaseUnit) that;
        return this.symbol.equals(thatUnit.symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
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
