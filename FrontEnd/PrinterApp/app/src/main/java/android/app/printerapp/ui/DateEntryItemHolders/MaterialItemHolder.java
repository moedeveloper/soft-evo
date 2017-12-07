package android.app.printerapp.ui.DateEntryItemHolders;

import android.app.printerapp.SpecificActivity;
import android.view.View;

/**
 * Created by SAMSUNG on 2017-11-22.
 */

public class MaterialItemHolder extends DataEntryItemHolder {

    public MaterialItemHolder(View view) {
        super(view);
    }

    @Override
    public int getFragmentType() {
        return SpecificActivity.START_MATERIAL_FRAGMENT;
    }
}
