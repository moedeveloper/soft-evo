package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-07.
 */

public class TestType implements DataEntry{
    private String name;
    private String id;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCreationDate() {
        return "";
    }

    @Override
    public String getIdName() {
        return "";
    }

    public void setId(String id) {
        this.id = id;
    }


}
