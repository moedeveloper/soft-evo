package android.app.printerapp.model;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-12-06.
 */

public class MeasurementList {
    public List<Measurement> getMeasurementsApi() {
        return measurementsApi;
    }

    public void setMeasurementsApi(List<Measurement> measurementsApi) {
        this.measurementsApi = measurementsApi;
    }

    private List<Measurement> measurementsApi;
}
