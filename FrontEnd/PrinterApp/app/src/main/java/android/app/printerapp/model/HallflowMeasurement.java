package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-06.
 */

public class HallflowMeasurement{
    private String id;
    private String value;
    private String hallflowTestId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getHallflowTestId() {
        return hallflowTestId;
    }

    public void setHallflowTestId(String hallflowTestId) {
        this.hallflowTestId = hallflowTestId;
    }
}
