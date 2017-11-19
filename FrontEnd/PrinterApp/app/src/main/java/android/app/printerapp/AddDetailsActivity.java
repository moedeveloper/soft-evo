package android.app.printerapp;

import android.app.Activity;
import android.app.printerapp.model.Company;
import android.os.Bundle;
import android.util.*;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class AddDetailsActivity extends Activity implements Callback<List<Company>> {

    private List<Company> companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        companies = new LinkedList<>();

        ContextFactory.createApiService().fetchAllCompanies().enqueue(this);
    }


    @Override
    public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
        if (response.isSuccessful()) {
            companies.addAll(response.body());

            Spinner spinner = (Spinner) findViewById(R.id.companyListInput);
            SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, companies);
            spinner.setAdapter(adapter);

        }

        android.util.Log.e(TAG, "onResponse: " + response.toString());
    }

    @Override
    public void onFailure(Call<List<Company>> call, Throwable t) {
        companies.add(new Company("No Company Found", 0));
    }
}
