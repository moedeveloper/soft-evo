package android.app.printerapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.app.printerapp.devices.DevicesFragment;
//import android.app.printerapp.devices.database.DatabaseController;
//import android.app.printerapp.devices.discovery.InitialFragment;
//import android.app.printerapp.devices.printview.GcodeCache;
//import android.app.printerapp.devices.printview.PrintViewFragment;
//import android.app.printerapp.history.HistoryDrawerAdapter;
//import android.app.printerapp.history.SwipeDismissListViewTouchListener;
//import android.app.printerapp.library.LibraryController;
//import android.app.printerapp.library.LibraryFragment;
//import android.app.printerapp.library.detail.DetailViewFragment;
//import android.app.printerapp.settings.SettingsFragment;
//import android.app.printerapp.util.ui.AnimationHelper;
import android.app.printerapp.viewer.ViewerMainFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * modified by momor1 on 06/10/2017.
 */
public class MainActivity extends ActionBarActivity {

    //List of Fragments
    //private DevicesFragment mDevicesFragment; //Devices fragment @static for refresh
    //private LibraryFragment mLibraryFragment; //Storage fragment
    private ViewerMainFragment mViewerFragment; //Print panel fragment @static for model load

    //Class specific variables
    private static Fragment mCurrent; //The current shown fragment @static
    private static FragmentManager mManager; //Fragment manager to handle transitions @static
    private static DialogController mDialog; //Dialog controller @static

    private static TabHost mTabHost;

    //Drawer
    private static DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private static ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * Since API level 11, thread policy has changed and now does not allow network operation to
         * be executed on UI thread (NetworkOnMainThreadException), so we have to add these lines to
         * permit it.
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mTabHost = (TabHost) findViewById(R.id.tabHost);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Initialize variables
        mManager = getFragmentManager();

        mViewerFragment = (ViewerMainFragment) getFragmentManager().findFragmentByTag(ListContent.ID_VIEWER);


        initDrawer();


        //Set tab host for the view
        setTabHost();

        ApiController apiController = new ApiController();
        apiController.testDetailsApi();

        Button addBotton =  (Button) findViewById(R.id.button_add_details);
        addBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),add_details.class);
                startActivityForResult(intent, 0);
            }
        });

        Button listDetailsButton = (Button) findViewById(R.id.button_list_details);
        listDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailsListActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void performClick(int i){

        mTabHost.setCurrentTab(i);

    }

    //Initialize history drawer
    public void initDrawer(){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                   /* host Activity */
                mDrawerLayout,                /* DrawerLayout object */
                R.string.add,            /* "open drawer" description */
                R.string.cancel         /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setSelector(getResources().getDrawable(R.drawable.selectable_rect_background_green));

        View drawerListEmptyView = findViewById(R.id.history_empty_view);
        mDrawerList.setEmptyView(drawerListEmptyView);

        LayoutInflater inflater = getLayoutInflater();
        //mDrawerList.addHeaderView(inflater.inflate(R.layout.history_drawer_header, null));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mDrawerLayout.closeDrawers();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void setTabHost() {

        mTabHost.setup();

        //Models tab
        TabHost.TabSpec spec = mTabHost.newTabSpec("Library");
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_models)));
        spec.setContent(R.id.maintab1);
        mTabHost.addTab(spec);

        //Print panel tab
        spec = mTabHost.newTabSpec("Panel");
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_print)));
        spec.setContent(R.id.maintab2);
        mTabHost.addTab(spec);

        //Print view tab
        spec = mTabHost.newTabSpec("Printer");
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_devices)));
        spec.setContent(R.id.maintab3);
        mTabHost.addTab(spec);


        /*
        if (DatabaseController.count() > 0){
            mTabHost.setCurrentTab(0);
            onItemSelected(0);
        } else {
            mTabHost.setCurrentTab(2);
            onItemSelected(2);

        }
*/

        mTabHost.getTabWidget().setDividerDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                View currentView = mTabHost.getCurrentView();
                //AnimationHelper.inFromRightAnimation(currentView);

                onItemSelected(mTabHost.getCurrentTab());

            }
        });

    }

    //handle action bar menu open
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
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

        if (id!= 1) {

            ViewerMainFragment.hideActionModePopUpWindow();
            ViewerMainFragment.hideCurrentActionPopUpWindow();
        }

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

        if (mViewerFragment != null) {
            if (mCurrent != mViewerFragment) {
                //Make the surface invisible to avoid frame overlapping
                mViewerFragment.setSurfaceVisibility(0);
            } else {
                //Make the surface visible when we press
                mViewerFragment.setSurfaceVisibility(1);
            }
        }

        //Show current fragment
        if (mCurrent != null) {
            Log.i("OUT", "Changing " + mCurrent.getTag());
            fragmentTransaction.show(mCurrent).commit();
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }


    }
    /**
     * Method to create a new type of fragment to show special detailed views.
     *
     * @param type  Type of detailed view 0: DetailView 1: PrintView
     * @param id Extra argument to the fragment DetailView: File index, PrintView: Printer id
     */
    public static void showExtraFragment(int type, long id) {

        //New transaction
        FragmentTransaction mTransaction = mManager.beginTransaction();
        //mTransaction.setCustomAnimations(0, 0 , 0, R.anim.fragment_slide_out_left);

        //Add current fragment to the backstack and hide it (will show again later)
        mTransaction.addToBackStack(mCurrent.getTag());
        mTransaction.hide(mCurrent);

        switch (type) {

            case 0:

                //closePrintView();
                mManager.popBackStack();
                //SettingsFragment settings = new SettingsFragment();
                //mTransaction.replace(R.id.container_layout, settings, ListContent.ID_SETTINGS).commit();

                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mDrawerToggle.setDrawerIndicatorEnabled(false);

                break;

            case 1:

                mCurrent.setMenuVisibility(false);
                //New detailview with the printer name as extra
                //PrintViewFragment detailp = new PrintViewFragment();
                Bundle argsp = new Bundle();
                argsp.putLong("id", id);
                //detailp.setArguments(argsp);
                //mTransaction.replace(R.id.maintab3, detailp, ListContent.ID_PRINTVIEW).commit();

                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mDrawerToggle.setDrawerIndicatorEnabled(false);

                break;

            case 2:

                //InitialFragment initial = new InitialFragment();
                //mTransaction.replace(R.id.maintab3, initial, ListContent.ID_INITIAL).commit();

                break;
        }


    }

    @Override
    public void onBackPressed() {

        if (mCurrent != null) {
            Fragment fragment = mManager.findFragmentByTag(ListContent.ID_SETTINGS);
            //Cursor c = DatabaseController.retrieveDeviceList();

            if ((fragment != null) ){ //|| (c.getCount() > 1)


                if (mManager.popBackStackImmediate()){

                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                }else super.onBackPressed();
            } else super.onBackPressed();


        } else {
            super.onBackPressed();
        }
    }

    //Show dialog
    public static void showDialog(String msg) {
        mDialog.displayDialog(msg);
    }


    @Override
    protected void onDestroy() {

        // Unregister since the activity is not visible
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mAdapterNotification);

        super.onDestroy();
    }

    @Override
    protected void onResume() {

        //NotificationReceiver.setForeground(true);

        super.onResume();

    }

    @Override
    protected void onPause() {
        //NotificationReceiver.setForeground(false);
        super.onPause();
    }

    public void showAddDetailsActivity(){

    }
}
