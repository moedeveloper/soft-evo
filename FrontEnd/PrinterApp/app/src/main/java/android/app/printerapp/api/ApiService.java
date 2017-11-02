package android.app.printerapp.api;

import android.app.printerapp.model.Detail;
import android.app.printerapp.model.DetailList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Johan on 11/1/2017.
 */

public interface ApiService {
    @GET("details")
    Call<DetailList> listDetails();
}
