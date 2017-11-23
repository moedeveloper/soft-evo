package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-11-18.
 */

public class Build implements DataEntry{

    private String id;
    private String creationDate;
    private String imageId;
    private String comment;

    @Override
    public String getName() {
        return "B" + getId();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCreationDate() {
        return creationDate;
    }

    @Override
    public String getIdName() {
        return "B" + id;
    }

    public String getComment() {
        return comment;
    }

    public String getImageId() {
        return imageId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setId(String id) {
        this.id = id;
    }
}


