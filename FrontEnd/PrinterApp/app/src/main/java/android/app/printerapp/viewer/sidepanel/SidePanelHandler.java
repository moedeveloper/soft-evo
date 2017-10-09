package android.app.printerapp.viewer.sidepanel;

import android.app.Activity;
import android.app.printerapp.Log;
import android.app.printerapp.MainActivity;
import android.app.printerapp.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.material.widget.PaperButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to initialize and handle the side panel in the print panel
 * Created by alberto-baeza on 10/24/14.
 */
public class SidePanelHandler {

    //static parameters
    private static final String[] SUPPORT_OPTIONS = {"None", "Buildplate", "Everywhere"}; //support options
    private static final String[] ADHESION_OPTIONS = {"None", "Brim", "Raft"}; //adhesion options
    private static final String[] PRINTER_TYPE = {"Witbox", "Hephestos"};
    private static final String[] PREDEFINED_PROFILES = {"bq"}; //filter for profile deletion

    private static final int DEFAULT_INFILL = 20;
    private int mCurrentInfill = DEFAULT_INFILL;

    //Printer to send the files

    //Inherited elements
    private View mRootView;
    private Activity mActivity;

    //UI elements
    private PaperButton printButton;
    private PaperButton sliceButton;
    private PaperButton saveButton;
    private PaperButton restoreButton;
    private PaperButton deleteButton;

    private Spinner s_profile;
    private Spinner s_type;
    private Spinner s_adhesion;
    private Spinner s_support;

    private RelativeLayout s_infill;
    private PopupWindow mInfillOptionsPopupWindow;
    private TextView infillText;

    public SidePanelProfileAdapter profileAdapter;

    private EditText layerHeight;
    private EditText shellThickness;
    private CheckBox enableRetraction;
    private EditText bottomTopThickness;
    private EditText printSpeed;
    private EditText printTemperature;
    private EditText filamentDiamenter;
    private EditText filamentFlow;

    private EditText travelSpeed;
    private EditText bottomLayerSpeed;
    private EditText infillSpeed;
    private EditText outerShellSpeed;
    private EditText innerShellSpeed;

    private EditText minimalLayerTime;
    private CheckBox enableCoolingFan;

    //Constructor
    public SidePanelHandler( Activity activity, View v) {

        mActivity = activity;
        mRootView = v;

        initUiElements();

    }

    //Initialize UI references
    public void initUiElements() {




        initTextWatchers();

    }

    public void initTextWatchers(){

        layerHeight.addTextChangedListener(new GenericTextWatcher("profile.layer_height"));
        shellThickness.addTextChangedListener(new GenericTextWatcher("profile.wall_thickness"));
        enableRetraction.setOnCheckedChangeListener(new GenericTextWatcher("profile.retraction_enable"));
        bottomTopThickness.addTextChangedListener(new GenericTextWatcher("profile.solid_layer_thickness"));
        printSpeed.addTextChangedListener(new GenericTextWatcher("profile.print_speed"));
        printTemperature.addTextChangedListener(new GenericTextWatcher("profile.print_temperature"));
        filamentDiamenter.addTextChangedListener(new GenericTextWatcher("profile.filament_diameter"));
        filamentFlow.addTextChangedListener(new GenericTextWatcher("profile.filament_flow"));
        travelSpeed.addTextChangedListener(new GenericTextWatcher("profile.travel_speed"));
        bottomLayerSpeed.addTextChangedListener(new GenericTextWatcher("profile.bottom_layer_speed"));
        infillSpeed.addTextChangedListener(new GenericTextWatcher("profile.infill_speed"));
        outerShellSpeed.addTextChangedListener(new GenericTextWatcher("profile.outer_shell_speed"));
        innerShellSpeed.addTextChangedListener(new GenericTextWatcher("profile.inner_shell_speed"));
        minimalLayerTime.addTextChangedListener(new GenericTextWatcher("profile.cool_min_layer_time"));
        enableCoolingFan.setOnCheckedChangeListener(new GenericTextWatcher("profile.fan_enabled"));



    }

    //Enable/disable profile options depending on the model type
    public void enableProfileSelection(boolean enable) {

        s_profile.setEnabled(enable);
        s_support.setEnabled(enable);
        s_adhesion.setEnabled(enable);
        s_infill.setEnabled(enable);

    }


