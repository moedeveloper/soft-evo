package android.app.printerapp.search;

import android.app.printerapp.R;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.Detail;
import android.app.printerapp.model.Operator;
import android.app.printerapp.model.Print;
import android.content.Context;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SAMSUNG on 2017-11-23.
 * In order to make the outer view (most likely a fragment such as PrintsFragment) update itself
 * after search, it must listen to this, by implementing PropertyChangeListener.
 * Everytime the new view updates its data, it must also call the method updateData() on this
 * class. This updates the AutoCompleteTextView.
 */

public class SearchView extends ConstraintLayout {

//---------------------------------------------------------------------------------------
//          VARIABLES
//---------------------------------------------------------------------------------------

    //Class variables
    private Context mContext;
    private List<? extends DataEntry> data;
    private Map<String, List<? extends DataEntry>> optionLists = new HashMap<>();
    private DataEntry selectedOperator;
    private DataEntry selectedCompany;

    //Views
    private LinearLayout leftSearchOptionsLayout;
    private LinearLayout rightSearchOptionsLayout;
    private AutoCompleteTextView searchBar;
    private Button goButton;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    //Constants
    public static final String GO_BUTTON_CLICKED = "ONCLICK";

    //Counter
    private int optionCounter = 0;

//---------------------------------------------------------------------------------------
//          OVERRIDES
//---------------------------------------------------------------------------------------

    public SearchView(Context context) {
        super(context);
        initializeView();
    }

    public SearchView(Context context, AttributeSet attrs){
        super(context, attrs);
        initializeView();
    }

    public SearchView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initializeView();
    }

//---------------------------------------------------------------------------------------
//          SETUP METHODS
//---------------------------------------------------------------------------------------

    private void initializeView(){
        //Basic setup
        mContext = getContext();
        inflate(mContext, R.layout.search_layout, this);

        //Retrieve references to GUI elements
        leftSearchOptionsLayout = (LinearLayout) findViewById(R.id.left_search_options);
        rightSearchOptionsLayout = (LinearLayout) findViewById(R.id.right_search_options);
        searchBar = (AutoCompleteTextView) findViewById(R.id.search_bar);
        goButton = (Button) findViewById(R.id.search_go_button);

        //Setup the onclick listener for the Go button
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOperator != null) {
                    new LoadPrintsByOperator().execute();
                } else if (selectedCompany != null) {
                    new LoadDetailsByCompany().execute();
                } else {
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

    public void createSearchOptionSelection(final String title, List<? extends DataEntry> options){
        if(options == null){
            return;
        }
        optionLists.put(title, options);

        //Inflate the search_option_selection_layout
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout searchOptionLayout = (RelativeLayout) inflater.inflate(R.layout.search_option_selection_layout, null);

        //Retrieve references to views
        TextView titleTextView = (TextView) searchOptionLayout.findViewById(R.id.search_option_text);
        titleTextView.setText(title);
        Spinner optionSpinner = (Spinner) searchOptionLayout.findViewById(R.id.search_option_spinner);

        //Get all company names and put them in array
        String[] dataNames = new String[options.size() + 1];
        Arrays.fill(dataNames, "");
        dataNames[0] = "No option selected";
        for(int i = 1; i < options.size(); i++){
            //TODO: Name is not unique? Need to fix in database
            if(options.get(i).getName() != null) {
                dataNames[i] = options.get(i).getName();
            }
        }

        //Set the adapter of the spinner
        SearchOptionArrayAdapter<? extends DataEntry> adapter = new SearchOptionArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, options);
        optionSpinner.setAdapter(adapter);
        optionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(title.equals("Operator")){
                    selectedOperator = (DataEntry) adapterView.getAdapter().getItem(i);
                }else if (title.equals("Company")){
                    selectedCompany = (DataEntry) adapterView.getAdapter().getItem(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedOperator = null;
            }
        });


        addOptionToLayout(searchOptionLayout);

    }

    public void createSearchOptionTextInput(String title, String hint){
        //Inflate the search_option_selection_layout
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout searchOptionLayout = (RelativeLayout) inflater.inflate(R.layout.search_option_text_input_layout, null);

        //Retrieve references to views
        TextView titleTextView = (TextView) searchOptionLayout.findViewById(R.id.search_option_text);
        titleTextView.setText(title);
        EditText textInput= (EditText) searchOptionLayout.findViewById(R.id.search_option_text_input);
        textInput.setHint(hint);

        addOptionToLayout(searchOptionLayout);

    }

    private void addOptionToLayout(RelativeLayout searchOptionLayout){
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

    public void updateData(List<? extends DataEntry> data){

        List<String> dataIds = new ArrayList<>();

        //Save all print ids in a list. Add prefix to define that it's a print
        for(DataEntry current : data){
            dataIds.add(current.getIdName());
        }

        //Update teh adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_dropdown_item_1line, dataIds);
        searchBar.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

    private class LoadDetailsByCompany extends AsyncTask<Void,Void,Void>{
        List<Detail> details = null;

        @Override
        protected Void doInBackground(Void... voids) {
            ApiService apiService = DatabaseHandler.getInstance().getApiService();
            try {
                details = apiService.fetchDetailByCompany(Integer.parseInt(selectedCompany.getId())).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            List<? extends DataEntry> dataToDisplay = data;
            if(details != null){
                dataToDisplay = details;
            }
            pcs.firePropertyChange(GO_BUTTON_CLICKED, null, filterList(dataToDisplay, searchBar.getText().toString()));
            searchBar.setText("");
        }
    }

    private class LoadPrintsByOperator extends AsyncTask<Void,Void,Void> {

        List<Print> prints = null;

        @Override
        protected Void doInBackground(Void... voids) {
            ApiService apiService = DatabaseHandler.getInstance().getApiService();
            try {
                prints = apiService.fetchPrintFromOperator(Integer.parseInt(selectedOperator.getId())).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            List<? extends DataEntry> dataToDisplay = data;
            if(prints != null){
                dataToDisplay = prints;
            }
            pcs.firePropertyChange(GO_BUTTON_CLICKED, null, filterList(dataToDisplay, searchBar.getText().toString()));
            searchBar.setText("");
        }
    }

}
