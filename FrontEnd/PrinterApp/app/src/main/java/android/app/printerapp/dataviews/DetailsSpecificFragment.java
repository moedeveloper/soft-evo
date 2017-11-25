package android.app.printerapp.dataviews;

import android.app.AlertDialog;
import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.FileManager;
import android.app.printerapp.ListContent;
import android.app.printerapp.Log;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Build;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.app.printerapp.viewer.DataTextAdapter;
import android.app.printerapp.viewer.STLViewer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcmma on 2017-11-21.
 */

public class DetailsSpecificFragment extends SpecificFragment {
    //---------------------------------------------------------------------------------------
//          VARIABLES
//---------------------------------------------------------------------------------------
    //Variables for specific print
    private int id;
    private Detail detail;
    private List<Print> linkedPrints = new ArrayList<>();
    private List<Build> linkedBuilds = new ArrayList<>();

    //Api
    File[] files;

    //Constants
    public static final String DETAIL_ID = "detail_id";

    //Views
    private STLViewer stlViewer;
    private LinearLayout upperButtonLayout;
    private ToggleButton  showDetailButton;

//---------------------------------------------------------------------------------------
//          OVERRIDES
//---------------------------------------------------------------------------------------


    @Override
    public void onResume() {
        super.onResume();
        if(showDetailButton == null){
            return;
        }
        if(showDetailButton.isChecked()){
            downloadAndOpenFile();
        } else {
            stlViewer.optionClean();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        //If there's no saved instance state, initialize variables
        if (savedInstanceState == null) {
            //Retrieve id from arguments
            if(arguments != null) {
                id = arguments.getInt(DETAIL_ID);
            } else {
                id = 1;
            }
        }

        upperButtonLayout = (LinearLayout) mRootView.findViewById(R.id.prints_detail_upper_buttons_layout);
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
        createTab(ListContent.ID_BUILDS, "Build");
        createTab(ListContent.ID_PRINTS, "Print");
    }

//---------------------------------------------------------------------------------------
//          HELPER FUNCTIONS
//---------------------------------------------------------------------------------------
    //Scans for files and saves them in a variable
    private void scanForFiles(){
        files = FileManager.scanStlFiles(mContext.getDir("Octoprint", mContext.MODE_PRIVATE).getAbsolutePath());
        for(File f : files){
            Log.d("DetailsSpecificFragment", f.getPath());
        }
    }

    private void downloadAndOpenFile() {
        if (!FileManager.modelExistsInSystem(detail)) {
            FileManager.downloadAndOpenFile(mContext, stlViewer, detail);
        } else {
            stlViewer.optionClean();
            stlViewer.openFileDialog(FileManager.getModelFile(detail).getAbsolutePath());
        }
    }

//---------------------------------------------------------------------------------------
//          FACTORY METHODS
//---------------------------------------------------------------------------------------

    //Factory method used to create this fragment
    public static DetailsSpecificFragment newInstance(int id){
        Bundle b = new Bundle();
        b.putInt(DETAIL_ID, id);
        DetailsSpecificFragment dsf = new DetailsSpecificFragment();
        dsf.setArguments(b);
        return dsf;
    }

    @Override
    void onTagSelected(Object tag) {
        if(tag == null){
            return;
        }
        if(tag.equals(ListContent.ID_PRINTS)){
            allTraceLists.get(ListContent.ID_PRINTS).setAdapter(new DataEntryRecyclerViewAdapter<>(linkedPrints));
            allTraceLists.get(ListContent.ID_PRINTS).setLayoutManager(new LinearLayoutManager(mContext));
            allTraceLists.get(ListContent.ID_PRINTS).addItemDecoration(new DividerItemDecoration(mContext));
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
                detail = apiService.fetchDetail(id).execute().body().get(0);
                //TODO: Implement an API function for getting builds from detail id
                List<BuildDetailLink> buildDetailResult = apiService.fetchBuildDetailLink(id).execute().body();

                //For each build found, retrieve their data
                for(BuildDetailLink link : buildDetailResult){
                    List<Build> build = apiService.fetchBuild(
                            Integer.parseInt(link.getBuildId())).execute().body();
                    linkedBuilds.add(build.get(0));
                    List<Print> print = apiService.fetchPrintFromBuild(
                            Integer.parseInt(link.getBuildId())).execute().body();
                    linkedPrints.add(print.get(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //If we failed to retrieve a print, do nothing
            if(detail == null) {
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Log.d("DetailsSpecificFragment", "Failed to retrieve prints");
                return;
            }

            String[] detailTitles = {"Id", "Name", "Creation date", "Company id", "File id", "Project id", "Comments"};
            String[] detailValues = {detail.getId(), detail.getName(), detail.getCreationDate(),
                    String.valueOf(detail.getCompanyId()), detail.getFileId(), String.valueOf(detail.getProjectId()),
                    detail.getComment()};

            dataListView.setAdapter(new DataTextAdapter(detailTitles, detailValues, mContext));

            allTraceLists.get(ListContent.ID_BUILDS).setAdapter(new DataEntryRecyclerViewAdapter<>(linkedBuilds));
            allTraceLists.get(ListContent.ID_BUILDS).setLayoutManager(new LinearLayoutManager(mContext));
            allTraceLists.get(ListContent.ID_BUILDS).addItemDecoration(new DividerItemDecoration(mContext));

            showDetailButton = new ToggleButton(mContext);
            showDetailButton.setText("Show model");
            showDetailButton.setTextOn("Hide model");
            showDetailButton.setTextOff("Show model");
            showDetailButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        downloadAndOpenFile();
                    } else {
                        stlViewer.optionClean();
                        stlViewer.draw();
                    }
                }
            });
            upperButtonLayout.addView(showDetailButton);

        }

    }
}
