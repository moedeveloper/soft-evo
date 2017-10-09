package android.app.printerapp.viewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.printerapp.Log;
import android.app.printerapp.MainActivity;
import android.app.printerapp.R;
import android.app.printerapp.viewer.sidepanel.SidePanelHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devsmart.android.ui.HorizontalListView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ViewerMainFragment extends Fragment {
    //Tabs
    private static final int NORMAL = 0;

    private static int mCurrentViewMode = 0;

    //Constants
    public static final int DO_SNAPSHOT = 0;
    public static final int DONT_SNAPSHOT = 1;
    public static final int PRINT_PREVIEW = 3;

    private static final int MENU_HIDE_OFFSET_SMALL = 20;
    private static final int MENU_HIDE_OFFSET_BIG = 1000;

    //Variables
    private static File mFile;

    private static ViewerSurfaceView mSurface;
    private static FrameLayout mLayout;


    //Buttons
    private static ImageButton mVisibilityModeButton;

    private static SeekBar mSeekBar;
    private boolean isKeyboardShown = false;

    private static List<DataStorage> mDataList = new ArrayList<DataStorage>();

    //Undo button bar
    private static LinearLayout mUndoButtonBar;

    //Edition menu variables
    private static ProgressBar mProgress;

    private static Context mContext;
    private static View mRootView;

    private static LinearLayout mStatusBottomBar;
    private static FrameLayout mBottomBar;
    private static LinearLayout mRotationLayout;
    private static LinearLayout mScaleLayout;
    private static ImageView mActionImage;

    private static EditText mScaleEditX;
    private static EditText mScaleEditY;
    private static EditText mScaleEditZ;
    private static ImageButton mUniformScale;

    private static ScaleChangeListener mTextWatcherX;
    private static ScaleChangeListener mTextWatcherY;
    private static ScaleChangeListener mTextWatcherZ;

    /**
     * ****************************************************************************
     */
    private static SlicingHandler mSlicingHandler;
    private static SidePanelHandler mSidePanelHandler;

    private static int mCurrentType = WitboxFaces.TYPE_WITBOX;
    ;
    private static int[] mCurrentPlate = new int[]{WitboxFaces.WITBOX_LONG, WitboxFaces.WITBOX_WITDH, WitboxFaces.WITBOX_HEIGHT};
    ;

    private static LinearLayout mSizeText;
    private static int mCurrentAxis;

    //Empty constructor
    public ViewerMainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retain instance to keep the Fragment from destroying itself
        setRetainInstance(true);

        mSlicingHandler = new SlicingHandler(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Reference to View
        mRootView = null;

        //If is not new
        if (savedInstanceState == null) {
            InitViewElements(inflater, container);


            draw();

            //Hide the action bar when editing the scale of the model
            mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Rect r = new Rect();
                    mRootView.getWindowVisibleDisplayFrame(r);

                    if (mSurface.getEditionMode() == ViewerSurfaceView.SCALED_EDITION_MODE){

                        SetWindow(r);
                    }

                }
            });
        }

        return mRootView;

    }

    private void SetWindow(Rect r) {
        int[] location = new int[2];
        int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);

        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...

            if (!isKeyboardShown) {
                SetkBuildVersion(location, true);
            }
        } else
            {
            if (isKeyboardShown) {
                SetkBuildVersion(location, false);
            }
        }
    }
    private void SetkBuildVersion(int[] location, boolean state){
        isKeyboardShown = state;
        mActionModePopupWindow.getContentView().getLocationInWindow(location);

        if (Build.VERSION.SDK_INT >= 19)
            mActionModePopupWindow.update(location[0], location[1] + MENU_HIDE_OFFSET_SMALL);
        else  mActionModePopupWindow.update(location[0], location[1] - MENU_HIDE_OFFSET_BIG);
    }

    private void InitViewElements(LayoutInflater inflater, ViewGroup container) {
        //Show custom option menu
        setHasOptionsMenu(true);

        //Inflate the fragment
        mRootView = inflater.inflate(R.layout.print_panel_main,
                container, false);

        mContext = getActivity();


        //Register receiver
        mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        initUIElements();

//            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Init slicing elements
        mSidePanelHandler = new SidePanelHandler(mSlicingHandler, getActivity(), mRootView);
        mCurrentType = WitboxFaces.TYPE_WITBOX;
        mCurrentPlate = new int[]{WitboxFaces.WITBOX_LONG, WitboxFaces.WITBOX_WITDH, WitboxFaces.WITBOX_HEIGHT};

        mSurface = new ViewerSurfaceView(mContext, mDataList, NORMAL, DONT_SNAPSHOT, mSlicingHandler);
    }


    /**
     * ********************** UI ELEMENTS *******************************
     */

    private void initUIElements() {

        //Set behavior of the expandable panel
        final FrameLayout expandablePanel = (FrameLayout) mRootView.findViewById(R.id.advanced_options_expandable_panel);
        expandablePanel.post(new Runnable() { //Get the initial height of the panel after onCreate is executed
            @Override
            public void run() {
                int mSettingsPanelMinHeight = expandablePanel.getMeasuredHeight();
            }
        });

        //Set elements to handle the model
        mSeekBar = (SeekBar) mRootView.findViewById(R.id.barLayer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mSeekBar.getThumb().mutate().setAlpha(0);
        mSeekBar.setVisibility(View.INVISIBLE);

        //Undo button bar
        mUndoButtonBar = (LinearLayout) mRootView.findViewById(R.id.model_button_undo_bar_linearlayout);

        mLayout = (FrameLayout) mRootView.findViewById(R.id.viewer_container_framelayout);

        mVisibilityModeButton = (ImageButton) mRootView.findViewById(R.id.visibility_button);
        mVisibilityModeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                showVisibilityPopUpMenu();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDataList.get(0).setActualLayer(progress);
                mSurface.requestRender();
            }
        });


        /*****************************
         * EXTRA
         *****************************/
        mProgress = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
        mProgress.setVisibility(View.GONE);
        mSizeText = (LinearLayout) mRootView.findViewById(R.id.axis_info_layout);
        mActionImage = (ImageView) mRootView.findViewById(R.id.print_panel_bar_action_image);


        mStatusBottomBar = (LinearLayout) mRootView.findViewById(R.id.model_status_bottom_bar);
        mRotationLayout = (LinearLayout) mRootView.findViewById(R.id.model_button_rotate_bar_linearlayout);
        mScaleLayout  = (LinearLayout) mRootView.findViewById(R.id.model_button_scale_bar_linearlayout);

        mTextWatcherX = new ScaleChangeListener(0);
        mTextWatcherY = new ScaleChangeListener(1);
        mTextWatcherZ = new ScaleChangeListener(2);

        mScaleEditX = (EditText) mScaleLayout.findViewById(R.id.scale_bar_x_edittext);
        mScaleEditY = (EditText) mScaleLayout.findViewById(R.id.scale_bar_y_edittext);
        mScaleEditZ = (EditText) mScaleLayout.findViewById(R.id.scale_bar_z_edittext);
        mUniformScale = (ImageButton) mScaleLayout.findViewById(R.id.scale_uniform_button);
        mUniformScale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUniformScale.isSelected()){
                    mUniformScale.setSelected(false);
                } else {
                    mUniformScale.setSelected(true);
                }


            }
        });
        mUniformScale.setSelected(true);

        mScaleEditX.addTextChangedListener(mTextWatcherX);
        mScaleEditY.addTextChangedListener(mTextWatcherY);
        mScaleEditZ.addTextChangedListener(mTextWatcherZ);

        mStatusBottomBar.setVisibility(View.VISIBLE);
        mBottomBar = (FrameLayout) mRootView.findViewById(R.id.bottom_bar);
        mBottomBar.setVisibility(View.INVISIBLE);
        mCurrentAxis = -1;

    }


    /**
     * *************************************************************************
     */

    public static void initSeekBar(int max) {
        mSeekBar.setMax(max);
        mSeekBar.setProgress(max);
    }

    public static void configureProgressState(int v) {
        if (v == View.GONE) mSurface.requestRender();
        else if (v == View.VISIBLE) mProgress.bringToFront();

        mProgress.setVisibility(v);
    }


    /**
     * ********************** OPTIONS MENU *******************************
     */
    //Create option menu and inflate viewer menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.print_panel_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        switch (item.getItemId()) {

            case R.id.viewer_open:
                FileBrowser.openFileBrowser(getActivity(), FileBrowser.VIEWER, getString(R.string.choose_file), ".stl", ".gcode");
                return true;

            case R.id.viewer_save:
                //saveNewProject();
                return true;

            case R.id.viewer_restore:
                //optionRestoreView();
                return true;

            case R.id.viewer_clean:

                optionClean();

                return true;

            case R.id.library_settings:
                hideActionModePopUpWindow();
                hideCurrentActionPopUpWindow();
                MainActivity.showExtraFragment(0, 0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Clean the print panel and delete all references
     */
    public static void optionClean() {

        //Delete slicing reference
        //DatabaseController.handlePreference("Slicing", "Last", null, false);

        mDataList.clear();
        mFile = null;
        
        if (mSlicingHandler!=null){

            mSlicingHandler.setOriginalProject(null);
            mSlicingHandler.setLastReference(null);
            mSeekBar.setVisibility(View.INVISIBLE);
            mSurface.requestRender();
            showProgressBar(0,0);
        }


    }


    //Select the last object added
    public static void doPress(){

        mSurface.doPress(mDataList.size() - 1);

    }

    public static void draw() {

        String filePath = "";
        if (mFile != null) filePath = mFile.getAbsolutePath();

        //Add the view
        mLayout.removeAllViews();
        mLayout.addView(mSurface, 0);
        mLayout.addView(mSeekBar, 1);
        mLayout.addView(mSizeText, 2);
    }


    /**
     * ********************** SURFACE CONTROL *******************************
     */
    //This method will set the visibility of the surfaceview so it doesn't overlap
    //with the video grid view
    public void setSurfaceVisibility(int i) {

        if (mSurface != null) {
            switch (i) {
                case 0:
                    mSurface.setVisibility(View.GONE);
                    break;
                case 1:
                    mSurface.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private static PopupWindow mActionModePopupWindow;
    private static PopupWindow mCurrentActionPopupWindow;

    /**
     * ********************** ACTION MODE *******************************
     */

    /**
     * Show a pop up window with the available actions of the item
     */
    public static void showActionModePopUpWindow() {

        hideCurrentActionPopUpWindow();

        mSizeText.setVisibility(View.VISIBLE);

        if (mActionModePopupWindow == null) {

            //Get the content view of the pop up window
            final LinearLayout popupLayout = (LinearLayout) ((Activity) mContext).getLayoutInflater()
                    .inflate(R.layout.item_edit_popup_menu, null);
            popupLayout.measure(0, 0);

            //Set the behavior of the action buttons
            int imageButtonHeight = 0;
            for (int i = 0; i < popupLayout.getChildCount(); i++) {
                View v = popupLayout.getChildAt(i);
                if (v instanceof ImageButton) {
                    ImageButton ib = (ImageButton) v;
                    imageButtonHeight = ib.getMeasuredHeight();
                    ib.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onActionItemSelected((ImageButton) view);
                        }
                    });
                }
            }

            //Show the pop up window in the correct position
            int[] viewerContainerCoordinates = new int[2];
            mLayout.getLocationOnScreen(viewerContainerCoordinates);
            int popupLayoutPadding = (int) mContext.getResources().getDimensionPixelSize(R.dimen.content_padding_normal);
            int popupLayoutWidth = popupLayout.getMeasuredWidth();
            int popupLayoutHeight = popupLayout.getMeasuredHeight();
            final int popupLayoutX = viewerContainerCoordinates[0] + mLayout.getWidth() - popupLayoutWidth;
            final int popupLayoutY = viewerContainerCoordinates[1] + imageButtonHeight + popupLayoutPadding;



            mActionModePopupWindow.showAtLocation(mSurface, Gravity.NO_GRAVITY,
                    popupLayoutX, popupLayoutY);

        }
    }

    /**
     * Hide the action mode pop up window
     */
    public static void hideActionModePopUpWindow() {
        if (mActionModePopupWindow != null) {
            mActionModePopupWindow.dismiss();
            mSurface.exitEditionMode();
            mRotationLayout.setVisibility(View.GONE);
            mScaleLayout.setVisibility(View.GONE);
            mStatusBottomBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.INVISIBLE);
            mActionModePopupWindow = null;
            mSurface.setRendererAxis(-1);
        }

        //Hide size text
        if (mSizeText != null)
            if (mSizeText.getVisibility() == View.VISIBLE) mSizeText.setVisibility(View.INVISIBLE);

    }

    /**
     * Hide the current action pop up window if it is showing
     */
    public static void hideCurrentActionPopUpWindow() {
        if (mCurrentActionPopupWindow != null) {
            mCurrentActionPopupWindow.dismiss();
            mCurrentActionPopupWindow = null;
        }
        hideSoftKeyboard();
    }

    public static void hideSoftKeyboard() {
        try{
            InputMethodManager inputMethodManager = (InputMethodManager)  mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity)mContext).getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e){

        }

    }

    /**
     * Perform the required action depending on the pressed button
     *
     * @param item Action button that has been pressed
     */
    public static void onActionItemSelected(final ImageButton item) {

        mStatusBottomBar.setVisibility(View.VISIBLE);
        mSurface.setRendererAxis(-1);
        mRotationLayout.setVisibility(View.GONE);
        mScaleLayout.setVisibility(View.GONE);
        mBottomBar.setVisibility(View.INVISIBLE);
        mSizeText.setVisibility(View.VISIBLE);

        selectActionButton(item.getId());

        switch (item.getId()) {
            case R.id.move_item_button:
                hideCurrentActionPopUpWindow();
                mSurface.setEditionMode(ViewerSurfaceView.MOVE_EDITION_MODE);
                break;
            case R.id.rotate_item_button:

                if (mCurrentActionPopupWindow == null) {
                    final String[] actionButtonsValues = mContext.getResources().getStringArray(R.array.rotate_model_values);
                    final TypedArray actionButtonsIcons = mContext.getResources().obtainTypedArray(R.array.rotate_model_icons);
                    showHorizontalMenuPopUpWindow(item, actionButtonsValues, actionButtonsIcons,
                            null, new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //changeCurrentAxis(Integer.parseInt(actionButtonsValues[position]));
                                    mBottomBar.setVisibility(View.VISIBLE);
                                    mRotationLayout.setVisibility(View.VISIBLE);
                                    mSurface.setEditionMode(ViewerSurfaceView.ROTATION_EDITION_MODE);
                                    hideCurrentActionPopUpWindow();
                                    item.setImageResource(actionButtonsIcons.getResourceId(position, -1));
                                    mActionImage.setImageDrawable(mContext.getResources().getDrawable(actionButtonsIcons.getResourceId(position, -1)));
                                }
                            });
                } else {
                    hideCurrentActionPopUpWindow();
                }
                break;
            case R.id.scale_item_button:
                hideCurrentActionPopUpWindow();
                mBottomBar.setVisibility(View.VISIBLE);
                mScaleLayout.setVisibility(View.VISIBLE);
                mSurface.setEditionMode(ViewerSurfaceView.SCALED_EDITION_MODE);
                mActionImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_scale));
                displayModelSize(mSurface.getObjectPresed());
                break;
            case R.id.multiply_item_button:
                hideCurrentActionPopUpWindow();
                showMultiplyDialog();
                break;
            case R.id.delete_item_button:
                hideCurrentActionPopUpWindow();
                mSurface.deleteObject();
                hideActionModePopUpWindow();
                break;
        }

    }


    /**
     * Set the state of the selected action button
     *
     * @param selectedId Id of the action button that has been pressed
     */
    public static void selectActionButton(int selectedId) {

        if (mActionModePopupWindow != null) {
            //Get the content view of the pop up window
            final LinearLayout popupLayout = (LinearLayout) mActionModePopupWindow.getContentView();

            //Set the behavior of the action buttons
            for (int i = 0; i < popupLayout.getChildCount(); i++) {
                View v = popupLayout.getChildAt(i);
                if (v instanceof ImageButton) {
                    ImageButton ib = (ImageButton) v;
                    if (ib.getId() == selectedId)
                        ib.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.oval_background_green));
                    else
                        ib.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_button_selector_dark));
                }
            }
        }
    }

    /**
     * Show a pop up window with the visibility options: Normal, overhang, transparent and layers.
     */
    public void showVisibilityPopUpMenu() {

        //Hide action mode pop up window to show the new menu
        hideActionModePopUpWindow();


        //Show a menu with the visibility options
        if (mCurrentActionPopupWindow == null) {
            final String[] actionButtonsValues = mContext.getResources().getStringArray(R.array.models_visibility_values);
            final TypedArray actionButtonsIcons = mContext.getResources().obtainTypedArray(R.array.models_visibility_icons);
            showHorizontalMenuPopUpWindow(mVisibilityModeButton, actionButtonsValues, actionButtonsIcons,
                    Integer.toString(mCurrentViewMode), new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            //Change the view mode of the model
                            //changeStlViews(Integer.parseInt(actionButtonsValues[position]));
                            hideCurrentActionPopUpWindow();
                        }
                    });
        } else {
            hideCurrentActionPopUpWindow();
        }

    }

    /**
     * Show a pop up window with a horizontal list view as a content view
     */
    public static void showHorizontalMenuPopUpWindow(View currentView, String[] actionButtonsValues,
                                                     TypedArray actionButtonsIcons,
                                                     String selectedOption,
                                                     AdapterView.OnItemClickListener onItemClickListener) {

        HorizontalListView landscapeList = new HorizontalListView(mContext, null);


        landscapeList.measure(0, 0);

        int popupLayoutHeight = 0;
        int popupLayoutWidth = 0;

        //Show the pop up window in the correct position
        int[] actionButtonCoordinates = new int[2];
        currentView.getLocationOnScreen(actionButtonCoordinates);
        int popupLayoutPadding = (int) mContext.getResources().getDimensionPixelSize(R.dimen.content_padding_normal);
        final int popupLayoutX = actionButtonCoordinates[0] - popupLayoutWidth - popupLayoutPadding / 2;
        final int popupLayoutY = actionButtonCoordinates[1];



        mCurrentActionPopupWindow.showAtLocation(mSurface, Gravity.NO_GRAVITY, popupLayoutX, popupLayoutY);
    }

    /**
     * ********************** MULTIPLY ELEMENTS *******************************
     */

    public static void showMultiplyDialog() {
        View multiplyModelDialog = LayoutInflater.from(mContext).inflate(R.layout.dialog_multiply_model, null);
        final NumberPicker numPicker = (NumberPicker) multiplyModelDialog.findViewById(R.id.number_copies_numberpicker);
        numPicker.setMaxValue(10);
        numPicker.setMinValue(0);

        final int count = numPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numPicker)).setColor(mContext.getResources().getColor(R.color.theme_primary_dark));
                    ((EditText) child).setTextColor(mContext.getResources().getColor(R.color.theme_primary_dark));

                    Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                    for (Field pf : pickerFields) {
                        if (pf.getName().equals("mSelectionDivider")) {
                            pf.setAccessible(true);
                            try {
                                pf.set(numPicker, mContext.getResources().getDrawable(R.drawable.separation_line_horizontal));
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    numPicker.invalidate();
                } catch (NoSuchFieldException e) {
                    Log.w("setNumberPickerTextColor", e.toString());
                } catch (IllegalAccessException e) {
                    Log.w("setNumberPickerTextColor", e.toString());
                } catch (IllegalArgumentException e) {
                    Log.w("setNumberPickerTextColor", e.toString());
                }
            }
        }

        //Remove soft-input from number picker
        numPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        final MaterialDialog.Builder createFolderDialog = new MaterialDialog.Builder(mContext);
        createFolderDialog.title(R.string.viewer_menu_multiply_title)
                .customView(multiplyModelDialog, true)
                .positiveColorRes(R.color.theme_accent_1)
                .positiveText(R.string.dialog_continue)
                .negativeColorRes(R.color.body_text_2)
                .negativeText(R.string.cancel)
                .autoDismiss(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        drawCopies(numPicker.getValue());
                        slicingCallback();
                    }
                })
                .show();

    }

    private static void drawCopies(int numCopies) {
        int model = mSurface.getObjectPresed();
        int num = 0;

        while (num < numCopies) {
            final DataStorage newData = new DataStorage();
            newData.copyData(mDataList.get(model));
            mDataList.add(newData);

            /**
             * Check if the piece is out of the plate and stop multiplying
             */
            if (!Geometry.relocateIfOverlaps(mDataList)) {

                Toast.makeText(mContext, R.string.viewer_multiply_error, Toast.LENGTH_LONG).show();
                mDataList.remove(newData);
                break;

            }

            num++;
        }

        draw();
    }

