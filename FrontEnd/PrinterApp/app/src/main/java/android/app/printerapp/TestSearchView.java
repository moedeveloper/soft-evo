package android.app.printerapp;

import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.Material;
import android.app.printerapp.model.Print;
import android.app.printerapp.ui.DataEntryArrayAdapter;
import android.app.printerapp.ui.DataEntryRecyclerViewAdapter;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAMSUNG on 2017-11-23.
 * In order to make the outer view (most likely a fragment such as PrintsFragment) update itself
 * after search, it must listen to this, by implementing PropertyChangeListener.
 * Everytime the new view updates its data, it must also call the method updateData() on this
 * class. This updates the AutoCompleteTextView.
 */

public class TestSearchView extends ConstraintLayout {

//---------------------------------------------------------------------------------------
//          VARIABLES
//---------------------------------------------------------------------------------------

    //Class variables
    private Context mContext;
    private List<? extends DataEntry> data;

    //Views
    private LinearLayout leftSearchOptionsLayout;
    private LinearLayout rightSearchOptionsLayout;
    private AutoCompleteTextView searchBar;
    private Button goButton;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ListView listView;
    private DataEntryArrayAdapter<DataEntry> currentAdapter;
    private Button confirmButton;
    private Button cancelButton;

    //Constants
    public static final String GO_BUTTON_CLICKED = "onclick";
    public static final String CONFIRM_ATTACHMENT_PRINT = "confirm-p";
    public static final String CONFIRM_ATTACHMENT_MATERIAL = "confirm-m";
    public static final String CANCEL_ATTACHMENT = "cancel";
    public static final String PRINT_DATATYPE = "data-p";
    public static final String MATERIAL_DATATYPE = "data-m";


    //Variables
    private static int optionCounter = 0;
    private String attachmentDataType = "";

//---------------------------------------------------------------------------------------
//          OVERRIDES
//---------------------------------------------------------------------------------------

    public TestSearchView(Context context) {
        super(context);
        initializeView();
    }

    public TestSearchView(Context context, AttributeSet attrs){
        super(context, attrs);
        initializeView();
    }

    public TestSearchView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initializeView();
    }

//---------------------------------------------------------------------------------------
//          SETUP METHODS
//---------------------------------------------------------------------------------------

    private void initializeView(){
        //Basic setup
        mContext = getContext();
        inflate(mContext, R.layout.test_search_layout, this);

        //Retrieve references to GUI elements
        leftSearchOptionsLayout = (LinearLayout) findViewById(R.id.search_options_layout_left);
        rightSearchOptionsLayout = (LinearLayout) findViewById(R.id.search_options_layout_right);
        searchBar = (AutoCompleteTextView) findViewById(R.id.search_bar_test);
        goButton = (Button) findViewById(R.id.test_search_go_button);
        listView = (ListView) findViewById(R.id.test_search_list_view);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        //Setup all buttons in the search view
        confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                List<DataEntry> checkedData = new ArrayList<>();

                if(currentAdapter != null){
                    List<Boolean> checked = (currentAdapter.getCheckBoxState());
                    //Adds all checked dataentries to checkedData
                    for(int i = 0; i < listView.getAdapter().getCount(); i++){
                        if(checked.get(i).equals(Boolean.TRUE)) {
                            checkedData.add((DataEntry) listView.getAdapter().getItem(i));
                        }
                    }
                    //Clears all checks at the end
                    currentAdapter.clearChecked();
                    listView.clearChoices();
                    listView.requestLayout();
                }

                //Sends the checked data to a listener to allow them to handle it
                if(attachmentDataType.equals(PRINT_DATATYPE)){
                    pcs.firePropertyChange(CONFIRM_ATTACHMENT_PRINT, null, checkedData);
                } else if (attachmentDataType.equals(MATERIAL_DATATYPE)){
                    pcs.firePropertyChange(CONFIRM_ATTACHMENT_MATERIAL, null, checkedData);
                }
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentAdapter != null) {
                    //Clears all checks
                    currentAdapter.clearChecked();
                    listView.clearChoices();
                    listView.requestLayout();
                }
                pcs.firePropertyChange(CANCEL_ATTACHMENT, false, true);
            }
        });

        //Setup the onclick listener for the Go button
        goButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data != null) {
                    pcs.firePropertyChange(GO_BUTTON_CLICKED, null, filterList(data, searchBar.getText().toString()));
                    searchBar.setText("");
                }
            }
        });
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        pcs.removePropertyChangeListener(listener);
    }

    public void createSearchOption(String title, String[] options){
        //Inflate the search_option_layout
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout searchOptionLayout = (RelativeLayout) inflater.inflate(R.layout.search_option_layout, null);

        //Retrieve references to views
        TextView titleTextView = (TextView) searchOptionLayout.findViewById(R.id.search_option_text);
        titleTextView.setText(title);
        Spinner optionSpinner = (Spinner) searchOptionLayout.findViewById(R.id.search_option_spinner);

        //Set the adapter of the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, options);
        optionSpinner.setAdapter(adapter);

        //Places uneven views in the left layout and even views on the right
        if(optionCounter % 2 == 0){
            leftSearchOptionsLayout.addView(searchOptionLayout);
        }else{
            rightSearchOptionsLayout.addView(searchOptionLayout);
        }
        optionCounter++;

    }

//---------------------------------------------------------------------------------------
//          METHODS TO CONTROL THE CLASS
//---------------------------------------------------------------------------------------

    public void updateData(List<DataEntry> data){

        if(data.size() < 1){
            return;
        }

        //Determine the datatype for the updated data
        DataEntry sampleData = data.get(0);
        if(sampleData.getClass() == Print.class){
            attachmentDataType = PRINT_DATATYPE;
        } else if (sampleData.getClass() == Material.class){
            attachmentDataType = MATERIAL_DATATYPE;
        }

        List<String> dataIds = new ArrayList<>();

        //Save all print ids in a list. Add prefix to define that it's a print
        for(DataEntry current : data){
            dataIds.add(current.getIdName());
        }

        //Update the adapter (Auto-complete)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_dropdown_item_1line, dataIds);
        searchBar.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //Update the dataentry adapter (Used in search)
        currentAdapter = new DataEntryArrayAdapter<DataEntry>(getContext(),
                android.R.layout.simple_list_item_multiple_choice, data);

        listView.setAdapter(currentAdapter);

        //Update our data
        this.data = data;
    }

    //Filters a list of DataEntry, by ID
    private List<DataEntry> filterList(List<? extends DataEntry> list, String filter){
        List<DataEntry> returnList = new ArrayList<>();
        for(DataEntry current : list){
            if(current.getIdName().toLowerCase().contains(filter.toLowerCase())){
                returnList.add(current);
            }
        }
        return returnList;
    }
}
