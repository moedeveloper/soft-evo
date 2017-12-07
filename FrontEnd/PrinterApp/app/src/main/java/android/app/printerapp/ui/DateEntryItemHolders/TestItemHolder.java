package android.app.printerapp.ui.DateEntryItemHolders;

import android.app.printerapp.SpecificActivity;
import android.view.View;

/**
 * Created by SAMSUNG on 2017-11-18.
 */

public class TestItemHolder extends DataEntryItemHolder {
    public TestItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getFragmentType() {
        return SpecificActivity.START_TEST_FRAGMENT;
    }
}
