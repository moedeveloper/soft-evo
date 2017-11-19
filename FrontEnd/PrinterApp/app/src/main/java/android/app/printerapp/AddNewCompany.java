package android.app.printerapp;

import android.app.Activity;
import android.app.printerapp.api.CompanyDTO;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.Company;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Response;

public class AddNewCompany extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_company);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View button = findViewById(R.id.add_new_company_button);
        View.OnClickListener listener = getListner(this);
        button.setOnClickListener(listener);
    }

    private View.OnClickListener getListner(final Activity addNewCompany) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                saveNewCompany();
                    goBackToPreviousView();
            }

            private boolean saveNewCompany() {
                try {
                    Company company = extractCompanyFromUserInput();
                    System.out.println(company.toString());
                    Call<String> call = DatabaseHandler.getInstance().getApiService().createNewCompany(new CompanyDTO(company.getName()));
                    return 200 == call.execute().code();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            private RequestBody convert(final Company company) {
                return new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("application/json");
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        sink.writeUtf8(company.requestBody());
                    }
                };
            }

            private void goBackToPreviousView() {
                addNewCompany.finish();
            }
        };
    }

    @NonNull
    private Company extractCompanyFromUserInput() {
        String companyName = readCompanyNameFromUserInput();
        return new Company(companyName);
    }

    private String readCompanyNameFromUserInput() {
        View companyText = findViewById(R.id.newCompany);
        if (isEditText(companyText)) {
            EditText companyNameInputText = (EditText) companyText;
            return companyNameInputText.getText().toString();
        } else {
            return null;
        }
    }

    private boolean isEditText(View companyText) {
        return companyText != null && companyText instanceof EditText;
    }

}
