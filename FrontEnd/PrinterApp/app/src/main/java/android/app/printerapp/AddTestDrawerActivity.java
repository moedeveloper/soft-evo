package android.app.printerapp;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class AddTestDrawerActivity extends ActionBarActivity
        implements SearchDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private SearchDrawerFragment searchDrawerFragment;
    private static DrawerLayout drawerLayout;

    private LinearLayout addTestElementHolder;

    private static int elementCounter = 0;
    private static Double average = 0.0;
    private static int amount = 0;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        searchDrawerFragment = (SearchDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        searchDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout);

        //Initialize the view
        LinearLayout date_layout = createTextInput("Date", "2017-11-29");
        EditText date_input = (EditText) date_layout.findViewById(R.id.add_text_input);
        setupDateTimePicker(date_input);

        //Initialize the attachment layout
        ConstraintLayout addAttachmentsLayout = (ConstraintLayout) getLayoutInflater().
                inflate(R.layout.add_test_attachments_layout, null);
        final LinearLayout addPrintAttachmentLayout = (LinearLayout)
                addAttachmentsLayout.findViewById(R.id.add_test_prints_attachments_layout);
        final LinearLayout addMaterialAttachmentLayout = (LinearLayout)
                addAttachmentsLayout.findViewById(R.id.add_test_materials_attachments_layout);
        Button attachPrintButton = (Button) addAttachmentsLayout.
                findViewById(R.id.add_test_add_print_attachment_button);
        Button attachMaterialButton = (Button) addAttachmentsLayout.
                findViewById(R.id.add_test_add_material_attachment_button);

        attachPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //The data here should be given from the selection from the search
                //fragment.
                openDrawer();
                searchDrawerFragment.loadData(SearchDrawerFragment.DATATYPE_PRINT);

            }
        });

        attachMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //The data here should be given from the selection from the search
                //fragment.
                openDrawer();
                searchDrawerFragment.loadData(SearchDrawerFragment.DATATYPE_MATERIAL);            }
        });

        addTestElementHolder = (LinearLayout) findViewById(R.id.add_test_element_holder);
        addElementToElementHolder(createTextInput("Operator", "Aritstotle Svensson"));
        addElementToElementHolder(createTextInput("Machine", "M1548"));
        addElementToElementHolder(date_layout);
        addElementToElementHolder(createTextInput("Relative humidity", "50%"));
        addElementToElementHolder(createTextInput("Temperature", "57 C"));
        addElementToElementHolder(createCheckBoxInput("Tap", false));
        addElementToElementHolder(createValueMeasurementLayout("Value measurement", "m/kg"));

        //These things should be added last
        addElementToElementHolder(addAttachmentsLayout);
        addElementToElementHolder(createFinalizeButtons());

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public static void openDrawer(){
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

//--------------------------------------------------------------------------
//          FACTORY METHODS
//--------------------------------------------------------------------------

    private RelativeLayout createPrintAttachment(String id, String operator, String creationDate) {
        final RelativeLayout print_attachment_layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.tests_attachment_print, null);

        TextView idView = (TextView) print_attachment_layout.findViewById(R.id.attachment_print_id);
        TextView operatorView = (TextView) print_attachment_layout.findViewById(R.id.attachment_print_operator);
        TextView startDateView = (TextView) print_attachment_layout.findViewById(R.id.attachment_print_start_date);
        Button removePrintAttachmentButton = (Button) print_attachment_layout.findViewById(R.id.attachment_remove_print_button);

        idView.setText(id);
        operatorView.setText(operator);
        startDateView.setText(creationDate);

        removePrintAttachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup parent = (ViewGroup) print_attachment_layout.getParent();

                if(parent == null){
                    return;
                }

                if(parent.getChildCount() < 1){
                    return;
                }

                parent.removeView(print_attachment_layout);


            }
        });

        return print_attachment_layout;
    }

    private RelativeLayout createPrintAttachment() {
        return (RelativeLayout) getLayoutInflater().inflate(R.layout.tests_attachment_print, null);
    }

    private RelativeLayout createMaterialAttachment() {
        return (RelativeLayout) getLayoutInflater().inflate(R.layout.tests_attachment_material, null);
    }

    private RelativeLayout createMaterialAttachment(String id, String startDate) {
        final RelativeLayout material_attachment_layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.tests_attachment_material, null);

        TextView idView = (TextView) material_attachment_layout.findViewById(R.id.attachment_material_id);
        TextView startDateView = (TextView) material_attachment_layout.findViewById(R.id.attachment_material_creation_date);
        Button removeMaterialAttachmentButton = (Button) material_attachment_layout.findViewById(R.id.attachment_remove_material_button);

        idView.setText(id);
        startDateView.setText(startDate);

        removeMaterialAttachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup parent = (ViewGroup) material_attachment_layout.getParent();

                if(parent == null){
                    return;
                }

                if(parent.getChildCount() < 1){
                    return;
                }

                parent.removeView(material_attachment_layout);
            }
        });

        return material_attachment_layout;
    }

    private LinearLayout createTextInput(String title, String inputHint){
        LinearLayout text_input_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_text_layout, null);
        TextView titleView = (TextView)text_input_layout.findViewById(R.id.add_text_title);
        EditText inputView = (EditText)text_input_layout.findViewById(R.id.add_text_input);
        titleView.setText(title);
        inputView.setHint(inputHint);
        return text_input_layout;
    }

    private LinearLayout createFinalizeButtons(){
        LinearLayout buttons_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_test_buttons_layout, null);
        Button confirm = (Button) buttons_layout.findViewById(R.id.confirm_button);
        Button cancel = (Button) buttons_layout.findViewById(R.id.cancel_button);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save to database
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        return buttons_layout;
    }

    private LinearLayout createTextInput(String title, String inputHint, String metric){
        LinearLayout text_input_layout = createTextInput(title, inputHint);
        EditText inputView = (EditText) text_input_layout.findViewById(R.id.add_text_input);
        //Somehow add metric to the end of the string
        return text_input_layout;
    }

    private LinearLayout createCheckBoxInput(String title, Boolean checked){
        LinearLayout checkbox_input_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_checkbox_layout, null);
        TextView titleView = (TextView) checkbox_input_layout.findViewById(R.id.add_checkbox_title);
        titleView.setText(title);
        CheckBox checkBox = (CheckBox) checkbox_input_layout.findViewById(R.id.add_checkbox_input);
        checkBox.setChecked(checked);
        return checkbox_input_layout;
    }

    private LinearLayout createValueMeasurementLayout(String title, final String metric){
        final LinearLayout value_meas_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_measurement_layout, null);
        TextView titleView = (TextView) value_meas_layout.findViewById(R.id.value_measurement_title);
        final TextView averageTextView = (TextView) value_meas_layout.findViewById(R.id.value_measurement_average_text_view);

        final LinearLayout value_meas_input_layout =
                (LinearLayout) value_meas_layout.findViewById(R.id.value_measurement_values_layout);

        titleView.setText(title);

        Button add_meas_button = (Button) value_meas_layout.findViewById(R.id.value_measurement_add_button);
        add_meas_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText valueInput = (EditText) getLayoutInflater().inflate(R.layout.add_measurement_edit_text,null);
                valueInput.setHint(metric);
                amount++;
                averageTextView.setText(getMeasurementAverageString(metric));
                valueInput.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(!charSequence.toString().equals("")){
                            average -= Double.parseDouble(charSequence.toString());
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(!charSequence.toString().equals("")) {
                            average += Double.parseDouble(charSequence.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        averageTextView.setText(getMeasurementAverageString(metric));
                    }
                });
                value_meas_input_layout.addView(valueInput);
            }
        });

        Button remove_meas_button = (Button) value_meas_layout.findViewById(R.id.value_measurement_remove_button);
        remove_meas_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(value_meas_input_layout.getChildCount() > 0){
                    amount--;
                    EditText lastEditText = (EditText) value_meas_input_layout.getChildAt(
                            value_meas_input_layout.getChildCount()-1);
                    if(!lastEditText.getText().toString().equals("")){
                        average -= Double.parseDouble(lastEditText.getText().toString());
                    }
                    ((ViewGroup) lastEditText.getParent()).removeView(lastEditText);
                    averageTextView.setText(getMeasurementAverageString(metric));
                }
            }
        });

        return value_meas_layout;
    }

    //--------------------------------------------------------------------------
//          HELPER METHODS
//--------------------------------------------------------------------------
    private String getMeasurementAverageString(String metric){
        return (new Double(average/amount)).toString() + " " + metric;
    }

    private void addElementToElementHolder(View view){
        if(elementCounter % 2 == 0){
            view.setBackgroundColor(getResources().getColor(R.color.white));
        }else{
            view.setBackgroundColor(getResources().getColor(R.color.pager_background));
        }
        addTestElementHolder.addView(view);
        elementCounter++;
    };


    private void setupDateTimePicker(final EditText displayDate) {
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month =month+1;
                Log.d(TAG, "onDateSet: mm/dd/yy" + year + "/" + month + "/" + day);
                String date = String.format("%d-%d-%d 00:00:00", year, month, day);
                displayDate.setText(date);
            }
        };

        displayDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar currentCalendar = Calendar.getInstance();

                int year = currentCalendar.get(Calendar.YEAR);
                int month = currentCalendar.get(Calendar.MONTH);
                int day = currentCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddTestDrawerActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        dateSetListener,
                        year,
                        month,
                        day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

    }


}
