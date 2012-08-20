package no.slomic.body.measurements.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Quantity 
{
	private Unit unit;
	private double valueInRefUnit;
	private double valueInCurrentUnit;
	public static final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
	
	/**
	 * @param unit
	 * @param value
	 */
	public Quantity(double value, Unit unit) 
	{	
		this.unit = unit;
		this.valueInRefUnit = value * unit.getMultipleFactor();
		this.valueInCurrentUnit = value;
	}
	
	public Unit getUnit() 
	{
		return this.unit;
	}
	

	public double getValue() 
	{
		return this.valueInCurrentUnit;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return decimalFormat.format(getValue()) + " " + unit;
	}

    public String showInUnits(Unit u, int precision) {
        double result = valueInRefUnit / u.getMultipleFactor();
        return roundValue(precision, result) + " " + u;
    }
    
    public double showInUnits(Unit u)
    {
    	return valueInRefUnit / u.getMultipleFactor();
    }

    public String roundValue(int precision) {
        return roundValue(precision, getValue());
    }
    
    private String roundValue(int precision, double value)
    {
    	boolean isNegative = false;
    	
    	if( value < 0 )
    	{
    		value = Math.abs(value);
    		isNegative = true;
    	}
  
        String str = Double.toString(value);
        char cs[] = str.toCharArray();
        int i = 0;
        while (i < cs.length && (cs[i] >= '0' && cs[i] <= '9' || cs[i] == '.'))
            i++;
        
        if( i != 0)
        {
	        BigDecimal bd = new BigDecimal(new String(cs, 0, i));
	        BigDecimal bd2 = bd.setScale(precision, RoundingMode.HALF_UP);
	        str = bd2.toString();
        }
        

        String exp = "";
        if (i < cs.length)
            exp = new String(cs, i, cs.length - i);

        str = isNegative ? "-" + str : str;
        
        return str + exp;
    }
    
    public Quantity add(Quantity quantity2, Unit refUnit) 
    {
    	Unit unit;
    	double valueInRefUnit;
    	double valueInCurrentUnit;
    	Quantity result;
    	
    	 // if units on both quantities are equal do the addition on both values
    	if (this.getUnit() == quantity2.getUnit())
        {
    		unit = this.getUnit();
    		valueInRefUnit = this.valueInRefUnit + quantity2.valueInRefUnit;
        	valueInCurrentUnit = this.valueInCurrentUnit + quantity2.valueInCurrentUnit;
        }
    	// if the units on the quantities are not equal do 
    	// the addition on values in reference unit and set both values-variables to same value
        else 
        {
        	unit = refUnit;
        	valueInRefUnit = this.valueInRefUnit +  quantity2.valueInRefUnit;
        	valueInCurrentUnit = valueInRefUnit;
        }
        
    	result = new Quantity(valueInCurrentUnit, unit);
    	result.valueInRefUnit = valueInRefUnit;
    	
        return result;
    }
    
    public Quantity subtract(Quantity quantity2, Unit refUnit) 
    {
    	Unit unit;
    	double valueInRefUnit;
    	double valueInCurrentUnit;
    	Quantity result;
    	
    	 // if units on both quantities are equal do the subtract on both values
    	if (this.getUnit() == quantity2.getUnit())
        {
    		unit = this.getUnit();
    		valueInRefUnit = this.valueInRefUnit - quantity2.valueInRefUnit;
        	valueInCurrentUnit = this.valueInCurrentUnit - quantity2.valueInCurrentUnit;
        }
    	// if the units on the quantities are not equal do 
    	// the subtract on values in reference unit and set both values-variables to same value
        else 
        {
        	unit = refUnit;
        	valueInRefUnit = this.valueInRefUnit - quantity2.valueInRefUnit;
        	valueInCurrentUnit = valueInRefUnit;
        }
        
    	result = new Quantity(valueInCurrentUnit, unit);
    	result.valueInRefUnit = valueInRefUnit;
    	
        return result;
    }
    
    public Quantity convert(WeightUnit newUnit) 
    {
    	return new Quantity(valueInRefUnit / newUnit.getMultipleFactor(), newUnit);
    }
}
