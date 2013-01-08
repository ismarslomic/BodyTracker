/**
 * 
 */

package no.slomic.body.measurements.utils;

import no.slomic.body.measurements.entities.LengthUnit;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.WeightUnit;

import java.text.DecimalFormat;

public class QuantityStringFormat {
    public static final DecimalFormat WEIGHT_ONE_DECIMAL_FORMAT = new DecimalFormat("#,##0.0");
    public static final DecimalFormat WEIGHT_TWO_DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    public static final DecimalFormat LENGTH_DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    /**
     * Formats the quantity value of weight measurement in kilograms and grams
     * 
     * @param q the quantity of weight to format the string
     * @return formatted string of quantity value. If value > 10 kg then one
     *         decimal is returned (i.e. 75.5 kg). If between 1 and 10 kg then two
     *         decimals are returned (i.e. 5.65 kg). If value < 1 kg then only
     *         gram is returned (i.e. 100 g or 50 g). If the quantity is empty or
     *         not valid empty string will be returned
     */
    public static String formatWeightToMetric(Quantity q) {
        // if quantity is empty return empty string
        if (q == null)
            return "";

        // get value in kilograms
        int valueInKg = (int) q.showInUnits(WeightUnit.KG);

        // if value >= 10 kg return with one decimal and kg as unit
        if (valueInKg >= 10)
            return WEIGHT_ONE_DECIMAL_FORMAT.format(q.showInUnits(WeightUnit.KG)) + " "
                    + WeightUnit.KG.toString();
        // if value between 1 and 10 kg return with two decimals and kg as unit
        else if (valueInKg < 10 && valueInKg > 0)
            return WEIGHT_TWO_DECIMAL_FORMAT.format(q.showInUnits(WeightUnit.KG)) + " "
                    + WeightUnit.KG.toString();
        // if value < 1 kg return in gram
        else
            return (int) q.showInUnits(WeightUnit.G) + " " + WeightUnit.G.toString();
        
        // if value < 1 kg
        /*
        if ((int) q.showInUnits(WeightUnit.KG) == 0)
            return (int) q.showInUnits(WeightUnit.G) + " " + WeightUnit.G.toString();
        else
            return WEIGHT_ONE_DECIMAL_FORMAT.format(q.showInUnits(WeightUnit.KG)) + " "
                    + WeightUnit.KG.toString();*/
    }
    
    /**
     * Formats the quantity value of weight measurement in pounds and ounces
     * 
     * @param q the quantity of weight to format the string
     * @return formatted string of quantity value. If value > 10 kg then pounds (in plural form) and ounces with zero decimals is returned. 
     *         If between 0,45 kg (1 pound) and 10 kg then pounds (in plural form if > 1) with zero decimal and ounces with one decimal is returned. If value < 0,45 (1 pound) kg then only
     *         ounces with one decimal is returned. If the quantity is empty or
     *         not valid empty string will be returned
     */
    public static String formatWeightToImperial(Quantity q) {
        // if quantity is empty return empty string
        if (q == null)
            return "";

        // get value in pounds and ounces
        int pounds = (int) q.showInUnits(WeightUnit.LB);
        double ounces = q.showInUnits(WeightUnit.OZ);

        // if value >= 10 kg return pounds (in plural form) and ounces with zero decimals
        if (pounds >= 22)
        {
            pounds = (int) (ounces / 16);
            ounces = Math.abs(ounces) % 16; // so we don't get -4lb -6,55oz
            return pounds + WeightUnit.LB.getSymbolPlural() + " " + (int) ounces + WeightUnit.OZ.getSymbol();
        }
        // if value between 0,45 and 10 kg return pounds (in plural form if > 1) with zero decimal and ounces with one decimal
        else if (pounds < 22 && pounds > 0)
        {
            pounds = (int) (ounces / 16);
            ounces = Math.abs(ounces) % 16; // so we don't get -4lb -6,55oz
            
            // return pounds in plural form
            if(pounds > 1)
                return pounds + WeightUnit.LB.getSymbolPlural() + " " + WEIGHT_ONE_DECIMAL_FORMAT.format(ounces) + WeightUnit.OZ.getSymbol();
            else
                return pounds + WeightUnit.LB.getSymbol() + " "+ WEIGHT_ONE_DECIMAL_FORMAT.format(ounces) + WeightUnit.OZ.getSymbol();
        }
        // if value < 0,4535 kg return ounces with one decimal
        else
            return WEIGHT_ONE_DECIMAL_FORMAT.format(q.showInUnits(WeightUnit.OZ)) + " " + WeightUnit.OZ.getSymbol();
    }

    public static String formatLengthToMetric(Quantity q) {
        if ((int) q.showInUnits(LengthUnit.MM) < 10 && (int) q.showInUnits(LengthUnit.MM) > -10)
            return (int) q.showInUnits(LengthUnit.MM) + " " + LengthUnit.MM.toString();
        if ((int) q.showInUnits(LengthUnit.CM) < 100 && (int) q.showInUnits(LengthUnit.CM) > -100)
            return (int) q.showInUnits(LengthUnit.CM) + " " + LengthUnit.CM.toString();
        else
            return LENGTH_DECIMAL_FORMAT.format(q.showInUnits(LengthUnit.M)) + " "
                    + LengthUnit.M.toString();
    }
    
    public static String formatLengthToImperial(Quantity q) {
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
