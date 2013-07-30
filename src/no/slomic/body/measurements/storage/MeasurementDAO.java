
package no.slomic.body.measurements.storage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import android.database.SQLException;

public interface MeasurementDAO {

    public abstract void open() throws SQLException;

    public abstract void close();

    public abstract Measurement create(Measurement newMeasurement);

    public abstract void delete(Measurement measurement);

    public abstract void deleteAll(List<Measurement> measurements);

    public abstract List<Measurement> getAll();

    public abstract void exportAll(File exportFile) throws IOException;

    public abstract TreeSet<Measurement> getAllLastWeek();

    public abstract Quantity lastWeekStatistics();

    public abstract Measurement getLatest();
}
