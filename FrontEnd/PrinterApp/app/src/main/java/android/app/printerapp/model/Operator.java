package android.app.printerapp.model;

import android.provider.ContactsContract;

/**
 * Created by SAMSUNG on 2017-12-05.
 */

public class Operator implements DataEntry{
    private String id;
    private String firstname;
    private String lastname;

    @Override
    public String getName() {
        return firstname + " " + lastname;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getCreationDate() {
        return "";
    }

    @Override
    public String getIdName() {
        return getName() + " : " + id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfirstname() {
        return firstname;
    }

    public void setfirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getlastname() {
        return lastname;
    }

    public void setlastname(String lastname) {
        this.lastname = lastname;
    }

}
