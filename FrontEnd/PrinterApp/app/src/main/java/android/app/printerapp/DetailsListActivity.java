package android.app.printerapp;

import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.DetailList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jcmma on 2017-11-07.
 */

public class DetailsListActivity extends ActionBarActivity implements Callback<DetailList> {
    private List<Detail> dataset;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    DetailsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_list_activity);

        dataset = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.details_list_activity_progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.details_list_activity_recycler_view);
        adapter = new DetailsListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        loadData();
    }

    private void loadData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
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
            dataset.addAll(detailList.getDetails());
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<DetailList> call, Throwable t) {
        t.printStackTrace();
    }

    private class DetailsListAdapter extends RecyclerView.Adapter<DetailItemHolder> {

        @Override
        public DetailItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(DetailsListActivity.this).inflate(R.layout.detail_list_item, parent, false);
            return new DetailItemHolder(view);
        }

        @Override
        public void onBindViewHolder(DetailItemHolder holder, int position) {
            Detail detail = dataset.get(position);
            holder.name.setText(detail.getName());
            holder.id.setText(detail.getId() + "");
            holder.creationDate.setText("TBD");
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }

    private class DetailItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView id;
        TextView creationDate;

        public DetailItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.detail_list_item_name);
            id = (TextView) itemView.findViewById(R.id.detail_list_item_id);
            creationDate = (TextView) itemView.findViewById(R.id.detail_list_item_creation_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //TODO: Do something
        }
    }
}
