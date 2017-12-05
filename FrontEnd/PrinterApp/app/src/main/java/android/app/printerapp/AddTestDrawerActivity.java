package android.app.printerapp;

import android.app.DatePickerDialog;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.search.SearchDrawerFragment;
import android.app.printerapp.search.TestSearchView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class AddTestDrawerActivity extends ActionBarActivity
        implements PropertyChangeListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    //Drawer fragment
    private SearchDrawerFragment searchDrawerFragment;

    //Views
    private static DrawerLayout drawerLayout;
    private ConstraintLayout addAttachmentsLayout;
    private LinearLayout addPrintAttachmentLayout;
    private LinearLayout addMaterialAttachmentLayout;
    private LinearLayout addTestElementHolder;
    private LinearLayout operatorLayout;
    private LinearLayout machineLayout;
    private LinearLayout dateLayout;
    private LinearLayout relativeHumidityLayout;
    private LinearLayout temperatureLayout;
    private LinearLayout tapLayout;
    private LinearLayout valueMeasurementsLayout;

    //Static variables
    private static int elementCounter = 0;
    private static int amount = 0;
    private static Double average = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test_drawer);

        //Initialize the views
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        searchDrawerFragment = (SearchDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        dateLayout = createTextInput("Date", "2017-11-29");
        EditText date_input = (EditText) dateLayout.findViewById(R.id.add_text_input);
        setupDateTimePicker(date_input);

        searchDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);
        searchDrawerFragment.addListenerToSearchView(this);

        //Initialize all views
        addAttachmentsLayout = initializeAttachmentsLayout();
        operatorLayout = createTextInput("Operator", "Aritstotle Svensson");
        machineLayout = createTextInput("Machine", "M1548");
        relativeHumidityLayout = createTextInput("Relative humidity", "50%");
        temperatureLayout = createTextInput("Temperature", "57 C");
        tapLayout = createCheckBoxInput("Tap", false);
        valueMeasurementsLayout = createValueMeasurementLayout("Value measurement", "m/kg");

        //Add views to the element holder
        addTestElementHolder = (LinearLayout) findViewById(R.id.add_test_element_holder);
        addElementToElementHolder(operatorLayout);
        addElementToElementHolder(machineLayout);
        addElementToElementHolder(dateLayout);
        addElementToElementHolder(relativeHumidityLayout);
        addElementToElementHolder(temperatureLayout);
        addElementToElementHolder(tapLayout);
        addElementToElementHolder(valueMeasurementsLayout);

        //These things should be added last
        addElementToElementHolder(addAttachmentsLayout);
        addElementToElementHolder(createFinalizeButtons());
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
                submitToDatabase();
                //close;
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

    private ConstraintLayout initializeAttachmentsLayout(){
        ConstraintLayout addAttachmentsLayout = (ConstraintLayout) getLayoutInflater().
                inflate(R.layout.add_test_attachments_layout, null);
        addPrintAttachmentLayout = (LinearLayout)
                addAttachmentsLayout.findViewById(R.id.add_test_prints_attachments_layout);
        addMaterialAttachmentLayout = (LinearLayout)
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
        return addAttachmentsLayout;
    }

//--------------------------------------------------------------------------
//          HELPER METHODS
//--------------------------------------------------------------------------

    private void submitToDatabase(){
        String operator;
        String machine;
        String date;
        String relativeHumidity;
        String temperature;
        String tap;
        List<String> valueMeasurements = new ArrayList<>();
        List<String> materialIds = new ArrayList<>();
        List<String> printIds = new ArrayList<>();

        operator = ((EditText) operatorLayout.
                findViewById(R.id.add_text_input)).getText().toString();
        machine = ((EditText) machineLayout.
                findViewById(R.id.add_text_input)).getText().toString();
        date = ((EditText) dateLayout.
                findViewById(R.id.add_text_input)).getText().toString();
        relativeHumidity = ((EditText) relativeHumidityLayout.
                findViewById(R.id.add_text_input)).getText().toString();
        temperature = ((EditText) temperatureLayout.
                findViewById(R.id.add_text_input)).getText().toString();
        tap = String.valueOf(((CheckBox) tapLayout.
                findViewById(R.id.add_checkbox_input)).isChecked());

        //Store all value measurements
        LinearLayout valueMeasurementsInputs = (LinearLayout) valueMeasurementsLayout.
                findViewById(R.id.value_measurement_values_layout);
        for(int i = 0; i < valueMeasurementsInputs.getChildCount(); i++){
            valueMeasurements.add(
                    ((EditText) valueMeasurementsInputs.getChildAt(i)).getText().toString());
        }

        //Store all attached prints
        LinearLayout printAttachments = (LinearLayout) addAttachmentsLayout.
                findViewById(R.id.add_test_prints_attachments_layout);
        for(int i = 2; i < printAttachments.getChildCount(); i++){
            RelativeLayout printAttachmentHolder = (RelativeLayout) printAttachments.getChildAt(i);
            printIds.add(((TextView) printAttachmentHolder.
                    findViewById(R.id.attachment_print_id)).getText().toString());
        }

        //Store all attached materials
        LinearLayout materialAttachments = (LinearLayout) addAttachmentsLayout.
                findViewById(R.id.add_test_materials_attachments_layout);
        for(int i = 2; i < materialAttachments.getChildCount(); i++){
            RelativeLayout materialAttachmentHolder = (RelativeLayout) materialAttachments.getChildAt(i);
            materialIds.add(((TextView) materialAttachmentHolder.
                    findViewById(R.id.attachment_material_id)).getText().toString());
        }

        //Perform POST


    }

    public static void openDrawer(){
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

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

//--------------------------------------------------------------------------
//          IMPLEMENTED METHODS
//--------------------------------------------------------------------------
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(TestSearchView.CONFIRM_ATTACHMENT_PRINT)) {

            drawerLayout.closeDrawer(Gravity.END);

            List<DataEntry> data = (List<DataEntry>) e.getNewValue();
            for (DataEntry current : data) {

                addPrintAttachmentLayout.addView(
                        createPrintAttachment(current.getIdName(),
                                "Ivar Vidfamne", current.getCreationDate()));

            }


        }else if(e.getPropertyName().equals(TestSearchView.CONFIRM_ATTACHMENT_MATERIAL)){
            drawerLayout.closeDrawer(Gravity.END);

            List<DataEntry> data = (List<DataEntry>) e.getNewValue();
            for (DataEntry current : data) {

                addMaterialAttachmentLayout.addView(
                        createMaterialAttachment(current.getIdName(), current.getCreationDate()));

            }

        }else if(e.getPropertyName().equals(TestSearchView.CANCEL_ATTACHMENT)){
            drawerLayout.closeDrawer(Gravity.END);
        }
    }
}
