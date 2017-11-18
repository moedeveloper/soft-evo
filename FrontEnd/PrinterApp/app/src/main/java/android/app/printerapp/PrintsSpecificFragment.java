package android.app.printerapp;

import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.DetailList;
import android.app.printerapp.model.Print;
import android.app.printerapp.viewer.DataTextAdapter;
import android.app.printerapp.viewer.STLViewerFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

public class PrintsSpecificFragment extends STLViewerFragment {

    private Context mContext;
    private View mRootView;
    private Bundle arguments;
    private int id;

    private DatabaseHandler databaseHandler;

    public static final String PRINT_ID = "print_id";

//    Views
    private ListView dataListView;
    private RecyclerView detailsList;


    //Empty constructor
    public PrintsSpecificFragment() {
        databaseHandler = DatabaseHandler.getInstance();
    }

    public static PrintsSpecificFragment newInstance(int id){
        Bundle b = new Bundle();
        b.putInt(PRINT_ID, id);
        PrintsSpecificFragment psf = new PrintsSpecificFragment();
        psf.setArguments(b);

        return psf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retain instance to keep the Fragment from destroying itself
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Let parent initialize all STL Viewer elements
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d("Test", "what");
        //Reference to View
        mRootView = null;

        arguments = getArguments();
        //TODO: Here we should get the printid from arguments, which should be given when creating the fragment
        if(arguments != null) {
            id = arguments.getInt(PRINT_ID);
        } else {
            id = 1;
        }
        //If is not new
        if (savedInstanceState == null) {

            //Get the rootview from its parent
            mRootView = getRootView();
            mContext = getActivity();

            detailsList = (RecyclerView) mRootView.findViewById(R.id.prints_trace_recycler_view);
            dataListView = (ListView) mRootView.findViewById(R.id.prints_data_titles_list_view);
            dataListView.setAdapter(new DataTextAdapter(mContext));

        }

        //Load data from database
        new LoadDetailsTask().execute();
        new LoadDataTask().execute(id);

//      Placeholder buttons for testing
        Button addModelButton = (Button) mRootView.findViewById(R.id.print_middle_button);
        addModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hard coding
                String path = "/storage/emulated/0/PrintManager/Files/Feather/_stl/Feather.stl";
                openFileDialog(path);
            }
        });

        Button clearViewerButton = (Button) mRootView.findViewById(R.id.print_right_button);
        clearViewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hard coding
                optionClean();
            }
        });

        Button addTextButton = (Button) mRootView.findViewById(R.id.print_left_button);
        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hard coding
            }
        });

        return mRootView;

    }

    private class LoadDetailsTask extends AsyncTask<Integer, Integer, Integer> {

        private DetailList result = null;

        @Override
        protected Integer doInBackground(Integer... integers) {
            ApiService apiService = databaseHandler.getApiService();

            try {
                result = apiService.fetchAllDetails().execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            View view = LayoutInflater.from(mContext).inflate(R.layout.data_entry_list_item, null);
            detailsList.setAdapter(new DataEntryRecyclerViewAdapter<>(result.getDetails()));
            detailsList.setLayoutManager(new LinearLayoutManager(mContext));
            detailsList.addItemDecoration(new DividerItemDecoration(mContext));
        }
    }

//  Retrieving data from the database using an AsyncTask to make sure the
//  it doesn't lock the app
    private class LoadDataTask extends AsyncTask<Integer, Integer, Integer> {

        private List<Print> result = null;

        @Override
        protected Integer doInBackground(Integer... integers) {
            int id = integers[0];
            ApiService apiService = databaseHandler.getApiService();

            try {
                result =  apiService.fetchPrint(id).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            String[] printTitles = {"Id", "Build id", "Operator", "Machine", "Powder weight start", "Powder weight end",
                    "Build platform material", "Build platform weight", "End time", "Start time"};
            if(result.size() > 0) {
                Print print = result.get(0);
                String[] printValues = {print.getId(), print.getBuildsId(), print.getOperator(), print.getMachine(),
                        print.getPowderWeightStart(), print.getPowderWeightEnd(), print.getBuildPlatformMaterial(),
                        print.getBuildPlatformWeight(), print.getEndTime(), print.getStartTime()};

                dataListView.setAdapter(new DataTextAdapter(printTitles, printValues, mContext));
            } else {
               Log.d("PrintsSpecificFragment", "Could not fetch any data with given ID");
            }
        }
    }
}
