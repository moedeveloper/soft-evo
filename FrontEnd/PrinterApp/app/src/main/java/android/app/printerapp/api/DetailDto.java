package android.app.printerapp.api;

import android.app.printerapp.model.Detail;

/**
 * Created by Shireenyu on 2017-11-22.
 */

public class DetailDto {
    //  { "name" : "insert now", "companyId": "5", "projectId":"3", "creationDate": "2017-11-6 14:26:00", "comment" : "teststing insert" }
    private String name;
    private String companyId;
    private String fileId;
    private String projectId;
    private String comment;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public DetailDto() {
    }

    public DetailDto(Detail detail) {
        if(detail == null) return;

        name = detail.getName();
        companyId = String.valueOf(detail.getCompanyId());
        projectId = String.valueOf(detail.getProjectId());
        comment = detail.getComment();
        fileId = detail.getFileId();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
