package android.app.printerapp;

import android.app.Fragment;
import android.app.printerapp.dataviews.BuildSpecificFragment;
import android.app.printerapp.dataviews.DetailsSpecificFragment;
import android.app.printerapp.dataviews.MaterialSpecificFragment;
import android.app.printerapp.dataviews.PrintsSpecificFragment;
import android.app.printerapp.dataviews.TestSpecificFragment;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by jcmma on 2017-11-22.
 */

public class SpecificActivity extends ActionBarActivity {

    //Constants
    public static final String CHOSEN_FRAGMENT_INTENT_TAG = "chosenFragment";
    public static final int START_DETAIL_FRAGMENT = 0;
    public static final int START_BUILD_FRAGMENT = 1;
    public static final int START_PRINT_FRAGMENT = 2;
    public static final String ID = "data_id";
    public static final int START_MATERIAL_FRAGMENT = 3;
    public static final int START_TEST_FRAGMENT = 4;

    //Variables
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_activity);

        int chosenFragment = getIntent().getIntExtra(CHOSEN_FRAGMENT_INTENT_TAG, -1);
        id = getIntent().getIntExtra(ID, 0);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.specific_activity_root_view, createChosenFragment(chosenFragment));
        fragmentTransaction.commit();

    }

    private Fragment createChosenFragment(int chosenFragment) {
        switch (chosenFragment) {
            case START_DETAIL_FRAGMENT:
                return DetailsSpecificFragment.newInstance(id);
            case START_BUILD_FRAGMENT:
                return BuildSpecificFragment.newInstance(id);
            case START_PRINT_FRAGMENT:
                return PrintsSpecificFragment.newInstance(id);
            case START_MATERIAL_FRAGMENT:
                return MaterialSpecificFragment.newInstance(id);
            case START_TEST_FRAGMENT:
                return TestSpecificFragment.newInstance(id);
            default:
                throw new IllegalArgumentException("Invalid fragment type chosen.");
        }
    }
}
