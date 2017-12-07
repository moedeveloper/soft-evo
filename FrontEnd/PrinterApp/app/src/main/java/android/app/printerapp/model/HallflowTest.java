package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-06.
 */

public class HallflowTest implements DataEntry{
    private String id;
    private String operatorId;
    private String date;
    private String relativeHumidity;
    private String temperature;
    private String tap;
    private String materialId;

    @Override
    public String getName() {
        return getIdName();
    }

    public String getId() {
        return id;
    }

    @Override
    public String getCreationDate() {
        return date;
    }

    @Override
    public String getIdName() {
        return "Hallflow test" + id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
}
