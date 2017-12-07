package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-12-07.
 */

//Only used by spinners to allow selecting nothing

public class NoDataSelected implements DataEntry {
    public static final String NO_ITEM = "No item selected";

    @Override
    public String getName() {
        return NO_ITEM;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getCreationDate() {
        return NO_ITEM;
    }

    @Override
    public String getIdName() {
        return NO_ITEM;
    }
}
