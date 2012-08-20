package no.slomic.body.measurements.charts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.WeightUnit;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class MeasurementChart extends AbstractChart {
	private TreeSet<Measurement> measurements;
	
	public MeasurementChart(TreeSet<Measurement> measurements)
	{
		this.measurements = measurements;
	}
	
	public View execute(Context context) {
		String[] titles = new String[] { "This period" };
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();

		
		Iterator<Measurement> iterator = measurements.iterator();
		
		// Serie 1
		dates.add(new Date[measurements.size()]);
		values.add(new double[measurements.size()]);
		int index = 0;
		double minValue = Double.MAX_VALUE;
		double maxValue = 0;
		Date minDate = Calendar.getInstance().getTime();
		Date maxDate = Calendar.getInstance().getTime();
		while(iterator.hasNext())
		{
			Measurement m = iterator.next();
			dates.get(0)[index] = m.getDate().getTime();
			double value = m.getQuantity().getValue();
			
			// Get min and max value
			if( value < minValue )
				minValue = value;
			if( value > maxValue )
				maxValue = value;
			values.get(0)[index] = value;
			index++;
		}
		
		// Get min and max date
		if(measurements.size() >= 1)
		{
			minDate = measurements.last().getDate().getTime();
			maxDate = measurements.first().getDate().getTime();
		}
		

		int[] colors = new int[] { Color.LTGRAY };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		setChartSettings(renderer, "", "", "", minDate.getTime(),
				maxDate.getTime(), minValue, maxValue, Color.GRAY, Color.LTGRAY);

		renderer.setXLabels(3);
		renderer.setYLabels(5);

		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer seriesRenderer = renderer
					.getSeriesRendererAt(i);
			//seriesRenderer.setDisplayChartValues(true);
		}

		GraphicalView view = ChartFactory.getTimeChartView(context,
				buildDateDataset(titles, dates, values), renderer,
				"EE d. MMM yy");

		return view;
	}

}
