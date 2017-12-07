package android.app.printerapp.dataviews;

/**
 * Created by SAMSUNG on 2017-11-22.
 */

import android.app.Fragment;
import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.Build;
import android.app.printerapp.model.BuildList;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.Material;
import android.app.printerapp.model.MaterialList;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialsFragment extends Fragment implements PropertyChangeListener{

    private RecyclerView recyclerView;
    private DatabaseHandler databaseHandler;
    private Context mContext;
    private View mRootView;
    private RelativeLayout searchHolder;
    private SearchView searchView;

    private DataEntry dateInput;

    public MaterialsFragment() {
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

            Map<String, DataEntry> selectedOptions = new HashMap<>();
            if(event.getNewValue() instanceof Map){
                selectedOptions = (Map<String, DataEntry>)event.getNewValue();
            }

            if(selectedOptions.keySet().contains(SearchView.DATE_OPTION)){
                dateInput = selectedOptions.get(SearchView.DATE_OPTION);
            }

            new LoadFilteredDataTask().execute();

        }
    }

    private List<Material> filterByText(String text, List<Material> materials){
        List<Material> filteredMaterials = new ArrayList<>();
        for(Material current : materials){
            if(current.getIdName().contains(text)){
                filteredMaterials.add(current);
            }
        }
        return filteredMaterials;
    }

    private class LoadFilteredDataTask extends AsyncTask<Void, Void, Void>{
        private List<Material> result;

        @Override
        protected Void doInBackground(Void... voids) {
            ApiService apiService = databaseHandler.getApiService();
            try {
                if(!dateInput.getId().equals("")){
                    String[] date = dateInput.getId().split("-");
                    if (date.length >= 2) {
                        result = apiService.fetchMaterialsByYearMonth(date[0], date[1]).execute().body();
                    } else {
                        result = apiService.fetchMaterialsByYear(date[0]).execute().body();
                    }
                } else {
                    result = apiService.fetchAllMaterials().execute().body().getMaterials();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(result == null || result.isEmpty()){
                recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(null));
                recyclerView.getAdapter().notifyDataSetChanged();
                return;
            }

            result = filterByText(searchView.getSearchText(), result);
            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(result));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    //Async task to retrieve data from database, and set the adapter
    //of the recycler view upon data retrieved
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        private MaterialList result = null;

        @Override
        protected Void doInBackground(Void... vs) {
            ApiService apiService = databaseHandler.getApiService();

            try {
                result =  apiService.fetchAllMaterials().execute().body();
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

            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(result.getMaterials()));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext));

            searchView.updateData(result.getMaterials());
        }
    }
}
