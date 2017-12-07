package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-07.
 */
//This is not retrieved from database. Only used so we can
//Send it to the fragments through property change event

public class Date implements DataEntry {

    private String date;

    @Override
    public String getName() {
        return date;
    }

    @Override
    public String getId() {
        return date;
    }

    @Override
    public String getCreationDate() {
        return date;
    }

    @Override
    public String getIdName() {
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }
}
