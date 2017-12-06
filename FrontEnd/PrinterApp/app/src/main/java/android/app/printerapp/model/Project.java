package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-06.
 */

public class Project implements DataEntry {
    private String id;
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
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
        return "Project" + id;
    }
}
