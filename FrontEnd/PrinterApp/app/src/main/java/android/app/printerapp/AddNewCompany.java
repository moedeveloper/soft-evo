package android.app.printerapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class AddNewCompany extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_company);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
