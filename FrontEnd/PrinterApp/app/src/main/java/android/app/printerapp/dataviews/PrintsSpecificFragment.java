package android.app.printerapp.dataviews;

import android.app.AlertDialog;
import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.library.FileManager;
import android.app.printerapp.ListContent;
import android.app.printerapp.Log;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.app.printerapp.viewer.DataTextAdapter;
import android.app.printerapp.viewer.STLViewer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PrintsSpecificFragment extends SpecificFragment {
//---------------------------------------------------------------------------------------
//          VARIABLES
//---------------------------------------------------------------------------------------

    //Variables for specific print
    private int id;
    private Print print;

    //Api
    List<File> files = new ArrayList<>();

    //Constants
    public static final String PRINT_ID = "print_id";
    private final int MAX_BUTTONS_PER_LAYOUT = 5;

    //Views
    private LinearLayout upperButtonLayout;
    private LinearLayout lowerButtonLayout;
    List<ToggleButton> toggleDetailButtons = new ArrayList<>();
    private STLViewer stlViewer;
    private CompoundButton currentCheckedButton;

    //Data
    private List<Print> printResult = null;
    private List<BuildDetailLink> buildDetailResult = null;
    private List<Detail> linkedDetails = new ArrayList<Detail>();


//---------------------------------------------------------------------------------------
//          OVERRIDES
//---------------------------------------------------------------------------------------


    @Override
    public void onResume() {
        super.onResume();
        if(currentCheckedButton == null){
            stlViewer.optionClean();
        } else {
            downloadAndOpenFile(currentCheckedButton.getText().toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        //If there's no saved instance state, initialize variables
        if (savedInstanceState == null) {
            //Retrieve references to views
            upperButtonLayout = (LinearLayout) mRootView.findViewById(R.id.prints_detail_upper_buttons_layout);
            lowerButtonLayout = (LinearLayout) mRootView.findViewById(R.id.prints_detail_lower_buttons_layout);

            //Retrieve id from arguments
            if (arguments != null) {
                id = arguments.getInt(PRINT_ID);
            } else {
                id = 1;
            }
        }

        TextView title = (TextView) mRootView.findViewById(R.id.print_title);
        title.setText("Print P" + id);

        stlViewer = new STLViewer(mContext);
        RelativeLayout stlHolder = (RelativeLayout) mRootView.findViewById(R.id.stl_viewer_holder_layout);
        stlHolder.addView(stlViewer);

        //Clean the STL Viewer options every time we create a new fragment
        stlViewer.optionClean();

        //Scan for STL files and put them in "files" variable
        scanForFiles();

        //Load all data we need from database
        //and then display the data into the views we have
        new LoadDataTask().execute();

        return mRootView;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.prints_layout_main;
    }

    @Override
    public void loadData() {
        new LoadDataTask().execute();
    }

    @Override
    public void createTabs() {
        createTab(ListContent.ID_DETAILS, "Detail");
        createTab(ListContent.ID_MATERIALS, "Material");
        createTab(ListContent.ID_TESTS, "Tests");
    }

    //---------------------------------------------------------------------------------------
//          HELPER FUNCTIONS
//---------------------------------------------------------------------------------------
    //Scans for files and saves them in a variable
    private void scanForFiles() {
        files = Arrays.asList(FileManager.scanStlFiles(mContext.getDir("Octoprint",
                mContext.MODE_PRIVATE).getAbsolutePath()));
        for (File f : files) {
            Log.d("DetailsSpecificFragment", f.getPath());
        }
    }

    private void downloadAndOpenFile(String idName) {
        for (Detail d : linkedDetails) {
            if (d.getIdName().equals(idName)) {
                if (!FileManager.modelExistsInSystem(d)) {
                    //todo: The filemanager uses the stlViewer to open file. This needs to be
                    //todo: built better. Now there's a bug that causes nullpointerexception
                    FileManager.downloadAndOpenFile(mContext, stlViewer, d);
                } else {
                    stlViewer.optionClean();
                    stlViewer.openFileDialog(FileManager.getModelFile(d).getAbsolutePath());
                }

                break;
            }
        }
    }

    //Check selected button and deslect all others
    private void setChecked(CompoundButton button) {
        for (ToggleButton current : toggleDetailButtons) {
            if (button != current) {
                current.setChecked(false);
            }
        }
        currentCheckedButton = button;
    }

//---------------------------------------------------------------------------------------
//          FACTORY METHODS
//---------------------------------------------------------------------------------------

    //Factory method used to create this fragment
    public static PrintsSpecificFragment newInstance(int id) {
        Bundle b = new Bundle();
        b.putInt(PRINT_ID, id);
        PrintsSpecificFragment psf = new PrintsSpecificFragment();
        psf.setArguments(b);
        return psf;
    }

    @Override
    void onTagSelected(Object tag) {

    }

    //Method for creating Detail buttons for the STL viewer
    private void createDetailButton(final String idName) {
        ToggleButton button = new ToggleButton(mContext);
        button.setText(idName);
        button.setTextOn(idName);
        button.setTextOff(idName);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setChecked(buttonView);
                    downloadAndOpenFile(idName);
                } else {
                    currentCheckedButton = null;
                    stlViewer.optionClean();
                    stlViewer.draw();
                }
            }
        });

        //Add button to the list to make them connected
        toggleDetailButtons.add(button);

        //Adds the buttons to the layouts
        if (upperButtonLayout.getChildCount() < MAX_BUTTONS_PER_LAYOUT) {
            upperButtonLayout.addView(button);
        } else if (lowerButtonLayout.getChildCount() < MAX_BUTTONS_PER_LAYOUT * 2) {
            lowerButtonLayout.addView(button);
        } else {
            //TODO: Find out what to do
        }
    }
//---------------------------------------------------------------------------------------
//          PRIVATE CLASSES
//---------------------------------------------------------------------------------------

    //Async task used to load all data to be displayed
    private class LoadDataTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            ApiService apiService = databaseHandler.getApiService();

            try {
                //Fetch prints
                printResult = apiService.fetchPrint(id).execute().body();
                if (printResult != null && printResult.size() > 0) {
                    print = printResult.get(0);
                } else {
                    Log.d("PrintsSpecificFragment", "Could not fetch any data with given ID");
                    return 1;
                }
                //Based on build id given in prints fetch all links to detail
                buildDetailResult = apiService.fetchDetailBuildLink(
                        Integer.parseInt(print.getBuildsId())).execute().body();

                //For each detail found, retrieve their data
                for (BuildDetailLink link : buildDetailResult) {
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
            if (print == null) {
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
            allTraceLists.get(ListContent.ID_DETAILS).setAdapter(new DataEntryRecyclerViewAdapter<>(linkedDetails));
            allTraceLists.get(ListContent.ID_DETAILS).setLayoutManager(new LinearLayoutManager(mContext));
            allTraceLists.get(ListContent.ID_DETAILS).addItemDecoration(new DividerItemDecoration(mContext));

            //Create detail buttons
            for (int i = 0; i < linkedDetails.size(); i++) {
                createDetailButton("D" + linkedDetails.get(i).getId());
            }
        }
    }
}

