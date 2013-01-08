
package no.slomic.body.measurements.preferences;

import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.WeightUnit;

public class StaticPreferences 
{
    public static final Quantity AVARAGE_MALE_WEIGHT = new Quantity(75, WeightUnit.KG);
    public static final Quantity AVARAGE_FEMALE_WEIGHT = new Quantity(65, WeightUnit.KG);
    public static final Quantity MIN_WEIGHT_QUANTITY = new Quantity(1.00, WeightUnit.KG);
    public static final Quantity MAX_WEIGHT_QUANTITY = new Quantity(500.00, WeightUnit.KG);
    public static final String APP_PREFERENCE_NS = "http://schemas.android.com/apk/res/no.slomic.body.measurements";
}
