package android.app.printerapp.dataviews;

import android.app.Fragment;
import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.R;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Print;
import android.app.printerapp.model.ProjectList;
import android.app.printerapp.search.SearchView;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.Company;
import android.app.printerapp.model.CompanyList;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.DetailList;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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

/**
 * Created by jcmma on 2017-11-23.
 */

public class DetailsFragment extends Fragment implements PropertyChangeListener {
    private RecyclerView recyclerView;
    private DatabaseHandler databaseHandler;
    private Context mContext;
    private View mRootView;
    private RelativeLayout searchHolder;
    private SearchView searchView;

    Map<String, DataEntry> selectedOptions;

    public final static String COMPANY_OPTION = "company_option";
    public final static String PROJECT_OPTION = "project_option";

    public DetailsFragment() {
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
            searchView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT));
            searchView.addPropertyChangeListener(this);
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

            if(selectedOptions.keySet().contains(COMPANY_OPTION)){
                options[1] = selectedOptions.get(COMPANY_OPTION).getId();
            }
            if(selectedOptions.keySet().contains(PROJECT_OPTION)){
                options[2] = selectedOptions.get(PROJECT_OPTION).getId();
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

    private List<Detail> filterByText(String text, List<Detail> details){
        List<Detail> filteredDetails = new ArrayList<>();
        for(Detail current : details){
            if(current.getIdName().contains(text)){
                filteredDetails.add(current);
            }
        }
        return filteredDetails;
    }

    private class LoadFilteredDataTask extends AsyncTask<String, Void, Void> {
        List<Detail> filteredDetails = new ArrayList<>();
        ApiService apiService = DatabaseHandler.getInstance().getApiService();

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if(numberOfFiltrations(strings) >= 2){
                    filteredDetails = apiService.fetchDetailByFilter(
                            strings[0], strings[1], strings[2]).execute().body();
                } else if (!strings[0].equals("")) {
                    String[] date = strings[0].split("-");
                    if (date.length == 1) {
                        filteredDetails = apiService.
                                fetchDetailByYear(date[0]).execute().body();
                    } else {
                        filteredDetails = apiService.
                                fetchDetailByYearMonth(date[0], date[1]).execute().body();
                    }
                } else if (!strings[1].equals("")){
                    filteredDetails = apiService.
                            fetchDetailByCompany(Integer.parseInt(strings[1])).execute().body();
                } else if (!strings[2].equals("")) {
                    filteredDetails = apiService.fetchDetailByProject(strings[2]).execute().body();
                } else {
                    filteredDetails = apiService.fetchAllDetails().execute().body().getDetails();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(filteredDetails == null || filteredDetails.isEmpty()){
                recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(null));
                recyclerView.getAdapter().notifyDataSetChanged();
                return;
            }

            filteredDetails = filterByText(searchView.getSearchText(), filteredDetails);
            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(filteredDetails));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    //Async task to retrieve data from database, and set the adapter
    //of the recycler view upon data retrieved
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        private DetailList result = null;
        private CompanyList companyList = null;
        private ProjectList projectList = null;

        @Override
        protected Void doInBackground(Void... vs) {
            ApiService apiService = databaseHandler.getApiService();
            try {
                result =  apiService.fetchAllDetails().execute().body();
                companyList = apiService.fetchAllCompanies().execute().body();
                projectList = apiService.fetchAllProjects().execute().body();
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

            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(result.getDetails()));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext));
            searchView.updateData(result.getDetails());

            if(!(companyList == null) || !companyList.getCompaniesApi().isEmpty()){
                searchView.createSearchOptionSelection(COMPANY_OPTION ,"Company", companyList.getCompaniesApi());
            }
            if(!(projectList == null) || !projectList.getProjects().isEmpty()) {
                searchView.createSearchOptionSelection(PROJECT_OPTION, "Project", projectList.getProjects());
            }
        }
    }


}
