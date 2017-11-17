package android.app.printerapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Defines the contents of the rows of RecyclerView
 */

public class DataEntryItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Object image;
    private TextView name;
    private TextView id;
    private TextView creationDate;

    public DataEntryItemHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.detail_list_item_name);
        id = (TextView) itemView.findViewById(R.id.detail_list_item_id);
        creationDate = (TextView) itemView.findViewById(R.id.detail_list_item_creation_date);
    }

    @Override
    public void onClick(View view) {
        //TODO: Implement this
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
}

