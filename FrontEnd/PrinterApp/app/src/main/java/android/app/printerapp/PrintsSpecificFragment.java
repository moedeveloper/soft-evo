package android.app.printerapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.app.printerapp.viewer.DataTextAdapter;
import android.app.printerapp.viewer.STLViewer;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PrintsSpecificFragment extends Fragment {

    private Context mContext;
    private View mRootView;
    private Bundle arguments;

    private int id;
    private Print print;
    private DatabaseHandler databaseHandler;

    Map<String, RecyclerView> allTraceLists;

    //CONSTANTS
    public static final String PRINT_ID = "print_id";
    private final int MAX_BUTTONS_PER_LAYOUT = 5;

    //Views
    private ListView dataListView;
    private LinearLayout upperButtonLayout;
    private LinearLayout lowerButtonLayout;
    private TabHost traceTabHost;
    List<ToggleButton> toggleDetailButtons = new ArrayList<>();

    //Alert dialog builder
    AlertDialog.Builder alertDialogBuilder;

    //Files
    File[] files;
    private STLViewer stlViewer;

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
       
        //Retrieves all given arguments
        arguments = getArguments();
        
        if(arguments != null) {
            id = arguments.getInt(PRINT_ID);
        } else {
            id = 1;
        }

        if (savedInstanceState == null) {
            //Get the rootview from its parent
            mRootView = inflater.inflate(R.layout.prints_layout_main,
                  container, false);
            mContext = getActivity();

            dataListView = (ListView) mRootView.findViewById(R.id.prints_data_list_view);
            dataListView.setAdapter(new DataTextAdapter(mContext));
        }

        stlViewer = (STLViewer) mRootView.findViewById(R.id.stl_viewer);

        //Clean the STL Viewer options everytime we create a new fragment
        stlViewer.optionClean();


        //Alert dialog for when data cannot be loaded from the server
        alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("Retrieving data from the database failed. Would you like to try again?");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new LoadDataTask().execute();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        //Scan for files
        FileManager.downloadFile(mContext);
        files = FileManager.scanStlFiles(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        for(File f : files){
            Log.d("PrintsSpecificFragment", f.getPath());
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

    private class TraceTabFactory implements TabHost.TabContentFactory {

        public View createTabContent(String tag) {

            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        LinearLayout.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            RecyclerView recyclerView = new RecyclerView(mContext);
            //Save the recyclerview in our map so we can access it
            allTraceLists.put(tag, recyclerView);
            linearLayout.addView(recyclerView);

            return linearLayout;
        }
    }

    //Method for initializing the tab host
    private void initializeTraceTabHost(){
        traceTabHost.setup();
        allTraceLists = new HashMap<>();

        //Details tab
        createTab(ListContent.ID_DETAILS, "Detail");

        //Materials tab
        createTab(ListContent.ID_MATERIALS, "Material");

        //Tests tab
        createTab(ListContent.ID_TESTS, "Tests");
    }

    private void createTab(String tag, String title){
        TabHost.TabSpec spec = traceTabHost.newTabSpec(tag);
        spec.setIndicator(getTabIndicator(title));
        spec.setContent(new TraceTabFactory());
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
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
            allTraceLists.get(ListContent.ID_DETAILS).setAdapter(new DataEntryRecyclerViewAdapter<>(linkedDetails));
            allTraceLists.get(ListContent.ID_DETAILS).setLayoutManager(new LinearLayoutManager(mContext));
            allTraceLists.get(ListContent.ID_DETAILS).addItemDecoration(new DividerItemDecoration(mContext));

            //Create detail buttons
            for(int i = 0; i < linkedDetails.size(); i++){
                createDetailButton("D" + linkedDetails.get(i).getId());
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
                    //TODO: Search for the correct file to open
                    STLViewer.optionClean();
                    String path = files[(int)(Math.random()*files.length)].getAbsolutePath();
                    STLViewer.openFileDialog(path);
                }else{
                    STLViewer.optionClean();
                    STLViewer.draw();
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
