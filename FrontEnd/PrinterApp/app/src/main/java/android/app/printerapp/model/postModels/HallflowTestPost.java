package android.app.printerapp.model.postModels;

/**
 * Created by SAMSUNG on 2017-12-07.
 */

public class HallflowTestPost{
    String operatorId;
    String date;
    String relativeHumidity;
    String temperature;
    String tap;
    String measurementId;
    String materialId;
    String machineId;

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(String relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTap() {
        return tap;
    }

    public void setTap(String tap) {
        this.tap = tap;
    }

    public String getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(String measurementId) {
        this.measurementId = measurementId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String mataterialId) {
        this.materialId = mataterialId;
    }
}