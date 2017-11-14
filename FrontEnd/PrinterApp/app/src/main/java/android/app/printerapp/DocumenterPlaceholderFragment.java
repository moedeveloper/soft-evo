package android.app.printerapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by SAMSUNG on 2017-11-09.
 */

public class DocumenterPlaceholderFragment extends Fragment {

    View mRootView;

    public DocumenterPlaceholderFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        mRootView = null;

        if(savedInstanceState == null) {
            mRootView = inflater.inflate(R.layout.documenter_placeholder,
                    container, false);

            Button addButton = (Button) mRootView.findViewById(R.id.button_add_details);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), add_details.class);
                    startActivityForResult(intent, 0);
                }
            });

            Button listDetailsButton = (Button) mRootView.findViewById(R.id.button_list_details);
            listDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetailsListActivity.class);
                    startActivity(intent);
                }
            });
        }

        return mRootView;
    }

}
