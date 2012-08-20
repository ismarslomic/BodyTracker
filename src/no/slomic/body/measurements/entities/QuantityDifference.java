package no.slomic.body.measurements.entities;

import org.unitsofmeasurement.quantity.Quantity;
import org.unitsofmeasurement.unit.Unit;

public class QuantityDifference <Q extends Quantity<Q>>
{
	public static final int EQUAL_TO = 0, GREATER_THAN = 1, LESS_THAN = -1;
	private Unit<Q> unit;
	private Number value;
	
	public QuantityDifference (double value, Unit<Q> unit)
	{
		this.value = value;
		this.unit = unit;
	}
	
	public QuantityDifference ()
	{
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(Number value) {
		this.value = value;
	}
	
	public void setUnit(Unit<Q> unit) {
		this.unit = unit;
	}
	
	/**
	 * @return 0: if difference is zero/none, 1: if the difference is positive and -1 if the difference is negative
	 * 
	 * Use constants EQUAL_TO, LESS_THAN and GREATH_THAN;
	 * 
	 */
	public int getCompareTo() 
	{
		int compareTo;
		
		// Set compareTo
		if (this.value.doubleValue() == 0)
			compareTo = QuantityDifference.EQUAL_TO;
		else if (this.value.doubleValue() < 0)
			compareTo = QuantityDifference.LESS_THAN;
		else
			compareTo = QuantityDifference.GREATER_THAN;
		
		return compareTo;
	}

	/**
	 * @return the unit of difference value
	 */
	public Unit<Q> unit() {
		return unit;
	}
	
	/**
	 * @return the difference value, can be positive or negative
	 */
	public Number value() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return this.value + " " + unit;
	}
}
