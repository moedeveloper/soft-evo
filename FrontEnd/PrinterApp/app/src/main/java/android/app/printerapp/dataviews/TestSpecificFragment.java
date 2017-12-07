package android.app.printerapp.dataviews;

import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.ListContent;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Build;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.HallflowTest;
import android.app.printerapp.model.Material;
import android.app.printerapp.model.Measurement;
import android.app.printerapp.model.MeasurementList;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.app.printerapp.viewer.DataTextAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestSpecificFragment extends SpecificFragment {
//---------------------------------------------------------------------------------------
//          VARIABLES
//---------------------------------------------------------------------------------------
    //Variables for specific print
    private int id;
    private HallflowTest test;

    //Constants
    public static final String TEST_ID = "test_id";
    private List<Material> linkedMaterials;

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
            ListView testListView = (ListView) mRootView.findViewById(R.id.data_list_view);

            //Retrieve id from arguments
            if(arguments != null) {
                id = arguments.getInt(TEST_ID);
            } else {
                id = 1;
            }
        }
        TextView title = (TextView) mRootView.findViewById(R.id.print_title);
        title.setText("Hallflow Test HFT" + id);

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
        createTab(ListContent.ID_MATERIALS, "Material");
    }

//---------------------------------------------------------------------------------------
//          FACTORY METHODS
//---------------------------------------------------------------------------------------

    //Factory method used to create this fragment
    public static TestSpecificFragment newInstance(int id){
        Bundle b = new Bundle();
        b.putInt(TEST_ID, id);
        TestSpecificFragment bsf = new TestSpecificFragment();
        bsf.setArguments(b);
        return bsf;
    }

    @Override
    void onTagSelected(Object tag) {

        if(tag == null){
            return;
        }
        if(tag.equals(ListContent.ID_MATERIALS)){
            allTraceLists.get(ListContent.ID_MATERIALS).setAdapter(new DataEntryRecyclerViewAdapter<>(linkedMaterials));
            allTraceLists.get(ListContent.ID_MATERIALS).setLayoutManager(new LinearLayoutManager(mContext));
            allTraceLists.get(ListContent.ID_MATERIALS).addItemDecoration(new DividerItemDecoration(mContext));
        }
    }

//---------------------------------------------------------------------------------------
//          PRIVATE CLASSES
//---------------------------------------------------------------------------------------

    //Async task used to load all data to be displayed
    private class LoadDataTask extends AsyncTask<Integer, Integer, Integer> {

        private List<Measurement> measurementList;

        @Override
        protected Integer doInBackground(Integer... integers) {
            ApiService apiService = databaseHandler.getApiService();
            try {
                List<HallflowTest> tResult = apiService.fetchHallflowTest(String.valueOf(id)).execute().body();
                if(dataIsOk(tResult)){
                    test = tResult.get(0);
                    linkedMaterials = apiService.fetchMaterial(test.getMaterialId()).execute().body();
                }
                measurementList = apiService.fetchMeasurementsByHallflowTest(String.valueOf(id)).execute().body();


            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            if(test == null){
                createAlertDialog("Cannot retrieve build");
                return;
            }

            if(measurementList == null){
                measurementList = new ArrayList<>();
            }

            if(linkedMaterials == null){
                linkedMaterials = new ArrayList<>();
            }

            String[] testTitles = new String[6 + measurementList.size()];
            String[] testValues = new String[testTitles.length];

            testTitles[0] = "Id";
            testTitles[1] = "Creation date";
            testTitles[2] = "Operator";
            testTitles[3] = "Relative humidity";
            testTitles[4] = "Temperature";
            testTitles[5] = "Tap";
            testTitles[6] = "Comments";

            testTitles[0] = test.getId();
            testTitles[1] = test.getCreationDate();
            testTitles[2] = test.getOperatorId();
            testTitles[3] = test.getRelativeHumidity();
            testTitles[4] = test.getTemperature();
            testTitles[5] = test.getTap();
            testTitles[6] = test.getComments();

            for(int i = 0; i < measurementList.size(); i++){
                int j = 7 + i;
                testTitles[j] = "Measurement value [" + i + "]";
                testValues[j] = measurementList.get(i).getMeasurementValue() + " " +
                                measurementList.get(i).getMeasurementUnit();
            }

            dataListView.setAdapter(new DataTextAdapter(testTitles, testValues, mContext));

            allTraceLists.get(ListContent.ID_MATERIALS).setAdapter(new DataEntryRecyclerViewAdapter<>(linkedMaterials));
            allTraceLists.get(ListContent.ID_MATERIALS).setLayoutManager(new LinearLayoutManager(mContext));
            allTraceLists.get(ListContent.ID_MATERIALS).addItemDecoration(new DividerItemDecoration(mContext));

            super.onPostExecute(integer);
        }
    }
}
