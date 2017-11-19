package android.app.printerapp;

import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrintsSpecificFragment extends STLViewerFragment {

    private Context mContext;
    private View mRootView;
    private Bundle arguments;

    private int id;
    private Print print;
    private DatabaseHandler databaseHandler;

    //CONSTANTS
    public static final String PRINT_ID = "print_id";
    private final int MAX_BUTTONS_PER_LAYOUT = 5;

    //Views
    private ListView dataListView;
    private RecyclerView detailsList;
    private LinearLayout upperButtonLayout;
    private LinearLayout lowerButtonLayout;
    private TabHost traceTabHost;
    List<ToggleButton> toggleDetailButtons = new ArrayList<>();

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
        
        //Reference to View
        mRootView = null;

        //Clean the STL Viewer options everytime we create a new fragment
        optionClean();
       
        //Retrieves all given arguments
        arguments = getArguments();
        
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
            dataListView = (ListView) mRootView.findViewById(R.id.prints_data_list_view);
            dataListView.setAdapter(new DataTextAdapter(mContext));

        }

        //Retrieve button layouts
        upperButtonLayout = (LinearLayout) mRootView.findViewById(R.id.prints_detail_upper_buttons_layout);
        lowerButtonLayout = (LinearLayout) mRootView.findViewById(R.id.prints_detail_lower_buttons_layout);


        //Find tab host for tracing and initialize it
        traceTabHost = (TabHost) mRootView.findViewById(R.id.trace_tab_host);
        initializeTraceTabHost();

        //Load data from database
        new LoadDataTask().execute();

        return mRootView;

    }

    //Method for initializing the tab host
    private void initializeTraceTabHost(){
        traceTabHost.setup();

        //Details tab
        TabHost.TabSpec spec = traceTabHost.newTabSpec(ListContent.ID_DETAILS);
        spec.setIndicator(getTabIndicator("Detail"));
        spec.setContent(R.id.trace_tab1);
        traceTabHost.addTab(spec);

        //Materials tab
        spec = traceTabHost.newTabSpec(ListContent.ID_MATERIALS);
        spec.setIndicator(getTabIndicator("Material"));
        spec.setContent(R.id.trace_tab2);
        traceTabHost.addTab(spec);

        //Tests tab
        spec = traceTabHost.newTabSpec(ListContent.ID_TESTS);
        spec.setIndicator(getTabIndicator("Tests"));
        spec.setContent(R.id.trace_tab3);
        traceTabHost.addTab(spec);
    }

    //Create tab indicator to customize tabs for the tabhost
    private View getTabIndicator(String title) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trace_tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.trace_tab_title_textview);
        tv.setText(title);
        return view;
    }

    //Async task used to load all data to be displayed
    private class LoadDataTask extends AsyncTask<Integer, Integer, Integer> {

        private List<Print> printResult = null;
        private List<BuildDetailLink> buildDetailResult = null;
        private List<Detail> linkedDetails = new ArrayList<Detail>();

        @Override
        protected Integer doInBackground(Integer... integers) {
            ApiService apiService = databaseHandler.getApiService();

            try {
                //Fetch prints
                printResult =  apiService.fetchPrint(id).execute().body();
                if(printResult != null && printResult.size() > 0) {
                    print = printResult.get(0);
                } else {
                    Log.d("PrintsSpecificFragment", "Could not fetch any data with given ID");
                    return 1;
                }
                //Based on build id given in prints fetch all links to detail
                buildDetailResult = apiService.fetchDetailBuildLink(
                        Integer.parseInt(print.getBuildsId())).execute().body();

                //For each detail found, retrieve their data
                for(BuildDetailLink link : buildDetailResult){
                    List<Detail> detail = apiService.fetchDetail(
                            Integer.parseInt(link.getDetailsId())).execute().body();
                    linkedDetails.add(detail.get(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            //If we failed to retrieve a print, do nothing
            if(print == null) {
                Log.d("PrintsSpecificFragment", "Failed to retrieve prints");
                return;
            }
            //Fill the fields for the print
            String[] printTitles = {"Id", "Build id", "Operator", "Machine", "Powder weight start", "Powder weight end",
                    "Build platform material", "Build platform weight", "End time", "Start time"};
            String[] printValues = {print.getId(), print.getBuildsId(), print.getOperator(), print.getMachine(),
                    print.getPowderWeightStart(), print.getPowderWeightEnd(), print.getBuildPlatformMaterial(),
                    print.getBuildPlatformWeight(), print.getEndTime(), print.getStartTime()};

            dataListView.setAdapter(new DataTextAdapter(printTitles, printValues, mContext));

            //Fill the details field
            View view = LayoutInflater.from(mContext).inflate(R.layout.data_entry_list_item, null);
            detailsList.setAdapter(new DataEntryRecyclerViewAdapter<>(linkedDetails));
            detailsList.setLayoutManager(new LinearLayoutManager(mContext));
            detailsList.addItemDecoration(new DividerItemDecoration(mContext));

            //Create detail buttons
            for(int i = 0; i < linkedDetails.size(); i++){
                createDetailButton("D" + linkedDetails.get(i).getId());
            }

            //If the amount of details we have are more than 1
            //create a toggle button for showing all details at once
            if(linkedDetails.size() > 1) {
                ToggleButton showAll = new ToggleButton(mContext);
                showAll.setText("Show all");
                showAll.setTextOn("Show all");
                showAll.setTextOff("Show all");
                showAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (checked) {
                            setChecked(compoundButton);
                        }
                    }
                });


                toggleDetailButtons.add(showAll);
                lowerButtonLayout.addView(showAll);
            }
        }
    }


    private void setChecked(CompoundButton button){
        for(ToggleButton current : toggleDetailButtons) {
            if(button != current) {
                current.setChecked(false);
            }
        }
    }

    //Method for creating Detail buttons for the STL viewer
    private void createDetailButton(String name){
        ToggleButton button = new ToggleButton(mContext);
        button.setText(name);
        button.setTextOn(name);
        button.setTextOff(name);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    setChecked(buttonView);
                    //TODO: Open the specific detail view
                    optionClean();
                    String path = "/storage/emulated/0/PrintManager/Files/Feather/_stl/Feather.stl";
                    openFileDialog(path);
                }
            }
        });

        //Add button to the list to make them connected
        toggleDetailButtons.add(button);

        //Adds the buttons to the layouts
        if(upperButtonLayout.getChildCount() < MAX_BUTTONS_PER_LAYOUT){
            upperButtonLayout.addView(button);
        }else if(lowerButtonLayout.getChildCount() < MAX_BUTTONS_PER_LAYOUT*2) {
            lowerButtonLayout.addView(button);
        }else{
            //TODO: Find out what to do
        }
    }
}
