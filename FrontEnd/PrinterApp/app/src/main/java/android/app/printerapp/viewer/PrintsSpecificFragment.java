package android.app.printerapp.viewer;

import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DetailDataListView;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PrintsSpecificFragment extends STLViewerFragment {

    private Context mContext;
    private View mRootView;
    private int id;
    private final int PRINT = 0;
    private final int DETAIL = 1;
    private final int BUILD = 2;
    private final int TEST = 3;
    private final int MATERIAL = 4;

//    Views
    private ListView dataListView;


    //Empty constructor
    public PrintsSpecificFragment() {
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

        //Reference to View
        mRootView = null;

        Bundle arguments = getArguments();
        //TODO: Here we should get the printid from arguments, which should be given when creating the fragment
        id = 1;
        //If is not new
        if (savedInstanceState == null) {

            //Get the rootview from its parent
            mRootView = getRootView();
            mContext = getActivity();

            dataListView = (ListView) mRootView.findViewById(R.id.prints_data_titles_list_view);
            dataListView.setAdapter(new DataTextAdapter(mContext));

        }

        new LoadDataTask().execute(1);
        DetailDataListView test = new DetailDataListView(mContext, id);
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

    private class LoadDataTask extends AsyncTask<Integer, Integer, Integer> {

        private List<Print> result = null;

        @Override
        protected Integer doInBackground(Integer... integers) {
            int id = integers[0];

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<List<Print>> callPrint = apiService.fetchPrint(id);
            try {
                result = callPrint.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            String[] printTitles = {"id", "buildsId", "operator", "machine", "powderWeightStart", "powderWeightEnd",
                    "buildPlatformMaterial", "buildPlatformWeight", "endTime", "startTime"};
            Print print = result.get(0);
            String[] printValues = {print.getId(), print.getBuildsId(), print.getOperator(), print.getMachine(),
                    print.getPowderWeightStart(), print.getPowderWeightEnd(), print.getBuildPlatformMaterial(),
                    print.getBuildPlatformWeight(), print.getEndTime(), print.getStartTime()};

            dataListView.setAdapter(new DataTextAdapter(printTitles, printValues, mContext));
        }
    }
}
