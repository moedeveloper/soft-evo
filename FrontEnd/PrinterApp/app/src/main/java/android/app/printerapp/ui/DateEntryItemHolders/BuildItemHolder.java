package android.app.printerapp.ui.DateEntryItemHolders;

import android.app.printerapp.SpecificActivity;
import android.app.printerapp.ui.DateEntryItemHolders.DataEntryItemHolder;
import android.view.View;

/**
 * Created by SAMSUNG on 2017-11-22.
 */

public class BuildItemHolder extends DataEntryItemHolder {

    public BuildItemHolder(View view) {
        super(view);
    }

    @Override
    public int getFragmentType() {
        return SpecificActivity.START_BUILD_FRAGMENT;
    }
}
