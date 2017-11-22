package android.app.printerapp;

import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by jcmma on 2017-11-22.
 */

public class SpecificActivity extends ActionBarActivity {
    public static final String CHOSEN_FRAGMENT_INTENT_TAG = "chosenFragment";
    public static final int START_DETAIL_FRAGMENT = 0;
    public static final int START_BUILD_FRAGMENT = 1;
    public static final int START_PRINT_FRAGMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_activity);

        int chosenFragment = getIntent().getIntExtra(CHOSEN_FRAGMENT_INTENT_TAG, -1);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.specific_activity_root_view, createChosenFragment(chosenFragment));
        fragmentTransaction.commit();
    }

    private Fragment createChosenFragment(int chosenFragment) {
        switch (chosenFragment) {
            case START_DETAIL_FRAGMENT:
                return new DetailsSpecificFragment();
            case START_BUILD_FRAGMENT:
                return null; //TODO: Implement this
            case START_PRINT_FRAGMENT:
                return new PrintsSpecificFragment();
            default:
                throw new IllegalArgumentException("Invalid fragment type chosen.");
        }
    }
}
