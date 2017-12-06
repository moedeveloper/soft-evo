package android.app.printerapp.model;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-12-06.
 */

public class ProjectList {
    private List<Project> projectApi;

    public List<Project> getProjects() {
        return projectApi;
    }

    public void setProjectList(List<Project> projectApi) {
        this.projectApi = projectApi;
    }
}
