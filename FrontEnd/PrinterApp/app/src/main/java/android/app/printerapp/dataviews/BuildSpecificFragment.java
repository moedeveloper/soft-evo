package android.app.printerapp.dataviews;

import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.ListContent;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.app.printerapp.viewer.DataTextAdapter;
import android.os.AsyncTask;
import android.app.printerapp.model.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BuildSpecificFragment extends SpecificFragment {
//---------------------------------------------------------------------------------------
//          VARIABLES
//---------------------------------------------------------------------------------------
    //Variables for specific print
    private int id;
    private Build build;
    private List<Print> linkedPrints;
    private List<Detail> linkedDetails = new ArrayList<>();

    private ListView buildListView;

    //Api
    File[] files;

    //Constants
    public static final String BUILD_ID = "build_id";

//---------------------------------------------------------------------------------------
//          OVERRIDES
//---------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        //If there's no saved instance state, initialize variables
        if (savedInstanceState == null) {

            //Retrieve references to views
            buildListView = (ListView) mRootView.findViewById(R.id.data_list_view);


            //Retrieve id from arguments
            if(arguments != null) {
                id = arguments.getInt(BUILD_ID);
            } else {
                id = 1;
            }
        }

        RelativeLayout imageHolder = (RelativeLayout) mRootView.findViewById(R.id.stl_viewer_holder_layout);
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.magics);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        imageHolder.addView(imageView);


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
        createTab(ListContent.ID_PRINTS, "Print");
        createTab(ListContent.ID_MATERIALS, "Material");
    }

//---------------------------------------------------------------------------------------
//          FACTORY METHODS
//---------------------------------------------------------------------------------------

    //Factory method used to create this fragment
    public static BuildSpecificFragment newInstance(int id){
        Bundle b = new Bundle();
        b.putInt(BUILD_ID, id);
        BuildSpecificFragment bsf = new BuildSpecificFragment();
        bsf.setArguments(b);
        return bsf;
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
                build = apiService.fetchBuild(id).execute().body().get(0);
                List<BuildDetailLink> buildDetailResult = apiService.fetchDetailBuildLink(id).execute().body();
                linkedPrints = apiService.fetchPrintFromBuild(id).execute().body();

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

            String[] buildTitles = {"Id", "Creation date", "Comments"};
            String[] buildValues = {build.getId(), build.getCreationDate(), build.getComment()};

            dataListView.setAdapter(new DataTextAdapter(buildTitles, buildValues, mContext));

            allTraceLists.get(ListContent.ID_DETAILS).setAdapter(new DataEntryRecyclerViewAdapter<>(linkedDetails));
            allTraceLists.get(ListContent.ID_DETAILS).setLayoutManager(new LinearLayoutManager(mContext));
            allTraceLists.get(ListContent.ID_DETAILS).addItemDecoration(new DividerItemDecoration(mContext));

            super.onPostExecute(integer);


        }

    }


}
