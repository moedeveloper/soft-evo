package android.app.printerapp.ui.DateEntryItemHolders;

import android.app.printerapp.R;
import android.app.printerapp.SpecificActivity;
import android.app.printerapp.dataviews.BuildSpecificFragment;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), SpecificActivity.class);
        intent.putExtra(SpecificActivity.CHOSEN_FRAGMENT_INTENT_TAG, getFragmentType());
        intent.putExtra(SpecificActivity.ID, getId());
        view.getContext().startActivity(intent);
    }

    public abstract int getFragmentType();
}

