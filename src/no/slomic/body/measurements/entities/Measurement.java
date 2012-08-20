package no.slomic.body.measurements.entities;

import java.util.Calendar;

public class Measurement implements Comparable<Measurement>
{
	private Quantity quantity;
	private Calendar date;
	private Measurement previous;
	
	/**
	 * @param quantity the value of the measurement
	 * @param date the date when measurement were taken
	 */
	public Measurement(Quantity quantity, Calendar date) 
	{
		this.quantity = quantity;
		this.date = date;
	}
	
	/**
	 * @return the previous measurement
	 */
	public Measurement getPrevious() {
		return previous;
	}

	/**
	 * @param previous measurement
	 */
	public void setPrevious(Measurement previous) {
		this.previous = previous;
	}
	
	/**
	 * @return the quantity
	 */
	public Quantity getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the measurement date
	 */
	public Calendar getDate() {
		return date;
	}
	/**
	 * @param measurement date the date to set
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *
	 * Sorting DESC on measurement date
	 */
	@Override
	public int compareTo(Measurement another) 
	{
		if (another == null)
			return 1;
		else
			return another.getDate().compareTo(this.getDate());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Measurement other = (Measurement) obj;
		if (this.getDate() == null || other.getDate() == null) 
		{
			return false;
		} 
		else if (!this.getDate().equals(other.getDate()))
			return false;
		else
			return true;
	}
}
