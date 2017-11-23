package android.app.printerapp.ui;

import android.app.printerapp.AddDetailsActivity;
import android.app.printerapp.AddNewCompany;
import android.app.printerapp.MainActivity;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.api.DetailDto;
import android.app.printerapp.model.Company;
import android.app.printerapp.model.Detail;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Shireenyu on 2017-11-22.
 */

public class DetailSaveListener implements OnClickListener {

    private AddDetailsActivity addDetailsActivity;
    private ApiService apiService;

    public DetailSaveListener(AddDetailsActivity addDetailsActivity) {
        this.addDetailsActivity = addDetailsActivity;
        apiService = DatabaseHandler.getInstance().getApiService();
    }

    public void onClick(View view) {
        try {
            DetailDto detailDto = getDetailDto();
            Response<String> response = saveDetail(detailDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        goToMainActivity(view);
    }

    private Response<String> saveDetail(DetailDto detailDto) throws java.io.IOException {
        Call<String> restCall = apiService.createDetails(detailDto);
        return restCall.execute();
    }

    private void goToMainActivity(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        addDetailsActivity.startActivity(intent);
    }

    @NonNull
    private DetailDto getDetailDto() {
        Detail details = new Detail();
        // {
        //  "id":1,
        //  "name":"namehere",
        //  "companyId":1,
        //  "fileId":0,
        //  "projectId":1,
        //  "creationDate":"2017-10-19 16:50:00",
        //  "comment":"comment here"
        // }
        setCompanyId(details);
        setProjectId(details);
        setDate(details);
        setComment(details);
        setFileId(details);

        return new DetailDto(details);
    }

    private void setFileId(Detail details) {
        details.setFileId("0");
    }

    private void setComment(Detail details) {
        String text = getStringFromEditText(R.id.commentInput);
        details.setComment(text);
    }

    private void setDate(Detail details) {
        String dateAsString = getStringFromEditText(R.id.dateInput);
        details.setCreationDate(dateAsString);
    }

    private void setProjectId(Detail details) {
        String projectStringId = getStringFromEditText(R.id.projectIdInput);
        tryToGetProjectIdAsInteger(details, projectStringId);
    }

    @NonNull
    private String getStringFromEditText(int id) {
        EditText editText = (EditText) addDetailsActivity.findViewById(id);
        return editText.getText().toString();
    }

    private void tryToGetProjectIdAsInteger(Detail details, String editText) {
        try {
            details.setProjectId(Integer.parseInt(editText));
        } catch (NumberFormatException ex) {
            throw new InvalidUserInput(editText);
        }
    }

    private void setCompanyId(Detail details) {
        Company company = getCompany();
        details.setCompanyId(company.getIntId());
    }

    @Nullable
    private Company getCompany() {
        Spinner spinner = (Spinner) addDetailsActivity.findViewById(R.id.companyListInput);
        Company company = null;
        Object o = spinner.getSelectedItem();
        if (o != null && o instanceof Company)
            company = (Company) o;
        return company;
    }

}

class InvalidUserInput extends RuntimeException {
    public InvalidUserInput(String message) {
        super(message);
    }
}
