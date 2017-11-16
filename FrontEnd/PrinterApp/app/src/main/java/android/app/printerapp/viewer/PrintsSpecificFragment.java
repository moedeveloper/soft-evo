package android.app.printerapp.viewer;

import android.app.printerapp.R;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


public class PrintsSpecificFragment extends STLViewerFragment {

    private Context mContext;
    private View mRootView;

//    Views
    private ListView dataListView;


    //Empty constructor
    public PrintsSpecificFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retain instance to keep the Fragment from destroying itself
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Let parent initialize all STL Viewer elements
        super.onCreateView(inflater, container, savedInstanceState);

        //Reference to View
        mRootView = null;

        //If is not new
        if (savedInstanceState == null) {

            //Get the rootview from its parent
            mRootView = getRootView();
            mContext = getActivity();

            dataListView = (ListView) mRootView.findViewById(R.id.prints_data_titles_list_view);
            dataListView.setAdapter(new DataTextAdapter(mContext));

        }

//      Placeholder buttons for testing
        Button addModelButton = (Button) mRootView.findViewById(R.id.print_middle_button);
        addModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hard coding
                String path = "/storage/emulated/0/PrintManager/Files/Feather/_stl/Feather.stl";
                openFileDialog(path);
            }
        });

        Button clearViewerButton = (Button) mRootView.findViewById(R.id.print_right_button);
        clearViewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hard coding
                optionClean();
            }
        });

        Button addTextButton = (Button) mRootView.findViewById(R.id.print_left_button);
        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hard coding
            }
        });


        return mRootView;

    }
}
