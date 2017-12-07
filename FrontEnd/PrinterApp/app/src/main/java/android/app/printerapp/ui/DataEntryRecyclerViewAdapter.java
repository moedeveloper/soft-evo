package android.app.printerapp.ui;

import android.app.printerapp.R;
import android.app.printerapp.model.Build;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DateEntryItemHolders.BuildItemHolder;
import android.app.printerapp.ui.DateEntryItemHolders.DataEntryItemHolder;
import android.app.printerapp.ui.DateEntryItemHolders.DetailItemHolder;
import android.app.printerapp.ui.DateEntryItemHolders.PrintItemHolder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Used by RecyclerView to show Data entries
 */

public class DataEntryRecyclerViewAdapter<E extends DataEntry> extends RecyclerView.Adapter<DataEntryItemHolder> {

    List<E> dataset;

    public DataEntryRecyclerViewAdapter (List<E> dataset){
        this.dataset = dataset;
    }

    @Override
    public DataEntryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Class<?> type = dataset.get(0).getClass();

        Class<?> p = Print.class;
        Class<?> d = Detail.class;
        Class<?> b = Build.class;

        View view = null;
        DataEntryItemHolder itemholder = null;

        if(type == p){
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.data_entry_list_item, parent, false);
            itemholder = new PrintItemHolder(view);
        }else if(type == d){
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.data_entry_list_item, parent, false);
            itemholder = new DetailItemHolder(view);
        }else if(type == b){
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.data_entry_list_item, parent, false);
            itemholder = new BuildItemHolder(view);
        }

        return itemholder;
    }

    @Override
    public void onBindViewHolder(DataEntryItemHolder holder, int position) {
        E dataEntry = dataset.get(position);
        holder.setName(dataEntry.getName());
        holder.setId(dataEntry.getId());
        holder.setCreationDate(dataEntry.getCreationDate());
    }

    @Override
    public int getItemCount() {
        if(dataset != null){
            return dataset.size();
        }
        return -1;
    }
}
