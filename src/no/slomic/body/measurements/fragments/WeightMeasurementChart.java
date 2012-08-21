
package no.slomic.body.measurements.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.charts.MeasurementChart;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.storage.WeightMeasurementDAO;

import java.util.List;
import java.util.TreeSet;

public class WeightMeasurementChart extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return (LinearLayout) inflater.inflate(R.layout.layout_tab_weight_chart, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WeightMeasurementDAO weightMeasurementDAO = new WeightMeasurementDAO(getActivity());
        weightMeasurementDAO.open();
        // TODO: denne kan optimaliseres
        TreeSet<Measurement> measurementSet = new TreeSet<Measurement>();
        List<Measurement> measurements = weightMeasurementDAO.getAll();
        measurementSet.addAll(measurements);
        weightMeasurementDAO.close();

        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart);
        MeasurementChart psc = new MeasurementChart(measurementSet);
        View chartView = psc.execute(getActivity());
        layout.addView(chartView);
    }
}
