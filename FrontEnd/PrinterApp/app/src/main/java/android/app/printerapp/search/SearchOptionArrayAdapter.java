package android.app.printerapp.search;

import android.app.printerapp.model.DataEntry;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-12-05.
 */

public class SearchOptionArrayAdapter<T> extends ArrayAdapter<T> {
    public SearchOptionArrayAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public SearchOptionArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public SearchOptionArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull T[] objects) {
        super(context, resource, objects);
    }

    public SearchOptionArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SearchOptionArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    public SearchOptionArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(getItem(position) instanceof DataEntry){
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).
                        inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            DataEntry data = (DataEntry) getItem(position);
            if(data != null) {
                ((TextView) convertView).setText(data.getName());
            }
            return convertView;
        }else {
            return super.getView(position, convertView, parent);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getView(position, convertView, parent);
    }
}
