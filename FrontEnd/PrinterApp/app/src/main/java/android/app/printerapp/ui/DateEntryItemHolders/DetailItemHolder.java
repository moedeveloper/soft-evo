package android.app.printerapp.ui.DateEntryItemHolders;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.printerapp.ListContent;
import android.app.printerapp.Log;
import android.app.printerapp.SpecificActivity;
import android.app.printerapp.dataviews.PrintsSpecificFragment;
import android.app.printerapp.R;
import android.view.View;

/**
 * Created by SAMSUNG on 2017-11-18.
 */

public class DetailItemHolder extends DataEntryItemHolder {
    public DetailItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getFragmentType() {
        return SpecificActivity.START_DETAIL_FRAGMENT;
    }
}
