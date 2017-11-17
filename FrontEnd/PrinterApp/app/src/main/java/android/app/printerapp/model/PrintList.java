package android.app.printerapp.model;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-11-17.
 */

public class PrintList {
    private List<Print> printsApi;

    public List<Print> getPrints() {
        return printsApi;
    }

    public void setPrints(List<Print> prints) {
        this.printsApi = prints;
    }
}
