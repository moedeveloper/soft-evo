package android.app.printerapp.model;

/**
 * Created by SAMSUNG on 2017-11-19.
 */

public class BuildDetailLink {

    private String id;
    private String buildId;
    private String detailsId;

    public void setId(String id){
        this.id = id;
    }

    public void setBuildId(String id){
        this.buildId = id;
    }

    public void setDetails(String id){
        this.detailsId = id;
    }
    public String getId(){
        return id;
    }

    public String getBuildId(){
        return buildId;
    }

    public String getDetailsId(){
        return detailsId;
    }
}
