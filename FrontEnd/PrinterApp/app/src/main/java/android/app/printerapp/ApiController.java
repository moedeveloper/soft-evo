package android.app.printerapp;

import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.DetailList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Johan on 11/1/2017.
 */

public class ApiController implements Callback<DetailList> {

    private static final String BASE_URL = "http://mo3app.azurewebsites.net/api/";

    public void testDetailsApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<DetailList> call = apiService.listDetails();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<DetailList> call, Response<DetailList> response) {
        if (response.isSuccessful()) {
            DetailList detailList = response.body();
            for (Detail detail : detailList.getDetails()) {
                System.out.println(detail.getName());
            }
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<DetailList> call, Throwable t) {
        t.printStackTrace();
    }
}
