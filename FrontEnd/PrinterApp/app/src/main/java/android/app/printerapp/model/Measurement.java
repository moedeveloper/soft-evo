package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-06.
 */

public class Measurement implements DataEntry{
    private String id;
    private String name;

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
