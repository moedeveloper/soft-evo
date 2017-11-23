package android.app.printerapp.api;

import android.app.printerapp.model.BuildList;
import android.app.printerapp.model.CompanyList;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.BuildDetailLinkList;
import android.app.printerapp.model.Company;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.DetailList;
import android.app.printerapp.model.Print;
import android.app.printerapp.model.PrintList;
import android.app.printerapp.model.Build;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


/**
 * Created by Johan on 11/1/2017.
 */

public interface ApiService {
    String BASE_URL = "http://mo3app.azurewebsites.net/api/";
//-------------------------------------------
//      DETAILS
//-------------------------------------------
    @GET("details")
    Call<DetailList> fetchAllDetails();

    @GET("details/{detailId}")
    Call<List<Detail>> fetchDetail(@Path("detailId") int detailId);

    @GET("details/build/{buildId}")
    Call<List<BuildDetailLink>> fetchDetailBuildLink(@Path("buildId") int buildId);

    @Streaming
    @GET ("download/file/{fileId}")
    Call<ResponseBody> downloadStlFile(@Path("fileId") int fileId);

    //-------------------------------------------
//      PRINTS
//-------------------------------------------
    @GET("print/{printId}")
    Call<List<Print>> fetchPrint(@Path("printId") int printId);

    @GET("prints")
    Call<PrintList> fetchAllPrints();

    @GET("print/build/{buildId}")
    Call<List<Print>> fetchPrintFromBuild(@Path("buildId") int buildId);

//-------------------------------------------
//      BUILDS
//-------------------------------------------
    @GET("build/{buildId}")
    Call<List<Build>> fetchBuild(@Path("buildId") int buildId);

    @GET("builds")
    Call<BuildList> fetchAllBuilds();

//-------------------------------------------
//      COMPANIES
//-------------------------------------------
    @GET("companies")
    Call<CompanyList> fetchAllCompanies();

    @Headers({
            "Accept: application/json",
            "Content-Type: text/html; charset=utf-8"
    })
    @POST("company/create")
    Call<String> createNewCompany(@Body RequestBody companyDetails);

    @POST("company/create")
    Call<String> createNewCompany(@Body CompanyDTO company);
}
