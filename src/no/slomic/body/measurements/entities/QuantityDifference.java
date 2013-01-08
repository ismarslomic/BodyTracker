// Restrukturert: ok

package no.slomic.body.measurements.entities;

public class QuantityDifference {
    public static final int EQUAL_TO = 0, GREATER_THAN = 1, LESS_THAN = -1;
    private Unit mUnit;
    private Number mValue;

    public QuantityDifference(double value, Unit unit) {
        this.mValue = value;
        this.mUnit = unit;
    }

    public QuantityDifference() {
    }

    /**
     * @param value the value to set
     */
    public void setValue(Number value) {
        this.mValue = value;
    }

    public void setUnit(Unit unit) {
        this.mUnit = unit;
    }

    /**
     * @return 0: if difference is zero/none, 1: if the difference is positive
     *         and -1 if the difference is negative Use constants EQUAL_TO,
     *         LESS_THAN and GREATH_THAN;
     */
    public int getCompareTo() {
        int compareTo;

        // Set compareTo
        if (this.mValue.doubleValue() == 0)
            compareTo = QuantityDifference.EQUAL_TO;
        else if (this.mValue.doubleValue() < 0)
            compareTo = QuantityDifference.LESS_THAN;
        else
            compareTo = QuantityDifference.GREATER_THAN;

        return compareTo;
    }

    /**
     * @return the unit of difference value
     */
    public Unit unit() {
        return mUnit;
    }

    /**
     * @return the difference value, can be positive or negative
     */
    public Number value() {
        return mValue;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.mValue + " " + mUnit;
    }
}
