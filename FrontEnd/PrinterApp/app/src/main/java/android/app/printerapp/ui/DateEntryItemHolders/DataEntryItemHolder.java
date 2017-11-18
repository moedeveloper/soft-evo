package android.app.printerapp.ui.DateEntryItemHolders;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.printerapp.ListContent;
import android.app.printerapp.Log;
import android.app.printerapp.PrintsSpecificFragment;
import android.app.printerapp.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Defines the contents of the rows of RecyclerView
 */

public abstract class DataEntryItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Object image;
    private TextView name;
    private TextView id;
    private TextView creationDate;

    public DataEntryItemHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.detail_list_item_name);
        id = (TextView) itemView.findViewById(R.id.detail_list_item_id);
        creationDate = (TextView) itemView.findViewById(R.id.detail_list_item_creation_date);
        itemView.setOnClickListener(this);
    }

    public int getId(){
        return Integer.parseInt(id.getText().toString());
    }

    public void setId(String id){
        this.id.setText(id);
    }

    public void setName(String name){
        this.name.setText(name);
    }

    public void setCreationDate(String creationDate){
        this.creationDate.setText(creationDate);
    }

    public void setImage(){
        this.image = image;
    }


    //TODO: Perhaps move all fragment changing to a manager class
    //TODO: Perhaps move all instantiation of fragments to a factory class

    @Override
    public abstract void onClick(View view);
}

