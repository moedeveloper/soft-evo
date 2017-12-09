package android.app.printerapp;

import android.app.AlertDialog;
import android.app.printerapp.api.ApiService;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.DataEntry;
import android.app.printerapp.model.Machine;
import android.app.printerapp.model.NoDataSelected;
import android.app.printerapp.model.OkPacket;
import android.app.printerapp.model.Operator;
import android.app.printerapp.model.postModels.HallflowTestPost;
import android.app.printerapp.model.postModels.MeasurementPost;
import android.app.printerapp.search.SearchDrawerFragment;
import android.app.printerapp.search.SearchOptionArrayAdapter;
import android.app.printerapp.search.TestSearchView;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

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
    private LinearLayout relativeHumidityLayout;
    private LinearLayout temperatureLayout;
    private LinearLayout tapLayout;
    private LinearLayout valueMeasurementsLayout;
    private AlertDialog.Builder builder;

    //variables
    private int elementCounter = 0;
    private int amount = 0;
    private Double average = 0.0;
    private HallflowTestPost testSubmission;
    private List<MeasurementPost> measurementSubmissionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test_drawer);

        //Initialize the views
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        searchDrawerFragment = (SearchDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        searchDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);
        searchDrawerFragment.addListenerToSearchView(this);

        builder = new AlertDialog.Builder(AddTestDrawerActivity.this);

        new LoadDataTask().execute();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.END)){
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            super.onBackPressed();
        }
    }

    private void initializeAllViews(List<Operator> operators, List<Machine> machines){
        //Initialize all views

        addAttachmentsLayout = initializeAttachmentsLayout();
        operatorLayout = createSpinnerInput("Operator", operators);
        machineLayout = createSpinnerInput("Machine", machines);
        relativeHumidityLayout = createTextInput("Relative humidity", "e.g 50");
        temperatureLayout = createTextInput("Temperature", "e.g 35");
        tapLayout = createCheckBoxInput("Tap", false);
        valueMeasurementsLayout = createValueMeasurementLayout("Value measurement", "m/kg");

        //Add views to the element holder
        addTestElementHolder = (LinearLayout) findViewById(R.id.add_test_element_holder);
        addElementToElementHolder(operatorLayout);
        addElementToElementHolder(machineLayout);
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


    private LinearLayout createSpinnerInput(String title, List<? extends DataEntry> data) {
        LinearLayout spinner_input_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_spinner_layout, null);
        TextView titleView = (TextView)spinner_input_layout.findViewById(R.id.add_spinner_title);
        Spinner spinner = (Spinner)spinner_input_layout.findViewById(R.id.add_spinner_input);
        titleView.setText(title);

        List<DataEntry> dataEntries = new ArrayList<>();
        dataEntries.add(new NoDataSelected());
        dataEntries.addAll(data);

        SearchOptionArrayAdapter<? extends DataEntry> adapter = new SearchOptionArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, dataEntries);

        spinner.setAdapter(adapter);
        return spinner_input_layout;
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

    private void createErrorDialog(String errorMessage, String errorTitle){
        builder.setMessage(errorMessage)
                .setTitle(errorTitle);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitToDatabase(){
        String operator;
        String machine;
        String relativeHumidity;
        String temperature;
        String tap;
        List<String> valueMeasurements = new ArrayList<>();
        List<String> materialIds = new ArrayList<>();
        List<String> printIds = new ArrayList<>();

        DataEntry operatorEntry = (DataEntry)((Spinner) operatorLayout.
                findViewById(R.id.add_spinner_input)).getSelectedItem();
        DataEntry machineEntry = (DataEntry)((Spinner) machineLayout.
                findViewById(R.id.add_spinner_input)).getSelectedItem();

        operator = operatorEntry.getId();
        machine = machineEntry.getId();

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
            String materialIdName = ((TextView) materialAttachmentHolder.
                    findViewById(R.id.attachment_material_id)).getText().toString();
            materialIdName = materialIdName.replaceAll("[^0-9]", "");
            materialIds.add(materialIdName);
        }

        if(operator.isEmpty() || machine.isEmpty() || relativeHumidity.isEmpty() || temperature.isEmpty()
                              || tap.isEmpty() || materialIds.isEmpty()){
            String errorMessage = "All fields must be filled in order to store the test.";
            String errorTitle = "Error: Empty field(s)";
            createErrorDialog(errorMessage, errorTitle);
            return;
        }

        measurementSubmissionList = new ArrayList<>();
        for(String current : valueMeasurements){
            MeasurementPost newMeasurement = new MeasurementPost();
            newMeasurement.setMeasurementValue(current);
            measurementSubmissionList.add(newMeasurement);
        }

        testSubmission = new HallflowTestPost();
        testSubmission.setOperatorId(operator);
        testSubmission.setMaterialId(materialIds.get(0));
        testSubmission.setRelativehumidity(relativeHumidity);
        testSubmission.setTemperature(temperature);
        testSubmission.setTap(tap);
        testSubmission.setMachineId(machine);

        new PostTestData().execute();

    }

    private class LoadDataTask extends AsyncTask<Void, Void,Void> {
        private ApiService apiService = DatabaseHandler.getInstance().getApiService();
        private List<Operator> operators;
        private List<Machine> machines;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                operators = apiService.fetchAllOperators().execute().body().getOperators();
                machines = apiService.fetchAllMachines().execute().body().getMachinesApi();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initializeAllViews(operators, machines);
        }
    }

    private class PostTestData extends AsyncTask<Void, Void, Void> {
        private ApiService apiService = DatabaseHandler.getInstance().getApiService();
        private String errorMessage;
        private String errorTitle;
        private boolean submissionSuccess = true;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(testSubmission != null) {
                    Response<OkPacket> response = apiService.createHallflowTest(testSubmission).execute();
                    String id = response.body().getInsertId();
                    if (response.isSuccessful()) {
                        for (MeasurementPost current : measurementSubmissionList) {
                            current.setHallflowTestId(id);
                            Response<OkPacket> mResponse = apiService.createMeasurement(current).execute();
                            if(!mResponse.isSuccessful()){
                                //TODO: Find some way to store data and try posting again at a later time
                                Log.d("Failed", "");
                                submissionSuccess = false;
                                errorMessage = "Failed to submit measurements to server.";
                                errorTitle = "Connection error";
                                return null;
                            } else {
                                submissionSuccess = true;
                            }
                        }
                    } else {
                        submissionSuccess = false;
                        errorMessage = "Failed to submit tests to server.";
                        errorTitle = "Connection error";
                    }
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(submissionSuccess){
                finish();
            }else{
                createErrorDialog(errorMessage, errorTitle);
            }
        }
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
