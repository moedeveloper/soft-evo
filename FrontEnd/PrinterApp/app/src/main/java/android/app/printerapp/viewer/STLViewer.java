package android.app.printerapp.viewer;

import android.app.printerapp.R;
import android.app.printerapp.library.LibraryController;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAMSUNG on 2017-11-20.
 * Right now, this only works if inflated using XML.
 */

public class STLViewer extends FrameLayout {
    //---------------------------------------
    // VARIABLES
    //---------------------------------------

    //  - Constants -
    private static final int NORMAL = 0;
    private static final int LAYER = 1;

    public static final int DO_SNAPSHOT = 0;
    public static final int DONT_SNAPSHOT = 1;
    public static final int PRINT_PREVIEW = 2;
    public static final boolean STL = true;

    // - Data variables for the viewer-
    private static File mFile;
    private static List<DataStorage> mDataList = new ArrayList<DataStorage>();
    private static int[] mCurrentPlate = new int[]{WitboxFaces.WITBOX_LONG,
            WitboxFaces.WITBOX_WITDH, WitboxFaces.WITBOX_HEIGHT};
    private static int mCurrentViewMode = 0;

    // - View variables -
    private static Context mContext;
    private static ViewerSurfaceView mSurface;
    private static FrameLayout mLayout;

    //---------------------------------------
    // CONSTRUCTORS
    //---------------------------------------
    public STLViewer(@NonNull Context context) {
        super(context);
        initializeViews(context);
    }

    public STLViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public STLViewer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    //---------------------------------------
    // METHODS FOR INITIALIZING THE VIEW
    //---------------------------------------
    //Initializes the layout
    private void initializeViews(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.stl_viewer, this);
    }

    //Upon finishing inflating, do this
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //Init elements for the viewer
        mCurrentPlate = new int[]{WitboxFaces.WITBOX_LONG, WitboxFaces.WITBOX_WITDH, WitboxFaces.WITBOX_HEIGHT};
        mSurface = new ViewerSurfaceView(getContext(), mDataList, NORMAL, DONT_SNAPSHOT);
        mContext = getContext();
        mLayout = this;
        draw();
    }

    //---------------------------------------
    // METHODS FOR CONTROLLING THE VIEW
    //---------------------------------------
    /**
     * Open a dialog if it's a GCODE to warn the user about unsaved data loss
     *
     * @param filePath
     */

    public static void openFileDialog(final String filePath) {

        if (LibraryController.hasExtension(0, filePath)) {

            if (!StlFile.checkFileSize(new File(filePath), mContext)) {
                new MaterialDialog.Builder(mContext)
                        .title(R.string.warning)
                        .content(R.string.viewer_file_size)
                        .negativeText(R.string.cancel)
                        .negativeColorRes(R.color.body_text_2)
                        .positiveText(R.string.ok)
                        .positiveColorRes(R.color.theme_accent_1)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                openFile(filePath);

                            }
                        })
                        .build()
                        .show();

            } else {
                openFile(filePath);
            }
        } else if (LibraryController.hasExtension(1, filePath)) {

            new MaterialDialog.Builder(mContext)
                    .title(R.string.warning)
                    .content(R.string.viewer_open_gcode_dialog)
                    .negativeText(R.string.cancel)
                    .negativeColorRes(R.color.body_text_2)
                    .positiveText(R.string.ok)
                    .positiveColorRes(R.color.theme_accent_1)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            openFile(filePath);
                        }
                    })
                    .build()
                    .show();
        }
    }

    public static void resetWhenCancel() {
        try {
            mDataList.remove(mDataList.size() - 1);
            mSurface.requestRender();

            mCurrentViewMode = NORMAL;
            mSurface.configViewMode(mCurrentViewMode);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    //Opens given file (STL)
    public static void openFile(String filePath) {
        DataStorage data = null;
        //Open the file
        if (LibraryController.hasExtension(0, filePath)) {

            data = new DataStorage();

            mFile = new File(filePath);
            StlFile.openStlFile(mContext, mFile, data, DONT_SNAPSHOT);
            mCurrentViewMode = NORMAL;

        } else if (LibraryController.hasExtension(1, filePath)) {

            data = new DataStorage();
            mFile = new File(filePath);
            GcodeFile.openGcodeFile(mContext, mFile, data, DONT_SNAPSHOT);
            mCurrentViewMode = LAYER;

        }

        mDataList.add(data);
    }

    //Clear all data in the STL Viewer
    public static void optionClean() {
        mDataList.clear();
        mFile = null;
    }

    //Draws the 3d viewer
    public static void draw() {
        //Once the file has been opened, we need to refresh the data list.
        //If we are opening a .gcode file, we need to ic_action_delete the
        //previous files (.stl and .gcode)
        //If we are opening a .stl file, we need to ic_action_delete the previous file only
        //if it was a .gcode file.
        //We have to do this here because user can cancel the opening of the file and the
        //Print Panel would appear empty if we clear the data list

        String filePath = "";
        if (mFile != null) filePath = mFile.getAbsolutePath();

        if (LibraryController.hasExtension(0, filePath)) {
            if (mDataList.size() > 1) {
                if (LibraryController.hasExtension(1, mDataList.get(mDataList.size() - 2).getPathFile())) {
                    mDataList.remove(mDataList.size() - 2);
                }
            }
            Geometry.relocateIfOverlaps(mDataList);

        } else if (LibraryController.hasExtension(1, filePath)) {
            if (mDataList.size() > 1)
                while (mDataList.size() > 1) {
                    mDataList.remove(0);
                }
        }

        //Add the view
        mLayout.removeAllViews();
        mLayout.addView(mSurface, 0);
        mSurface.setVisibility(View.VISIBLE);
    }

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

    public static File getFile() {
        return mFile;
    }

    public static int[] getCurrentPlate() {
        return mCurrentPlate;
    }


}
