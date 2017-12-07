package android.app.printerapp.api;

import android.app.printerapp.model.BuildList;
import android.app.printerapp.model.CompanyList;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.BuildDetailLinkList;
import android.app.printerapp.model.Company;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.DetailList;
import android.app.printerapp.model.HallFlowTestList;
import android.app.printerapp.model.HallflowTest;
import android.app.printerapp.model.Machine;
import android.app.printerapp.model.MachineList;
import android.app.printerapp.model.Material;
import android.app.printerapp.model.MaterialList;
import android.app.printerapp.model.Measurement;
import android.app.printerapp.model.MeasurementList;
import android.app.printerapp.model.Operator;
import android.app.printerapp.model.OperatorList;
import android.app.printerapp.model.Print;
import android.app.printerapp.model.PrintList;
import android.app.printerapp.model.Build;
import android.app.printerapp.model.Project;
import android.app.printerapp.model.ProjectList;

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
    @GET("download/file/{fileId}")
    Call<ResponseBody> downloadStlFile(@Path("fileId") int fileId);

    @GET("details/companyId/{companyId}")
    Call<List<Detail>> fetchDetailByCompany(@Path("companyId") int companyId);

    @POST("details/create")
    Call<String> createDetails(@Body DetailDto detail);

    @GET("details/date/{year}")
    Call<List<Detail>> fetchDetailByYear(@Path("year") String year);

    @GET("details/date/{year}/{month}")
    Call<List<Detail>> fetchDetailByYearMonth(@Path("year") String year, @Path("month") String month);

    @GET("details/filter/year/{year}/company/{companyId}/project/{projectId}")
    Call<List<Detail>> fetchDetailByFilter(@Path("year") String year, @Path("companyId") String companyId,
                                           @Path("projectId") String projectId);

    @GET("details/projectId/{projectId}")
    Call<List<Detail>> fetchDetailByProject(@Path("projectId") String projectId);


    //-------------------------------------------
//      MATERIALS
//-------------------------------------------
    @GET("materials")
    Call<MaterialList> fetchAllMaterials();

    @GET("material/{id}")
    Call<List<Material>> fetchMaterial(@Path("id") String id);

    @GET("material/date/{year}")
    Call<List<Material>> fetchMaterialsByYear(@Path("year") String year);

    @GET("material/date/{year}/{month}")
    Call<List<Material>> fetchMaterialsByYearMonth(@Path("year") String year, @Path("month") String month);


    //-------------------------------------------
//      PRINTS
//-------------------------------------------
    @GET("print/{printId}")
    Call<List<Print>> fetchPrint(@Path("printId") int printId);

    @GET("prints")
    Call<PrintList> fetchAllPrints();

    @GET("print/build/{buildId}")
    Call<List<Print>> fetchPrintFromBuild(@Path("buildId") int buildId);

    @GET("print/machine/{machineId}")
    Call<List<Print>> fetchPrintByMachine(@Path("machineId") String machineId);

    @GET("print/operator/{operatorId}")
    Call<List<Print>> fetchPrintFromOperator(@Path("operatorId") String operatorId);

    @GET("print/date/{year}")
    Call<List<Print>> fetchPrintByYear(@Path("year") String year);

    @GET("print/date/{year}/{month}")
    Call<List<Print>> fetchPrintByYearMonth(@Path("year") String year, @Path("month") String month);

    @GET("prints/filter/year/{year}/operator/{operatorId}/machine/{machineId}")
    Call<List<Print>> fetchPrintByFilter(@Path("year") String year, @Path("operatorId") String operatorId,
                                         @Path("machineId") String machineId);


    //-------------------------------------------
//      BUILDS
//-------------------------------------------
    @GET("build/{buildId}")
    Call<List<Build>> fetchBuild(@Path("buildId") int buildId);

    @GET("build/date/{year}")
    Call<List<Build>> fetchBuildByYear(@Path("year") String year);

    @GET("build/date/{year}/{month}")
    Call<List<Build>> fetchBuildByYearMonth(@Path("year") String year, @Path("month") String month);


    @GET("build/details/{detailId}")
    Call<List<BuildDetailLink>> fetchBuildDetailLink(@Path("detailId") int detailId);


    @GET("builds")
    Call<BuildList> fetchAllBuilds();

    //-------------------------------------------
//      COMPANIES
//-------------------------------------------
    @GET("companies")
    Call<CompanyList> fetchAllCompanies();

    @GET("company/{companyId}")
    Call<List<Company>> fetchCompany(@Path("companyId") int companyId);

    @Headers({
            "Accept: application/json",
            "Content-Type: text/html; charset=utf-8"
    })
    @POST("company/create")
    Call<String> createNewCompany(@Body RequestBody companyDetails);

    @POST("company/create")
    Call<String> createNewCompany(@Body CompanyDTO company);

    //-------------------------------------------
//      OPERATORS
//-------------------------------------------
    @GET("operators")
    Call<OperatorList> fetchAllOperators();

    @GET("operator/{operatorId}")
    Call<List<Operator>> fetchOperator(@Path("operatorId") int operatorId);

    //-------------------------------------------
//      PROJECTS
//-------------------------------------------
    @GET("projects")
    Call<ProjectList> fetchAllProjects();

    @GET("project/{projectId}")
    Call<List<Project>> fetchProject(@Path("projectId") int projectId);

    //-------------------------------------------
//      MACHINES
//-------------------------------------------
    @GET("machines")
    Call<MachineList> fetchAllMachines();

    @GET("machine/{machineId}")
    Call<List<Machine>> fetchMachine(@Path("machineId") int machineId);

    //-------------------------------------------
//      MEASUREMENTS
//-------------------------------------------
    @GET("measurements")
    Call<MeasurementList> fetchAllMeasurements();

    @GET("measurement/{measurementId}")
    Call<List<Measurement>> fetchMeasurement(@Path("measurementId") int measurementId);

    @GET("measurement/hallflowtest/{id}")
    Call<List<Measurement>> fetchMeasurementsByHallflowTest(@Path("id") String id);

    @POST("measurement/create")
    Call<List<Measurement>> createMeasurement(@Body List<Measurement> measurement);

    //-------------------------------------------
//      HALLFLOW TESTS
//-------------------------------------------
    @POST("hallflowtest/create")
    Call<List<HallflowTest>> createHallflowTest(@Body List<HallflowTest> hallflowTest);

    @GET("hallflowtest/{id}")
    Call<List<HallflowTest>> fetchHallflowTest(@Path("id") String id);

    @GET("hallflowtests")
    Call<HallFlowTestList> fetchAllHallflowTests();

    @GET("hallflowtest/material/{id}")
    Call<List<HallflowTest>> fetchHallflowTestByMaterial(@Path("id") String id);

    @GET("hallflowtest/operator/{id}")
    Call<List<HallflowTest>> fetchHallflowTestByOperator(@Path("id") String id);

    @GET("hallflowtest/date/{year}/{month}")
    Call<List<HallflowTest>> fetchHallflowTestsByYearMonth(@Path("year") String year, @Path("month") String month);

    @GET("hallflowtest/date/{year}")
    Call<List<HallflowTest>> fetchHallflowTestsByYear(@Path("year") String year);

    @GET("hallflowtest/filter/year/{year}/operator/{operatorId}/material/{materialId}")
    Call<List<HallflowTest>> fetchHallflowTestsByFilter(@Path("year") String year, @Path("operatorId") String operatorId, @Path("materialId") String materialId);




}

