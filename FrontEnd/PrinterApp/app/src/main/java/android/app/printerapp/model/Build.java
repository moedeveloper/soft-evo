package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-11-18.
 */

public class Build implements DataEntry{

    private String buildDetailLinkId;

    public String getBuildDetailLinkId(){
        return buildDetailLinkId;
    }

    @Override
    public String getName() {
        return "B" + getId();
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getCreationDate() {
        return null;
    }
}


