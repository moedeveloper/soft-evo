package android.app.printerapp.dataviews;

import android.app.Fragment;
import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.HallFlowTestList;
import android.app.printerapp.model.HallflowTest;
import android.app.printerapp.model.MachineList;
import android.app.printerapp.model.Material;
import android.app.printerapp.model.MaterialList;
import android.app.printerapp.model.OperatorList;
import android.app.printerapp.model.Print;
import android.app.printerapp.model.PrintList;
import android.app.printerapp.model.TestType;
import android.app.printerapp.search.SearchView;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class TestsFragment extends Fragment implements PropertyChangeListener{

    //Views
    private Context mContext;
    private View mRootView;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private RelativeLayout searchHolder;

    //Variables
    private DatabaseHandler databaseHandler;
    private List<HallflowTest> tests;
    Map<String, DataEntry> selectedOptions;

    //Constants
    public static final String OPERATOR_OPTION = "operator_option";
    public static final String MACHINE_OPTION = "machine_option";
    private static final String TEST_TYPE_OPTION = "test_type_option";
    private static final String MATERIAL_OPTION = "material_option";


    public TestsFragment() {
        databaseHandler = DatabaseHandler.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(savedInstanceState == null) {
            mRootView = inflater.inflate(R.layout.fragment_prints, container, false);
            mContext = getActivity();
            recyclerView = (RecyclerView) mRootView.findViewById(R.id.prints_recycler_view);
            searchHolder = (RelativeLayout) mRootView.findViewById(R.id.search_holder);
            searchView = new SearchView(mContext);
            searchView.addPropertyChangeListener(this);
            searchView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT));
            searchHolder.addView(searchView);
        }


        new LoadDataTask().execute();

        return mRootView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(SearchView.GO_BUTTON_CLICKED)){
            if(event.getNewValue() == null){
                return;
            }

            if(event.getNewValue() instanceof Map){
                selectedOptions = (Map<String, DataEntry>) event.getNewValue();
            }

            String[] options = new String[5];
            Arrays.fill(options, "");

            if(selectedOptions.keySet().contains(SearchView.DATE_OPTION)){
                options[0] = selectedOptions.get(SearchView.DATE_OPTION).getId();
            }

            if(selectedOptions.keySet().contains(OPERATOR_OPTION)){
                options[1] = selectedOptions.get(OPERATOR_OPTION).getId();
            }

//            if(selectedOptions.keySet().contains(MACHINE_OPTION)){
//                options[2] = selectedOptions.get(MACHINE_OPTION).getId();
//            }

//            if(selectedOptions.keySet().contains(TEST_TYPE_OPTION)){
//                options[3] = selectedOptions.get(TEST_TYPE_OPTION).getId();
//            }

            if(selectedOptions.keySet().contains(MATERIAL_OPTION)){
                options[2] = selectedOptions.get(MATERIAL_OPTION).getId();
            }

            new LoadFilteredDataTask().execute(options);

        }
    }

    private int numberOfFiltrations(String[] strings){
        int counter = 0;
        for(String current : strings){
            if (!current.equals(""))
                counter++;
        }
        return counter;
    }

    private List<HallflowTest> filterByText(String text, List<HallflowTest> tests){
        List<HallflowTest> filteredTests = new ArrayList<HallflowTest>();
        for(HallflowTest current : tests){
            if(current.getIdName().contains(text)){
                filteredTests.add(current);
            }
        }
        return filteredTests;
    }

    private class LoadFilteredDataTask extends AsyncTask<String, Void, Void> {

        List<HallflowTest> filteredTests = null;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (numberOfFiltrations(strings) >= 2) {
                    filteredTests = databaseHandler.getApiService().fetchHallflowTestsByFilter(
                            strings[0], strings[1], strings[2]).execute().body();
                } else if (!strings[0].equals("")) {  //If date field is not empty
                    String[] date = strings[0].split("-");
                    if (date.length == 1) {
                        filteredTests = databaseHandler.getApiService().
                                fetchHallflowTestsByYear(strings[0]).execute().body();
                    } else {
                        filteredTests = databaseHandler.getApiService().
                                fetchHallflowTestsByYearMonth(date[0], date[1]).execute().body();
                    }
                } else if (!strings[1].equals("")) { //If operator field is not empty
                    filteredTests = databaseHandler.getApiService().
                            fetchHallflowTestByOperator(strings[1]).execute().body();
                } else if (!strings[2].equals("")) { //If material field is not empty
                    filteredTests = databaseHandler.getApiService().
                            fetchHallflowTestByMaterial(strings[2]).execute().body();
                } else { //Otherwise no options are selected
                    filteredTests = databaseHandler.getApiService().
                            fetchAllHallflowTests().execute().body().getTestsApi();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(filteredTests == null || filteredTests.isEmpty()){
                recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(null));
                recyclerView.getAdapter().notifyDataSetChanged();
                return;
            }



            filteredTests = filterByText(searchView.getSearchText(), filteredTests);
            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(filteredTests));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    //Async task to retrieve data from database, and set the adapter
    //of the recycler view upon data retrieved
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        private OperatorList operators = null;
        private HallFlowTestList result = null;
        private MachineList machines = null;
        private MaterialList materials = null;

        @Override
        protected Void doInBackground(Void... vs) {
            ApiService apiService = databaseHandler.getApiService();

            try {
                result =  apiService.fetchAllHallflowTests().execute().body();
                operators = apiService.fetchAllOperators().execute().body();
                machines = apiService.fetchAllMachines().execute().body();
                materials = apiService.fetchAllMaterials().execute().body();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(result == null){
                return;
            }

            tests = result.getTestsApi();

            //Update the list of all prints
            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(tests));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext));

            //Update the data in search view
            searchView.updateData(tests);

            //Set options
            searchView.createSearchOptionSelection(OPERATOR_OPTION, "Operator", operators.getOperators());
            searchView.createSearchOptionSelection(MACHINE_OPTION, "Machine", machines.getMachinesApi());
            //TODO: MAKE TEST TYPE IN DATABASE
            TestType testType = new TestType();
            testType.setId("1");
            testType.setName("Hallflow test");
            List<TestType> ttList = new ArrayList<>();
            ttList.add(testType);
            searchView.createSearchOptionSelection(TEST_TYPE_OPTION, "Test type", ttList);
            searchView.createSearchOptionSelection(MATERIAL_OPTION, "Material id", materials.getMaterials());
        }
}

}
