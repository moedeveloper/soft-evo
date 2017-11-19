package android.app.printerapp.model;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-11-19.
 */

public class BuildDetailLinkList {

    private List<BuildDetailLink> BuildDetailLinkApi;

    public List<BuildDetailLink> getBuildDetailLinks() {
        return BuildDetailLinkApi;
    }

    public void setBuildDetailLinks(List<BuildDetailLink> BuildDetailLinks) {
        this.BuildDetailLinkApi = BuildDetailLinks;
    }
}
