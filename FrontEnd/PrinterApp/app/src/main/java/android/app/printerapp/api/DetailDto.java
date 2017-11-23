package android.app.printerapp.api;

import android.app.printerapp.model.Detail;

/**
 * Created by Shireenyu on 2017-11-22.
 */

public class DetailDto {
    //  { "name" : "insert now", "companyId": "5", "projectId":"3", "creationDate": "2017-11-6 14:26:00", "comment" : "teststing insert" }
    private String name;
    private String companyId;
    private String fieldId;
    private String projectId;
    private String creationDate;
    private String comment;

    public DetailDto() {
    }

    public DetailDto(Detail detail) {
        if(detail == null) return;

        name = detail.getName();
        companyId = String.valueOf(detail.getCompanyId());
        fieldId = detail.getFileId();
        projectId = String.valueOf(detail.getProjectId());
        creationDate = detail.getCreationDate();
        comment = detail.getComment();
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

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
