package android.app.printerapp.api;

/**
 * Created by Shireenyu on 2017-11-19.
 */

public class CompanyDTO {
    private String name;

    public CompanyDTO() {
    }

    public CompanyDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
