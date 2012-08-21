
package no.slomic.body.measurements.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtils {
    public static int getWholePartOfValue(double value) {
        return (int) value;
    }

    public static int getFractionalPartOfValue(double value) {
        int wholePart = getWholePartOfValue(value);
        double fraction = value - wholePart;
        fraction = Double.parseDouble(roundValue(3, fraction));

        int fractionInt = (int) (fraction * 10E2);

        return fractionInt;

    }

    public static double getValue(int wholePartOfValue, int fractionalPartOfValue) {
        double fractionalPart = fractionalPartOfValue / (10E2);
        double value = wholePartOfValue + fractionalPart;

        return value;
    }

    private static String roundValue(int precision, double value) {
        boolean isNegative = false;

        if (value < 0) {
            value = Math.abs(value);
            isNegative = true;
        }

        String str = Double.toString(value);
        char cs[] = str.toCharArray();
        int i = 0;
        while (i < cs.length && (cs[i] >= '0' && cs[i] <= '9' || cs[i] == '.'))
            i++;

        if (i != 0) {
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

}
