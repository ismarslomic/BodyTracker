package no.slomic.body.measurements.holders;

import no.slomic.body.measurements.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MeasurementHolder 
{
	private View row;
	private TextView measurementValue = null, measurementDate = null, diffValue = null;
	private ImageView diffIcon = null;
	
	public MeasurementHolder(View row)
	{
		this.row = row;
	}
	
	public TextView getMeasurementValue()
	{
		if (this.measurementValue == null) {
	         this.measurementValue = (TextView) row.findViewById(R.id.measurement_value);
	      }
	      return this.measurementValue;
	}
	
	public TextView getMeasurementDate()
	{
		if (this.measurementDate == null) {
	         this.measurementDate = (TextView) row.findViewById(R.id.measurement_date);
	      }
	      return this.measurementDate;
	}
	
	public TextView getDiffValue()
	{
		if (this.diffValue == null) {
	         this.diffValue = (TextView) row.findViewById(R.id.diff_value);
	      }
	      return this.diffValue;
	}
	
	public ImageView getDiffIcon()
	{
		if (this.diffIcon == null) {
	         this.diffIcon = (ImageView) row.findViewById(R.id.diff_icon);
	      }
	      return this.diffIcon;
	}
}
