package android.app.printerapp.dataviews;

import android.app.Fragment;
import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.R;
import android.app.printerapp.model.MachineList;
import android.app.printerapp.model.NoDataSelected;
import android.app.printerapp.search.SearchView;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.OperatorList;
import android.app.printerapp.model.Print;
import android.app.printerapp.model.PrintList;
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
import java.util.prefs.NodeChangeEvent;


public class PrintsFragment extends Fragment implements PropertyChangeListener{

    //Views
    private Context mContext;
    private View mRootView;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private RelativeLayout searchHolder;

    //Variables
    private DatabaseHandler databaseHandler;
    private List<Print> prints;
    Map<String, DataEntry> selectedOptions;

    //Constants
    public static final String OPERATOR_OPTION = "operator_option";
    public static final String COMPANY_OPTION = "company_option";
    public static final String MACHINE_OPTION = "machine_option";


    public PrintsFragment() {
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

            String[] options = new String[3];
            Arrays.fill(options, "");

            if(selectedOptions.keySet().contains(SearchView.DATE_OPTION)){
                options[0] = selectedOptions.get(SearchView.DATE_OPTION).getId();
            }

            if(selectedOptions.keySet().contains(OPERATOR_OPTION)){
                options[1] = selectedOptions.get(OPERATOR_OPTION).getId();
            }
            if(selectedOptions.keySet().contains(MACHINE_OPTION)){
                options[2] = selectedOptions.get(MACHINE_OPTION).getId();
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

    private List<Print> filterByText(String text, List<Print> prints){
        List<Print> filteredPrints = new ArrayList<Print>();
        for(Print current : prints){
            if(current.getIdName().contains(text)){
                filteredPrints.add(current);
            }
        }
        return filteredPrints;
    }

    private class LoadFilteredDataTask extends AsyncTask<String, Void, Void> {

        List<Print> filteredPrints = null;

        @Override
        protected Void doInBackground(String... strings) {
            try {

                if (numberOfFiltrations(strings) >= 2) {
                    filteredPrints = databaseHandler.getApiService().fetchPrintByFilter(
                            strings[0], strings[1], strings[2]).execute().body();
                } else if (!strings[0].equals("")) {  //If date field is not empty
                    String[] date = strings[0].split("-");
                    if (date.length == 1) {
                        filteredPrints = databaseHandler.getApiService().
                                fetchPrintByYear(strings[0]).execute().body();
                    } else {
                        filteredPrints = databaseHandler.getApiService().
                                fetchPrintByYearMonth(date[0], date[1]).execute().body();
                    }
                } else if (!strings[1].equals("")) { //If operator field is not empty
                    filteredPrints = databaseHandler.getApiService().
                            fetchPrintFromOperator(strings[1]).execute().body();
                } else if (!strings[2].equals("")) { //If machine field is not empty
                    filteredPrints = databaseHandler.getApiService().
                            fetchPrintByMachine(strings[2]).execute().body();
                } else { //Otherwise no options are selected
                    filteredPrints = databaseHandler.getApiService().
                            fetchAllPrints().execute().body().getPrints();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(filteredPrints == null || filteredPrints.isEmpty()){
                recyclerView.setAdapter(new DataEntryRecyclerViewAdapter(null));
                recyclerView.getAdapter().notifyDataSetChanged();
                return;
            }

            filteredPrints = filterByText(searchView.getSearchText(), filteredPrints);
            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(filteredPrints));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    //Async task to retrieve data from database, and set the adapter
    //of the recycler view upon data retrieved
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        private OperatorList operators = null;
        private PrintList result = null;
        private MachineList machines = null;

        @Override
        protected Void doInBackground(Void... vs) {
            ApiService apiService = databaseHandler.getApiService();

            try {
                result =  apiService.fetchAllPrints().execute().body();
                operators = apiService.fetchAllOperators().execute().body();
                machines = apiService.fetchAllMachines().execute().body();

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

            prints = result.getPrints();

            //Update the list of all prints
            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(prints));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext));

            //Update the data in search view
            searchView.updateData(prints);

            //Set options
            searchView.createSearchOptionSelection(OPERATOR_OPTION, "Operator", operators.getOperators());
            searchView.createSearchOptionSelection(MACHINE_OPTION, "Machine", machines.getMachinesApi());
    }
}

}
