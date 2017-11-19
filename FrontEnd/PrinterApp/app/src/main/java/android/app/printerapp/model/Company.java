package android.app.printerapp.model;

/**
 * Created by Shireenyu on 2017-11-18.
 */

public class Company implements DataEntry {
    private String name;
    private int id;

    public Company() {
        this(null, 0);
    }

    public Company(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getCreationDate() {
        throw new NotImplementedYet();
    }

    private class NotImplementedYet extends RuntimeException {
    }

    @Override
    public String toString() {
        return name;
    }
}
