package android.app.printerapp.dataviews;

import android.app.printerapp.DividerItemDecoration;
import android.app.printerapp.ListContent;
import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.model.Build;
import android.app.printerapp.model.BuildDetailLink;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Material;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.app.printerapp.viewer.DataTextAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MaterialSpecificFragment extends SpecificFragment {
//---------------------------------------------------------------------------------------
//          VARIABLES
//---------------------------------------------------------------------------------------
    //Variables for specific print
    private int id;

    //Api
    File[] files;

    //Constants
    public static final String BUILD_ID = "build_id";
    private Material material;

//---------------------------------------------------------------------------------------
//          OVERRIDES
//---------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        getActivity().setTitle("MaplePrint - Material");
        //If there's no saved instance state, initialize variables
        if (savedInstanceState == null) {

            //Retrieve references to views
            ListView buildListView = (ListView) mRootView.findViewById(R.id.data_list_view);

            //Retrieve id from arguments
            if(arguments != null) {
                id = arguments.getInt(BUILD_ID);
            } else {
                id = 1;
            }
        }
        TextView title = (TextView) mRootView.findViewById(R.id.print_title);
        title.setText("Material M" + id);

        RelativeLayout imageHolder = (RelativeLayout) mRootView.findViewById(R.id.stl_viewer_holder_layout);
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.material_info);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        imageHolder.addView(imageView);


        //Load all data we need from database
        //and then display the data into the views we have
        new LoadDataTask().execute();

        return mRootView;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.material_layout_main;
    }

    @Override
    public void loadData() {
        new LoadDataTask().execute();
    }

    @Override
    public void createTabs() {
        createTab(ListContent.ID_PRINTS, "Print");
        createTab(ListContent.ID_TESTS, "Test");
    }

//---------------------------------------------------------------------------------------
//          FACTORY METHODS
//---------------------------------------------------------------------------------------

    //Factory method used to create this fragment
    public static MaterialSpecificFragment newInstance(int id){
        Bundle b = new Bundle();
        b.putInt(BUILD_ID, id);
        MaterialSpecificFragment bsf = new MaterialSpecificFragment();
        bsf.setArguments(b);
        return bsf;
    }

    @Override
    void onTagSelected(Object tag) {

        if(tag == null){
            return;
        }
        if(tag.equals(ListContent.ID_PRINTS)){
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
                List<Material> mResult = apiService.fetchMaterial(String.valueOf(id)).execute().body();
                if(dataIsOk(mResult)){
                    material = mResult.get(0);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            if(material == null){
                createAlertDialog("Cannot retrieve material");
                return;
            }

            String[] buildTitles = {"Id", "Creation date", "Pdf file name"};
            String[] buildValues = {material.getId(), material.getCreationDate(), material.getPdfName()};

            dataListView.setAdapter(new DataTextAdapter(buildTitles, buildValues, mContext));

            super.onPostExecute(integer);
        }
    }
}
