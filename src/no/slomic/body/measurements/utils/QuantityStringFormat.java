/**
 * 
 */

package no.slomic.body.measurements.utils;

import no.slomic.body.measurements.entities.LengthUnit;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.WeightUnit;

import java.text.DecimalFormat;

/**
 * @author ismar.slomic
 */
public class QuantityStringFormat {
    public static final DecimalFormat WEIGHT_DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    public static final DecimalFormat LENGTH_DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    /**
     * @param q
     * @return
     */
    public static String formatWeightToSi(Quantity q) {
        if ((int) q.showInUnits(WeightUnit.KG) == 0)
            return (int) q.showInUnits(WeightUnit.G) + " " + WeightUnit.G.toString();
        else
            return WEIGHT_DECIMAL_FORMAT.format(q.showInUnits(WeightUnit.KG)) + " "
                    + WeightUnit.KG.toString();
    }

    /**
     * @param q
     * @return
     */
    public static String formatLengthToSi(Quantity q) {
        if ((int)q.showInUnits(LengthUnit.MM) < 10)
            return (int)q.showInUnits(LengthUnit.MM) + " " + LengthUnit.MM.toString();
        if ((int)q.showInUnits(LengthUnit.CM) < 100)
            return (int)q.showInUnits(LengthUnit.CM) + " " + LengthUnit.CM.toString();
        else
            return LENGTH_DECIMAL_FORMAT.format(q.showInUnits(LengthUnit.M)) + " "
                    + LengthUnit.M.toString();
    }

    /**
     * @param q
     * @return
     */
    public static String formatWeightToEnglish(Quantity q) {
        int pounds = (int) q.showInUnits(WeightUnit.LB);
        if (pounds >= 1 || pounds <= -1) {
            double ounces = q.showInUnits(WeightUnit.OZ);
            pounds = (int) (ounces / 16);
            ounces = Math.abs(ounces) % 16; // so we don't get -4lb -6,55oz
            return pounds + WeightUnit.LB.getSymbol() + " " + WEIGHT_DECIMAL_FORMAT.format(ounces)
                    + WeightUnit.OZ.getSymbol();
        } else
            return WEIGHT_DECIMAL_FORMAT.format(q.showInUnits(WeightUnit.OZ)) + " "
                    + WeightUnit.OZ.getSymbol();
    }

    /**
     * @param q
     * @return
     */
    public static String formatLengthToEnglish(Quantity q) {
        int feet = (int) q.showInUnits(LengthUnit.FT);
        if (feet >= 1 || feet <= -1) {
            double inches = q.showInUnits(LengthUnit.IN);
            feet = (int) (inches / 12);
            inches = Math.abs(inches) % 12;
            return feet + "' " + LENGTH_DECIMAL_FORMAT.format(inches) + "''";
        } else
            return LENGTH_DECIMAL_FORMAT.format(q.showInUnits(LengthUnit.IN)) + "''";
    }
}
