package android.app.printerapp.dataviews;

import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.ListContent;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Build;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.HallflowTest;
import android.app.printerapp.model.Machine;
import android.app.printerapp.model.Material;
import android.app.printerapp.model.Measurement;
import android.app.printerapp.model.MeasurementList;
import android.app.printerapp.model.Operator;
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
import android.widget.LinearLayout;
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
        getActivity().setTitle("MaplePrint - Test");
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

        LinearLayout imageHolder = (LinearLayout) mRootView.findViewById(R.id.stl_viewer_holder_layout);
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.material_info);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);

        imageHolder.addView(imageView);


        //Load all data we need from database
        //and then display the data into the views we have
        new LoadDataTask().execute();

        return mRootView;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.test_layout_main;
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
        List<Operator> operatorResult;
        List<Machine> machineResult;

        @Override
        protected Integer doInBackground(Integer... integers) {
            ApiService apiService = databaseHandler.getApiService();
            try {
                List<HallflowTest> tResult = apiService.fetchHallflowTest(
                        String.valueOf(id)).execute().body();
                if(dataIsOk(tResult)){
                    test = tResult.get(0);

                    machineResult = apiService.fetchMachine(
                            Integer.parseInt(test.getMachineId())).execute().body();

                    operatorResult = apiService.fetchOperator(
                            Integer.parseInt(test.getOperatorId())).execute().body();

                    linkedMaterials = apiService.fetchMaterial(
                            test.getMaterialId()).execute().body();
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

            String operatorName = test.getOperatorId();
            String machineName = test.getMachineId();

            if(dataIsOk(operatorResult)){
                operatorName = operatorResult.get(0).getName();
            }

            if(dataIsOk(machineResult)){
                machineName = machineResult.get(0).getName();
            }

            String[] testTitles = new String[9 + measurementList.size()];
            String[] testValues = new String[testTitles.length];

            testTitles[0] = "Id";
            testTitles[1] = "Creation date";
            testTitles[2] = "Operator";
            testTitles[3] = "Machine";
            testTitles[4] = "Relative humidity";
            testTitles[5] = "Temperature";
            testTitles[6] = "Tap";


            testValues[0] = test.getId();
            testValues[1] = test.getCreationDate();
            testValues[2] = operatorName;
            testValues[3] = machineName;
            testValues[4] = test.getRelativeHumidity() + " %";
            testValues[5] = test.getTemperature() + " C";
            testValues[6] = test.getTap();


            double Average = 0;

            if(dataIsOk(measurementList)){
                for(int i = 0; i < measurementList.size(); i++){
                    int j = 7 + i;
                    testTitles[j] = "Measurement value [" + i + "]";
                    testValues[j] = measurementList.get(i).getMeasurementValue() + " " +
                            measurementList.get(i).getMeasurementUnit();
                    Average += Double.parseDouble(measurementList.get(i).getMeasurementValue());
                }

                Average /= measurementList.size();
                testTitles[testTitles.length - 2] = "Measurement average";
                testValues[testValues.length - 2] = Average + " " +measurementList.get(0).getMeasurementUnit();
            }

            testTitles[testTitles.length - 1] = "Comments";
            testValues[testValues.length - 1] = test.getComments();

            dataListView.setAdapter(new DataTextAdapter(testTitles, testValues, mContext));

            allTraceLists.get(ListContent.ID_MATERIALS).setAdapter(new DataEntryRecyclerViewAdapter<>(linkedMaterials));
            allTraceLists.get(ListContent.ID_MATERIALS).setLayoutManager(new LinearLayoutManager(mContext));
            allTraceLists.get(ListContent.ID_MATERIALS).addItemDecoration(new DividerItemDecoration(mContext));

            super.onPostExecute(integer);
        }
    }
}
