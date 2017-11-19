package android.app.printerapp.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Shireenyu on 2017-11-19.
 */

public class CompanyList {
    List<Company> companiesApi;

    public CompanyList() {
        companiesApi =new LinkedList<>();
    }

    public List<Company> getCompaniesApi() {
        return companiesApi;
    }

    public void setCompaniesApi(List<Company> companiesApi) {
        this.companiesApi = companiesApi;
    }
}
