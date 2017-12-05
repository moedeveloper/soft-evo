package android.app.printerapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.printerapp.database.DatabaseController;
import android.app.printerapp.dataviews.BuildsFragment;
import android.app.printerapp.dataviews.DetailsFragment;
import android.app.printerapp.dataviews.HomeFragment;
import android.app.printerapp.dataviews.PrintsFragment;
import android.app.printerapp.library.FileManager;
import android.app.printerapp.ui.AnimationHelper;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by alberto-baeza on 1/21/15.
 */
public class MainActivity extends ActionBarActivity implements PropertyChangeListener {

    //List of Fragments

    private HomeFragment mHomeFragment; //Front page
    private DetailsFragment mDetailsFragment;
    private PrintsFragment mPrintsFragment;
    private BuildsFragment mBuildsFragment;

    public static final String PRINT_CLICKED = "print_clicked";

    //Class specific variables
    private static Fragment mCurrent; //The current shown fragment @static
    private static FragmentManager mManager; //Fragment manager to handle transitions @static
    private static DialogController mDialog; //Dialog controller @static

    private static TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * Since API level 11, thread policy has changed and now does not allow network operation to
         * be executed on UI thread (NetworkOnMainThreadException), so we have to add these lines to
         * permit it.
         */
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);

        FileManager.deleteCache(this);
        setContentView(R.layout.main_activity);

        mTabHost = (TabHost) findViewById(R.id.tabHost);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Initialize variables
        mManager = getFragmentManager();
        mDialog = new DialogController(this);

        //Initialize fragments
        mManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });

        //Set tab host for the view
        setTabHost();

    }

    public static void performClick(int i){
        mTabHost.setCurrentTab(i);
    }

    public void setTabHost() {

        mTabHost.setup();

        //Home tab
        TabHost.TabSpec spec = mTabHost.newTabSpec(ListContent.ID_HOME);
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_home)));
        spec.setContent(R.id.maintab0);
        mTabHost.addTab(spec);

        //Details tab
        spec = mTabHost.newTabSpec(ListContent.ID_DETAILS);
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_details)));
        spec.setContent(R.id.maintab1);
        mTabHost.addTab(spec);

        //Builds tab
        spec = mTabHost.newTabSpec(ListContent.ID_BUILDS);
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_builds)));
        spec.setContent(R.id.maintab2);
        mTabHost.addTab(spec);

        //Prints tab
        spec = mTabHost.newTabSpec(ListContent.ID_PRINTS);
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_prints)));
        spec.setContent(R.id.maintab3);
        mTabHost.addTab(spec);

        //Materials tab
        spec = mTabHost.newTabSpec(ListContent.ID_MATERIALS);
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_materials)));
        spec.setContent(R.id.maintab4);
        mTabHost.addTab(spec);

        //Tests tab
        spec = mTabHost.newTabSpec(ListContent.ID_TESTS);
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_tests)));
        spec.setContent(R.id.maintab5);
        mTabHost.addTab(spec);

        if (DatabaseController.count() > 0){
            mTabHost.setCurrentTab(2);
            onItemSelected(2);
        } else {
            mTabHost.setCurrentTab(0);
            onItemSelected(0);

        }


        mTabHost.getTabWidget().setDividerDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                View currentView = mTabHost.getCurrentView();
                AnimationHelper.inFromRightAnimation(currentView);
                onItemSelected(mTabHost.getCurrentTab());
            }
        });

    }

    /**
     * Return the custom view of the tab
     *
     * @param title Title of the tab
     * @return Custom view of a tab layout
     */
    private View getTabIndicator(String title) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.main_activity_tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.tab_title_textview);
        tv.setText(title);
        return view;
    }

    public void onItemSelected(int id) {

        Log.i("OUT", "Pressed " + id);
        //start transaction
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();

        //Pop backstack to avoid having bad references when coming from a Detail view
        mManager.popBackStack();

        //If there is a fragment being shown, hide it to show the new one
        if (mCurrent != null) {
            try {
                fragmentTransaction.hide(mCurrent);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        //Select fragment
        switch (id) {

            case 0: {
                //Check if we already created the Fragment to avoid having multiple instances
                if (getFragmentManager().findFragmentByTag(ListContent.ID_HOME) == null) {
                    mHomeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.maintab0, mHomeFragment, ListContent.ID_HOME);
                }
                mCurrent = mHomeFragment;

            }

            case 1: {
                //Check if we already created the Fragment to avoid having multiple instances
                if (getFragmentManager().findFragmentByTag(ListContent.ID_DETAILS) == null) {
                    mDetailsFragment = new DetailsFragment();
                    fragmentTransaction.add(R.id.maintab1, mDetailsFragment, ListContent.ID_DETAILS);
                }
                mCurrent = mDetailsFragment;

            }

            break;
            case 2: {
                if (getFragmentManager().findFragmentByTag(ListContent.ID_BUILDS) == null) {
                    mBuildsFragment = new BuildsFragment();
                    fragmentTransaction.add(R.id.maintab2, mBuildsFragment, ListContent.ID_BUILDS);
                }
                mCurrent = mBuildsFragment;
            }
            break;

            case 3: {
                if (getFragmentManager().findFragmentByTag(ListContent.ID_PRINTS) == null) {
                    mPrintsFragment = new PrintsFragment();
                    fragmentTransaction.add(R.id.maintab3, mPrintsFragment, ListContent.ID_PRINTS);
                }
                mCurrent = mPrintsFragment;
            }
        }

        //TODO: Find a way to do this
        /*PrintsSpecificFragment psc = (PrintsSpecificFragment) mManager.findFragmentByTag(ListContent.ID_PRINT_SPECIFIC);
            if(psc != null){
                if(mCurrent != psc){
                    psc.setSurfaceVisibility(0);

                } else {
                    //Make the surface visible when we press
                    psc.setSurfaceVisibility(1);
                }
            }
        */

        //Show current fragment
        if (mCurrent != null) {
            Log.i("OUT", "Changing " + mCurrent.getTag());
            fragmentTransaction.show(mCurrent).commit();
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName() == PRINT_CLICKED){
            Log.d("Test", "I got clicked! : " + event.getNewValue());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        FileManager.deleteCache(this);
    }
}
