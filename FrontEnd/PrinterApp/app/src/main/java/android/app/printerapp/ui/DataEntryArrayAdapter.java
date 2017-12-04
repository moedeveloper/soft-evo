package android.app.printerapp.ui;

import android.app.printerapp.SpecificActivity;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Print;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAMSUNG on 2017-12-04.
 */

public class DataEntryArrayAdapter<T> extends ArrayAdapter<T> {

    List<Boolean> checkBoxState = new ArrayList();

    public DataEntryArrayAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public DataEntryArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public DataEntryArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull T[] objects) {
        super(context, resource, objects);
    }

    public DataEntryArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public DataEntryArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    public DataEntryArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<T> objects) {
        super(context, resource, textViewResourceId, objects);

    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DataEntry dataEntry;
        if(getItem(position) instanceof DataEntry) {
            checkBoxState.add(false);
            dataEntry = (DataEntry) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }
            // Lookup view for data population
            if (dataEntry != null) {
                ((CheckedTextView) convertView).setText(dataEntry.getIdName());
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((CheckedTextView)view).isChecked()){
                        ((CheckedTextView) view).setChecked(false);
                        checkBoxState.set(position, false);
                    }else{
                        ((CheckedTextView) view).setChecked(true);
                        checkBoxState.set(position, true);
                    }
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(view.getContext(), SpecificActivity.class);
                    intent.putExtra(SpecificActivity.CHOSEN_FRAGMENT_INTENT_TAG, getFragmentType(dataEntry));
                    if (dataEntry != null) {
                        intent.putExtra(SpecificActivity.ID, Integer.parseInt(dataEntry.getId()));
                    }
                    view.getContext().startActivity(intent);
                    return false;
                }
            });
    }
        // Return the completed view to render on screen
        return convertView;
    }

    public List<Boolean> getCheckBoxState(){
        return checkBoxState;
    }

    public void clearChecked(){
        for(Boolean current : checkBoxState){
            current = false;
        }
    }

    private int getFragmentType(DataEntry dataEntry){
        if(dataEntry.getClass() == Print.class){
            return SpecificActivity.START_PRINT_FRAGMENT;
        } else if (dataEntry.getClass() == Detail.class){
            return SpecificActivity.START_DETAIL_FRAGMENT;
        }
        else return -1;
    }

}
