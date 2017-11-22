package android.app.printerapp.model;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-11-22.
 */

public class BuildList {

    List<Build> buildsApi;


    public List<Build> getBuilds() { return buildsApi;}

    public void setBuilds(List<Build> builds) {
        this.buildsApi = builds;
    }
}
