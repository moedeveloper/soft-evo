package android.app.printerapp.ui.DateEntryItemHolders;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.printerapp.ListContent;
import android.app.printerapp.Log;
import android.app.printerapp.PrintsSpecificFragment;
import android.app.printerapp.R;
import android.view.View;

/**
 * Created by SAMSUNG on 2017-11-18.
 */

public class PrintItemHolder extends DataEntryItemHolder {
    public PrintItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onClick(View view) {
        FragmentManager fragmentManager;

        Log.d("PrintItemHolder", "I was pressed");

        try{
            final Activity activity = (Activity) view.getContext();
            fragmentManager = activity.getFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentManager.popBackStack();

            PrintsSpecificFragment printsSpecificFragment =
                    PrintsSpecificFragment.newInstance(getId());

            if(fragmentManager.findFragmentById(R.id.maintab4) == null) {
                fragmentTransaction.add(R.id.maintab4, printsSpecificFragment, ListContent.ID_PRINT_SPECIFIC);
            }else {
                fragmentTransaction.replace(R.id.maintab4, printsSpecificFragment);
            }
            fragmentTransaction.show(printsSpecificFragment).commit();

        } catch (ClassCastException e) {
            Log.d("DataEntryItemHolder", "Can't get the fragment manager with this");
        }
    }
}
