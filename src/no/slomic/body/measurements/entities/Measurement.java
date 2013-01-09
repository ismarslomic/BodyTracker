// Restrukturert: ok

package no.slomic.body.measurements.entities;

import org.joda.time.DateTime;

public class Measurement implements Comparable<Measurement> {
    private Quantity mQuantity;
    private DateTime mDate;
    private Measurement mPrevious;

    /**
     * @param quantity the value of the measurement
     * @param date the date when measurement were taken
     */
    public Measurement(Quantity quantity, DateTime date) {
        this.mQuantity = quantity;
        this.mDate = date;
    }

    /**
     * @return the previous measurement
     */
    public Measurement getPrevious() {
        return this.mPrevious;
    }

    /**
     * @param previous measurement
     */
    public void setPrevious(Measurement previous) {
        this.mPrevious = previous;
    }

    /**
     * @return the quantity
     */
    public Quantity getQuantity() {
        return this.mQuantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Quantity quantity) {
        this.mQuantity = quantity;
    }

    /**
     * @return the measurement date
     */
    public DateTime getDate() {
        return this.mDate;
    }

    /**
     * @param measurement date the date to set
     */
    public void setDate(DateTime date) {
        this.mDate = date;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object) Sorting DESC on
     * measurement date
     */
    @Override
    public int compareTo(Measurement another) {
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
        if (this.getDate() == null || other.getDate() == null) {
            return false;
        } else if (!this.getDate().equals(other.getDate()))
            return false;
        else
            return true;
    }
}
