package android.app.printerapp.model;

import java.util.List;

/**
 * Created by Johan on 11/2/2017.
 */

public class DetailList {
    private List<Detail> detailsApi;

    public List<Detail> getDetails() {
        return detailsApi;
    }

    public void setDetails(List<Detail> details) {
        this.detailsApi = details;
    }
}
