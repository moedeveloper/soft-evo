package android.app.printerapp;

import android.app.Fragment;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.Print;
import android.app.printerapp.model.PrintList;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;


public class PrintsFragment extends Fragment implements PropertyChangeListener{

    private RecyclerView recyclerView;
    private DatabaseHandler databaseHandler;
    private Context mContext;
    private View mRootView;

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
        mRootView = inflater.inflate(R.layout.fragment_prints, container, false);
        mContext = getActivity();

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.prints_recycler_view);

        new LoadDataTask().execute();

        return mRootView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {

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
            recyclerView.setAdapter(new DataEntryRecyclerViewAdapter<>(result.getPrints()));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext));
    }
}

}