    //Initializes the side panel with the printer data
    /**
    public void initSidePanel() {

        Handler handler = new Handler();

        handler.post(new Runnable() {

            @Override
            public void run() {


                try {

                    //Initialize item listeners

                    s_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            switch (i) {

                                case 0:
                                    ViewerMainFragment.changePlate(ModelProfile.WITBOX_PROFILE);
                                    break;
                                case 1:
                                    ViewerMainFragment.changePlate(ModelProfile.PRUSA_PROFILE);
                                    break;
                                default:

                                    //TODO Profiles being removed automatically

                                    try {

                                        ViewerMainFragment.changePlate(s_type.getSelectedItem().toString());

                                    } catch (NullPointerException e) {


                                    }

                                    break;

                            }


                            //mPrinter = DevicesListController.selectAvailablePrinter(i + 1, s_type.getSelectedItem().toString());
                            mSlicingHandler.setPrinter(mPrinter);

                            ViewerMainFragment.slicingCallback();


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {


                        }
                    });

                    reloadProfileAdapter();

                    //Set slicing parameters to send to the server

                    //The quality adapter is set by the printer spinner
                    s_profile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            parseJson(ModelProfile.retrieveProfile(mActivity, s_profile.getSelectedItem().toString(), ModelProfile.TYPE_Q));
                            //mSlicingHandler.setExtras("profile", s_profile.getSelectedItem().toString());

                            if (i > 2){

                                refreshProfileExtras();

                            } else {
                                reloadBasicExtras();
                                mSlicingHandler.setExtras("profile", s_profile.getSelectedItem().toString());


                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            mSlicingHandler.setExtras("profile", null);
                        }
                    });

                    reloadQualityAdapter();


                    //Adhesion type
                    s_adhesion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                            mSlicingHandler.setExtras("profile.platform_adhesion", s_adhesion.getItemAtPosition(i).toString().toLowerCase());

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            mSlicingHandler.setExtras("profile.fill_density", null);
                        }
                    });


                    //Support
                    s_support.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            mSlicingHandler.setExtras("profile.support", s_support.getItemAtPosition(i).toString().toLowerCase());

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            mSlicingHandler.setExtras("profile.support", null);
                        }
                    });


                    ArrayAdapter<String> adapter_adhesion = new ArrayAdapter<String>(mActivity,
                            R.layout.print_panel_spinner_item, ADHESION_OPTIONS);
                    adapter_adhesion.setDropDownViewResource(R.layout.print_panel_spinner_dropdown_item);
                    ArrayAdapter<String> adapter_support = new ArrayAdapter<String>(mActivity,
                            R.layout.print_panel_spinner_item, SUPPORT_OPTIONS);
                    adapter_support.setDropDownViewResource(R.layout.print_panel_spinner_dropdown_item);

                    // s_profile.setAdapter(adapter_profile);
                    s_adhesion.setAdapter(adapter_adhesion);
                    s_support.setAdapter(adapter_support);

                    infillText.setText(DEFAULT_INFILL + "%");
                    s_infill.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openInfillPopupWindow();
                        }
                    });


                    //Send a print command
                    printButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            refreshPrinters();
                            sendToPrint();
                        }
                    });

                    sliceButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ViewerMainFragment.slicingCallbackForced();
                            switchSlicingButton(false);

                        }
                    });

                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveProfile();
                        }
                    });

                    restoreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //parseJson(s_profile.getSelectedItemPosition());
                            parseJson(ModelProfile.retrieveProfile(mActivity, s_profile.getSelectedItem().toString(), ModelProfile.TYPE_Q));
                            if (s_profile.getSelectedItemPosition() <= 2) reloadBasicExtras();
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (s_profile.getSelectedItemPosition() > 2) deleteProfile(s_profile.getSelectedItem().toString());
                            else {
                                Toast.makeText(mActivity,"You can't delete this profile",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

                //String prefType = DatabaseController.getPreference(DatabaseController.TAG_PROFILE, "type");
                //String prefQuality = DatabaseController.getPreference(DatabaseController.TAG_PROFILE, "quality");
                //String prefPrinter = DatabaseController.getPreference(DatabaseController.TAG_PROFILE,"type");
                //if (prefType != null) s_type.setSelection(Integer.parseInt(prefType));
                //if (prefQuality != null) s_profile.setSelection(Integer.parseInt(prefQuality));
                //if (prefPrinter!=null) s_printer.setSelection(Integer.parseInt(prefPrinter));

                refreshPrinters();

            }
        });


    }
**/

    /**
     * Parses a JSON profile to the side panel
     *
     * @i printer index in the list
     */
    public void parseJson(JSONObject profile) {

        //Parse the JSON element
        try {


            //JSONObject data = mPrinter.getProfiles().get(i).getJSONObject("data");
            JSONObject data = profile.getJSONObject("data");
            layerHeight.setText(data.getString("layer_height"));
            shellThickness.setText(data.getString("wall_thickness"));
            bottomTopThickness.setText(data.getString("solid_layer_thickness"));
            printSpeed.setText(data.getString("print_speed"));
            printTemperature.setText(data.getJSONArray("print_temperature").get(0).toString());
            filamentDiamenter.setText(data.getJSONArray("filament_diameter").get(0).toString());
            filamentFlow.setText(data.getString("filament_flow"));
            travelSpeed.setText(data.getString("travel_speed"));
            bottomLayerSpeed.setText(data.getString("bottom_layer_speed"));
            infillSpeed.setText(data.getString("infill_speed"));
            outerShellSpeed.setText(data.getString("outer_shell_speed"));
            innerShellSpeed.setText(data.getString("inner_shell_speed"));

            minimalLayerTime.setText(data.getString("cool_min_layer_time"));

            if (data.has("retraction_enable"))
                if (data.getString("retraction_enable").equals("true")) {
                    enableRetraction.setChecked(true);
                    Log.i("OUT", "Checked true");
                } else {
                    enableRetraction.setChecked(false);
                    Log.i("OUT", "Checked false");
                }

            if (data.getBoolean("fan_enabled")) {
                enableCoolingFan.setChecked(true);
                Log.i("OUT", "Checked true");
            } else {
                enableCoolingFan.setChecked(false);
                Log.i("OUT", "Checked false");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){ //If invalid values
            e.printStackTrace();
        }

    }

    /**
     * Parse float to a variable to avoid accuracy error
     *
     * @param s
     * @return
     */
    public Float getFloatValue(String s) throws NumberFormatException {

        Float f = Float.parseFloat(s);

        return f;
    }



    /**
     * Generic text watcher to add new printing parameters
     */
    private class GenericTextWatcher implements TextWatcher, CompoundButton.OnCheckedChangeListener {

        private String mValue;

        private GenericTextWatcher(String v) {

            mValue = v;

        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            try{
                //mSlicingHandler.setExtras(mValue, getFloatValue(editable.toString()));

            } catch (NumberFormatException e){

                Log.i("Slicer", "Invalid value " + editable.toString());

            }

        }


        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            //mSlicingHandler.setExtras(mValue, b);

        }
    }



}
