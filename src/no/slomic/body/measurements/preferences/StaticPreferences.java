
package no.slomic.body.measurements.preferences;

import no.slomic.body.measurements.entities.LengthUnit;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.WeightUnit;

public class StaticPreferences {
    public static final Quantity AVERAGE_MALE_WEIGHT = new Quantity(75, WeightUnit.KG);
    public static final Quantity AVERAGE_FEMALE_WEIGHT = new Quantity(65, WeightUnit.KG);
    public static final Quantity AVERAGE_MALE_WAIST = new Quantity(96.5, LengthUnit.CM);
    public static final Quantity AVERAGE_FEMALE_WAIST = new Quantity(81.3, LengthUnit.CM);
    public static final String APP_PREFERENCE_NS = "http://schemas.android.com/apk/res/no.slomic.body.measurements";
}
