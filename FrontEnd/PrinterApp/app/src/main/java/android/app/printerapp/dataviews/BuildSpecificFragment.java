package android.app.printerapp.dataviews;

import android.app.printerapp.ListContent;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;



public class BuildSpecificFragment extends SpecificFragment {
//---------------------------------------------------------------------------------------
//          VARIABLES
//---------------------------------------------------------------------------------------
    //Variables for specific print
    private int id;
    private Build build;

    //Api
    File[] files;

    //Constants
    public static final String BUILD_ID = "build_id";

//---------------------------------------------------------------------------------------
//          OVERRIDES
//---------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        //If there's no saved instance state, initialize variables
        if (savedInstanceState == null) {
            //Retrieve references to views

            //Retrieve id from arguments
            if(arguments != null) {
                id = arguments.getInt(BUILD_ID);
            } else {
                id = 1;
            }
        }

        //Load all data we need from database
        //and then display the data into the views we have
        new LoadDataTask().execute();

        return mRootView;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.prints_layout_main;
    }

    @Override
    public void loadData() {
        new LoadDataTask().execute();
    }

    @Override
    public void createTabs() {
        createTab(ListContent.ID_DETAILS, "Detail");
        createTab(ListContent.ID_MATERIALS, "Material");
        createTab(ListContent.ID_TESTS, "Tests");
    }

//---------------------------------------------------------------------------------------
//          FACTORY METHODS
//---------------------------------------------------------------------------------------

    //Factory method used to create this fragment
    public static BuildSpecificFragment newInstance(int id){
        Bundle b = new Bundle();
        b.putInt(BUILD_ID, id);
        BuildSpecificFragment bsf = new BuildSpecificFragment();
        bsf.setArguments(b);
        return bsf;
    }

//---------------------------------------------------------------------------------------
//          PRIVATE CLASSES
//---------------------------------------------------------------------------------------

    //Async task used to load all data to be displayed
    private class LoadDataTask extends AsyncTask<Integer, Integer, Integer> {


        @Override
        protected Integer doInBackground(Integer... integers) {
            ApiService apiService = databaseHandler.getApiService();
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

    }


}
