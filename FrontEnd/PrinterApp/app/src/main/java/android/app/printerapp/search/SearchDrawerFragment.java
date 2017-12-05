package android.app.printerapp.search;

import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.MaterialList;
import android.app.printerapp.model.Material;
import android.app.printerapp.model.Print;
import android.app.printerapp.model.PrintList;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class SearchDrawerFragment extends Fragment{

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */

    //View variables
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;
    private TestSearchView testSearchView;

    private boolean mUserLearnedDrawer;
    public static final String DATATYPE_PRINT = "print";
    public static final String DATATYPE_MATERIAL = "material";

    //-----------------------------------------------------
    //        OVERRIDES
    //-----------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize rootview
        View rootView = new LinearLayout(getContext());

        //Initialize search view
        testSearchView = new TestSearchView(getContext());
        testSearchView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        ((ViewGroup) rootView).addView(testSearchView);
        testSearchView.createSearchOption("Operator", new String[] {"Alex","Johan", "Shireen", "Mohamad"});

        return rootView;
    }

    //-----------------------------------------------------
    //        HELPER METHODS
    //-----------------------------------------------------
    public void loadData(String dataType){
        new LoadDataTask().execute(dataType);
    }

    public void addListenerToSearchView(PropertyChangeListener listener){
        testSearchView.addPropertyChangeListener(listener);
    }

    //-----------------------------------------------------
    //        INNER CLASSES
    //-----------------------------------------------------
    private class LoadDataTask extends AsyncTask<String,Void,Void>{
        PrintList resultPrints;
        MaterialList resultMaterials;

        @Override
        protected Void doInBackground(String... strings) {
            ApiService apiService = DatabaseHandler.getInstance().getApiService();

            try{
                if(strings[0].equals(DATATYPE_PRINT)){
                    resultPrints = apiService.fetchAllPrints().execute().body();
                } else if(strings[0].equals(DATATYPE_MATERIAL)){
                    resultMaterials= apiService.fetchAllMaterials().execute().body();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(resultPrints != null){
                ArrayList<DataEntry> details = new ArrayList<>();
                for(Print current : resultPrints.getPrints()){
                    details.add(current);
                }
                testSearchView.updateData(details);
            }

            if(resultMaterials != null){
                ArrayList<DataEntry> materials = new ArrayList<>();
                for(Material current : resultMaterials.getMaterials()){
                    materials.add(current);
                }
                testSearchView.updateData(materials);
            }
        }
    }


    //--------------------------------------------------------
    //      DRAWER IMPLEMENTATION
    //--------------------------------------------------------

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }
}
