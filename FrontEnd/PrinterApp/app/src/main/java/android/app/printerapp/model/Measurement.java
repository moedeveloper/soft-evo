package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-06.
 */

public class Measurement implements DataEntry{
    private String id;
    private String name;
    private String hallflowTestId;
    private String measurementValue;
    private String measurementUnit = "s/20m^3";

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public String getHallflowTestId() {
        return hallflowTestId;
    }

    public void setHallflowTestId(String hallflowTestId) {
        this.hallflowTestId = hallflowTestId;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(String measurementValue) {
        this.measurementValue = measurementValue;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    private String testId;

    public String getId() {
        return id;
    }

    @Override
    public String getCreationDate() {
        return "";
    }

    @Override
    public String getIdName() {
        return "Measurement" + id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
