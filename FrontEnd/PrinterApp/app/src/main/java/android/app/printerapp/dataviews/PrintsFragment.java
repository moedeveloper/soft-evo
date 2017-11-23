package android.app.printerapp.dataviews;

import android.app.Fragment;
import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.R;
import android.app.printerapp.SearchView;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.DataEntry;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PrintsFragment extends Fragment{

    private RecyclerView recyclerView;
    private DatabaseHandler databaseHandler;
    private Context mContext;
    private View mRootView;
    private RelativeLayout searchHolder;
    private SearchView searchView;
    private List<Print> prints;
    private List<String> printIds = new ArrayList<>();

    public static final String PRINT_CLICKED = "print_clicked";

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
            searchView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT));
            searchHolder.addView(searchView);
        }


        new LoadDataTask().execute();

        for(int i = 0; i < 10; i++){
            searchView.createSearchOption("Test", new String[]{"Starfish","Elephant"});
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_dropdown_item_1line, printIds);
        searchView.setAdapter(adapter);

        searchView.setOnClickListenerForGoButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoCompleteTextView searchEntry = (AutoCompleteTextView) searchView.findViewById(R.id.search_bar);
                DataEntryRecyclerViewAdapter adapter = new DataEntryRecyclerViewAdapter<>(
                        filterList(prints, searchEntry.getText().toString()));
                recyclerView.swapAdapter(adapter, false);
            }
        });

        return mRootView;
    }

    //Filters a list of DataEntry, by ID
    private List<DataEntry> filterList(List<? extends DataEntry> list, String filter){
        List<DataEntry> returnList = new ArrayList<>();
        for(DataEntry current : list){
            if(("P" + current.getId()).contains(filter)){
                returnList.add(current);
            }
        }
        return returnList;
    }

    //Async task to retrieve data from database, and set the adapter
    //of the recycler view upon data retrieved
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        private PrintList result = null;

        @Override
        protected Void doInBackground(Void... vs) {
            ApiService apiService = databaseHandler.getApiService();

            try {
                result =  apiService.fetchAllPrints().execute().body();
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

            //Save all print ids in a list. Add P as a prefix to define that it's a print
            for(Print current : prints){
                printIds.add("P" + current.getId());
            }

            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(prints));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext));
    }
}

}
