package android.app.printerapp;

import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.DetailList;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Johan on 11/10/2017.
 */

public class DetailActivity extends ActionBarActivity implements Callback<List<Detail>> {
        ProgressBar progressBar;
        TextView name;
        TextView companyName;
        View leftLayout;
        View rightLayout;

    private int detailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        detailId = getIntent().getIntExtra("detailId", -1);

        progressBar = (ProgressBar) findViewById(R.id.detail_activity_progress_bar);
        name = (TextView) findViewById(R.id.detail_activity_name_label);
        companyName = (TextView) findViewById(R.id.detail_activity_company);
        leftLayout = findViewById(R.id.detail_activity_left_layout);
        rightLayout = findViewById(R.id.detail_activity_right_layout);

        loadData();
    }

    private void loadData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Detail>> call = apiService.fetchDetail(detailId);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Detail>> call, Response<List<Detail>> response) {
        if (response.isSuccessful()) {
            List<Detail> details = response.body();
            Detail detail = details.get(0);
            name.setText("Name: " + detail.getName());
            companyName.setText("Id: " + detail.getId());
            progressBar.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);
            rightLayout.setVisibility(View.VISIBLE);
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Detail>> call, Throwable t) {
        t.printStackTrace();
    }
}
