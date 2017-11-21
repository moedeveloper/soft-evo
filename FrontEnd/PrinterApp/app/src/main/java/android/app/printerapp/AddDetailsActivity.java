package android.app.printerapp;

import android.app.Activity;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.Company;
import android.app.printerapp.model.CompanyList;
import android.content.Intent;
import android.os.Bundle;
import android.util.*;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class AddDetailsActivity extends Activity implements Callback<CompanyList> {

    private List<Company> companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        fillCompanyList();
        setup();
    }

    private void setup() {
        View button = findViewById(R.id.addNewCompanyButton);
        View.OnClickListener listener = getListner(this);
        button.setOnClickListener(listener);
    }

    private View.OnClickListener getListner(Activity activity) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddNewCompany.class);
                startActivity(intent);
            }
        };
    }

    private void fillCompanyList() {
        companies = new LinkedList<>();

        DatabaseHandler.getInstance().getApiService().fetchAllCompanies().enqueue(this);
    }


    @Override
    public void onResponse(Call<CompanyList> call, Response<CompanyList> response) {
        if (response.isSuccessful()) {
            companies.addAll(response.body().getCompaniesApi());

            Spinner spinner = (Spinner) findViewById(R.id.companyListInput);
            SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, companies);
            spinner.setAdapter(adapter);

        }

        android.util.Log.e(TAG, "onResponse: " + response.toString());
    }

    @Override
    public void onFailure(Call<CompanyList> call, Throwable t) {
        companies.add(new Company("No Company Found", 0));
    }
}
