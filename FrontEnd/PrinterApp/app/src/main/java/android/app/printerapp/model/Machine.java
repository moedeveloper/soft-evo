package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-06.
 */

public class Machine implements DataEntry{
    private String id;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    @Override
    public String getCreationDate() {
        return "";
    }

    @Override
    public String getIdName() {
        return "Machine" + id;
    }
}
