package android.app.printerapp;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class AddTestActivity extends ActionBarActivity {

    LinearLayout addTestElementHolder;
    private static int elementCounter = 0;
    private static Double average = 0.0;
    private static int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        LinearLayout date_layout = createTextInput("Date", "2017-11-29");
        EditText date_input = (EditText) date_layout.findViewById(R.id.add_text_input);
        setupDateTimePicker(date_input);

        addTestElementHolder = (LinearLayout) findViewById(R.id.add_test_element_holder);
        addElement(createTextInput("Operator", "Aritstotle Svensson"));
        addElement(createTextInput("Machine", "M1548"));
        addElement(date_layout);
        addElement(createTextInput("Relative humidity", "50%"));
        addElement(createTextInput("Temperature", "57 C"));
        addElement(createCheckBoxInput("Tap", false));
        addElement(createValueMeasurementLayout("Value measurement", "m/kg"));
        addElement(createFinalizeButtons());
    }

//--------------------------------------------------------------------------
//          FACTORY METHODS
//--------------------------------------------------------------------------

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

    private void addElement(View view){
        if(elementCounter % 2 == 0){
            view.setBackgroundColor(getResources().getColor(R.color.white));
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
                        AddTestActivity.this,
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