/**
 * **************************** PROGRESS BAR FOR SLICING ******************************************
 */

    /**
     * Static method to show the progress bar by sending an integer when receiving data from the socket
     *
     * @param i either -1 to hide the progress bar, 0 to show an indefinite bar, or a normal integer
     */
    public static void showProgressBar(int status, int i) {


        if (mRootView!=null){


            ProgressBar pb = (ProgressBar) mRootView.findViewById(R.id.progress_slice);
            TextView tv = (TextView) mRootView.findViewById(R.id.viewer_text_progress_slice);
            TextView tve = (TextView) mRootView.findViewById(R.id.viewer_text_estimated_time);
            TextView tve_title = (TextView) mRootView.findViewById(R.id.viewer_estimated_time_textview);

            if ( mSlicingHandler.getLastReference()!= null) {

                tve_title.setVisibility(View.VISIBLE);
                pb.setVisibility(View.VISIBLE);

            }else {

                pb.setVisibility(View.INVISIBLE);
                tve_title.setVisibility(View.INVISIBLE);
                tv.setText(null);
                tve.setText(null);
                mRootView.invalidate();



            }
        }



    }

    /**
     * Display model width, depth and height when touched
     */
    public static void displayModelSize(int position) {
        try {
            //TODO RANDOM CRASH ArrayIndexOutOfBoundsException
            DataStorage data = mDataList.get(position);

            //Set point instead of comma
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator(',');

            //Define new decimal format to display only 2 decimals
            DecimalFormat df = new DecimalFormat("##.##", otherSymbols);

            String width = df.format((data.getMaxX() - data.getMinX()));
            String depth = df.format((data.getMaxY() - data.getMinY()));
            String height = df.format((data.getMaxZ() - data.getMinZ()));

            //Display size of the model
            //mSizeText.setText("W = " + width + " mm / D = " + depth + " mm / H = " + height + " mm");
            //mSizeText.setText(String.format(mContext.getResources().getString(R.string.viewer_axis_info), Double.parseDouble(width), Double.parseDouble(depth), Double.parseDouble(height)));

            Log.i("Scale","Vamos a petar " + width);
            ((TextView) mSizeText.findViewById(R.id.print_panel_x_size)).setText(width);
            ((TextView) mSizeText.findViewById(R.id.print_panel_y_size)).setText(depth);
            ((TextView) mSizeText.findViewById(R.id.print_panel_z_size)).setText(height);

            if (mScaleLayout.getVisibility() == View.VISIBLE){

                mScaleEditX.removeTextChangedListener(mTextWatcherX);
                mScaleEditY.removeTextChangedListener(mTextWatcherY);
                mScaleEditZ.removeTextChangedListener(mTextWatcherZ);

                mScaleEditX.setText(width);
                mScaleEditX.setSelection(mScaleEditX.getText().length());
                mScaleEditY.setText(depth);
                mScaleEditY.setSelection(mScaleEditY.getText().length());
                mScaleEditZ.setText(height);
                mScaleEditZ.setSelection(mScaleEditZ.getText().length());

                mScaleEditX.addTextChangedListener(mTextWatcherX);
                mScaleEditY.addTextChangedListener(mTextWatcherY);
                mScaleEditZ.addTextChangedListener(mTextWatcherZ);
            }

        } catch (ArrayIndexOutOfBoundsException e) {

            e.printStackTrace();
        }


    }

    /**
     * Receives the "download complete" event asynchronously
     */
    public BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

  /*          if (DatabaseController.getPreference(DatabaseController.TAG_SLICING, "Last") != null)
                if ((DatabaseController.getPreference(DatabaseController.TAG_SLICING, "Last")).equals("temp.gco")) {

                    DatabaseController.handlePreference(DatabaseController.TAG_SLICING, "Last", null, false);


                    showProgressBar(StateUtils.SLICER_HIDE, 0);
                } else {

                }
*/

        }
    };

    /**
     * Notify the side panel adapters, check for null if they're not available yet (rare case)
     */
    public void notifyAdapter() {

        try {
            if (mSidePanelHandler.profileAdapter != null)
                mSidePanelHandler.profileAdapter.notifyDataSetChanged();

            mSidePanelHandler.reloadProfileAdapter();

        } catch (NullPointerException e) {

            e.printStackTrace();
        }


    }

    //Refresh printers when the fragmetn is shown
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mSidePanelHandler.refreshPrinters();
    }


    public static void slicingCallback() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (sharedPref.getBoolean(mContext.getResources().getString(R.string.shared_preferences_autoslice), true)) {

            SliceTask task = new SliceTask();
            mSidePanelHandler.refreshPrinters();
            task.execute();
        } else {

            mSidePanelHandler.refreshPrinters();
            mSidePanelHandler.switchSlicingButton(true);
        }


    }

    public static void slicingCallbackForced(){

//        SliceTask task = new SliceTask();
        mSidePanelHandler.refreshPrinters();
//        task.execute();

        Handler slicingHandler = new Handler();
        slicingHandler.post(mSliceRunnable);
    }

    static Runnable mSliceRunnable = new Runnable(){

        @Override
        public void run() {

            final List<DataStorage> newList = new ArrayList<DataStorage>(mDataList);

            //Code to update the UI─aÇ >
            //Check if the file is not yet loaded
            for (int i = 0; i < newList.size(); i++) {

                if (newList.get(i).getVertexArray() == null) {
                    return;
                }

            }

            if ((mSlicingHandler != null) && (mFile != null)) {



            }

        }
    };

    static class SliceTask extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            Log.i("Slicer","Starting background slicing task");

            final List<DataStorage> newList = new ArrayList<DataStorage>(mDataList);

            //Code to update the UI─aÇ >
            //Check if the file is not yet loaded
            for (int i = 0; i < newList.size(); i++) {

                if (newList.get(i).getVertexArray() == null) {
                    return null;
                }

            }

            if ((mSlicingHandler != null) && (mFile != null)) {



            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }


    /**
     * *********************************  SIDE PANEL *******************************************************
     */

    public static File getFile() {
        return mFile;
    }

    public static int[] getCurrentPlate() {
        return mCurrentPlate;
    }

    public static int getCurrentType() {
        return mCurrentType;
    }

    public static void changePlate(String resource) throws NullPointerException {

        //JSONObject profile = ModelProfile.retrieveProfile(mContext, resource, ModelProfile.TYPE_P);



        mSurface.changePlate(mCurrentPlate);
        mSurface.requestRender();
    }

    public static void setSlicingPosition(float x, float y) {

        JSONObject position = new JSONObject();
        try {

            //mPreviousOffset = new Geometry.Point(x,y,0);

            position.put("x", (int) x + mCurrentPlate[0]);
            position.put("y", (int) y + mCurrentPlate[1]);

            mSlicingHandler.setExtras("position", position);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    public static void displayErrorInAxis(int axis){

        if (mScaleLayout.getVisibility() == View.VISIBLE){
            switch (axis){

               // case 0: mScaleEditX.setError(mContext.getResources().getString(R.string.viewer_error_bigger_plate,mCurrentPlate[0] * 2));
               //     break;

                //case 1: mScaleEditY.setError(mContext.getResources().getString(R.string.viewer_error_bigger_plate,mCurrentPlate[1] * 2));
                 //   break;

            }
        }
    }


    private class ScaleChangeListener implements TextWatcher{

        int mAxis;

        private ScaleChangeListener(int axis){

            mAxis = axis;

        }


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            mScaleEditX.setError(null);
            mScaleEditY.setError(null);
            mScaleEditZ.setError(null);

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            boolean valid = true;


            //Check decimals
           if (editable.toString().endsWith(".")){
               valid = false;

            }


            if (valid)
            try{
                switch (mAxis){

                    case 0:
                        mSurface.doScale(Float.parseFloat(editable.toString()), 0, 0, mUniformScale.isSelected());
                        break;

                    case 1:
                        mSurface.doScale(0, Float.parseFloat(editable.toString()), 0, mUniformScale.isSelected());
                        break;

                    case 2:
                        mSurface.doScale(0, 0, Float.parseFloat(editable.toString()), mUniformScale.isSelected());
                        break;

                }
            } catch (NumberFormatException e){

                e.printStackTrace();

            }
        }
    }
}
