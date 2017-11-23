package android.app.printerapp;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.AttributedCharacterIterator;
import java.util.List;

/**
 * Created by SAMSUNG on 2017-11-23.
 */

public class SearchView extends ConstraintLayout {

    private Context mContext;

    //Views
    private LinearLayout leftSearchOptionsLayout;
    private LinearLayout rightSearchOptionsLayout;
    private AutoCompleteTextView searchBar;

    //Constants
    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };
    private static int optionCounter = 0;

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

    private void initializeView(){
        mContext = getContext();
        inflate(mContext, R.layout.search_layout, this);
        //Retrieve references to GUI elements
        leftSearchOptionsLayout = (LinearLayout) findViewById(R.id.left_search_options);
        rightSearchOptionsLayout = (LinearLayout) findViewById(R.id.right_search_options);
        searchBar = (AutoCompleteTextView) findViewById(R.id.search_bar);

        //Set adapter for searchbar
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        searchBar.setAdapter(adapter);

    }

    public void createSearchOption(String title, String[] options){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout searchOptionLayout = (RelativeLayout) inflater.inflate(R.layout.search_option_layout, null);
        TextView titleTextView = (TextView) searchOptionLayout.findViewById(R.id.search_option_text);
        titleTextView.setText(title);

        Spinner optionSpinner = (Spinner) searchOptionLayout.findViewById(R.id.search_option_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, options);
        optionSpinner.setAdapter(adapter);

        if(optionCounter % 2 == 0){
            leftSearchOptionsLayout.addView(searchOptionLayout);
        }else{
            rightSearchOptionsLayout.addView(searchOptionLayout);
        }
        optionCounter++;

    }








}
