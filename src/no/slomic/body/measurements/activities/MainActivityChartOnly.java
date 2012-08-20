package no.slomic.body.measurements.activities;

import java.util.List;
import java.util.TreeSet;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.charts.MeasurementChart;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.storage.WeightMeasurementDAO;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivityChartOnly extends Activity
{
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.layout_tab_weight_chart);
	        
	        
			WeightMeasurementDAO weightMeasurementDAO = new WeightMeasurementDAO(this);
			weightMeasurementDAO.open();
			//TODO: denne kan optimaliseres
			TreeSet<Measurement> measurementSet = new TreeSet<Measurement>();
			List<Measurement> measurements = weightMeasurementDAO.getAll();
			measurementSet.addAll(measurements);
			weightMeasurementDAO.close();
			
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
	        MeasurementChart psc = new MeasurementChart(measurementSet);
	        View chartView = psc.execute(this);
	        layout.addView(chartView);
	    }
}
