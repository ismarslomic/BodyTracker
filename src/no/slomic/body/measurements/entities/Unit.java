package no.slomic.body.measurements.entities;

public interface Unit {

	public String getSymbol();

	public double getMultipleFactor();

	public String getName();

	public Unit getSystemUnit();

}