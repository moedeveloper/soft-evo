package android.app.printerapp;

import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.Detail;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Used by RecyclerView to show Data entries
 */

public class DataEntryRecyclerViewAdapter<E extends DataEntry> extends RecyclerView.Adapter<DataEntryItemHolder> {

    Context context;
    List<E> dataset;

    public DataEntryRecyclerViewAdapter (Context context, List<E> dataset){
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public DataEntryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_entry_list_item, parent, false);
        DataEntryItemHolder itemholder = new DataEntryItemHolder(view);
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
        return dataset.size();
    }
}
