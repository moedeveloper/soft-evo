package android.app.printerapp.ui;

import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Detail;
import android.content.Context;
import android.widget.ListView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SAMSUNG on 2017-11-16.
 */

public class DetailDataListView extends ListView implements Callback<List<Detail>> {

    List<Detail> detail;

    private int id;

    public DetailDataListView(Context context, int id) {
        super(context);
        this.id = id;
        loadData();
    }

    private void loadData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Detail>> call = apiService.fetchDetail(id);
        call.enqueue(this);
    }



    @Override
    public void onResponse(Call<List<Detail>> call, Response<List<Detail>> response) {
        if (response.isSuccessful()) {
            detail = response.body();
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Detail>> call, Throwable t) {
        t.printStackTrace();
    }

}
