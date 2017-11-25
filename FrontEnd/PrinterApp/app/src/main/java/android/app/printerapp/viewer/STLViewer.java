package android.app.printerapp.viewer;

import android.app.printerapp.R;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
    private Context mContext;
    private ViewerSurfaceView mSurface;
    private FrameLayout mLayout;

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
        mCurrentPlate = new int[]{WitboxFaces.WITBOX_LONG, WitboxFaces.WITBOX_WITDH, WitboxFaces.WITBOX_HEIGHT};
        mSurface = new ViewerSurfaceView(this, getContext(), mDataList, NORMAL, DONT_SNAPSHOT);
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

    public void openFileDialog(final String filePath) {

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
    }

    public void resetWhenCancel() {
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
    public void openFile(String filePath) {
        DataStorage data = null;
        //Open the file
        data = new DataStorage();

        mFile = new File(filePath);
        StlFile.openStlFile(this, mContext, mFile, data, DONT_SNAPSHOT);
        mCurrentViewMode = NORMAL;

        mDataList.add(data);
    }

    //Clear all data in the STL Viewer
    public void optionClean() {
        mDataList.clear();
        mFile = null;
    }

    //Draws the 3d viewer
    public void draw() {
        //Once the file has been opened, we need to refresh the data list.
        //If we are opening a .gcode file, we need to ic_action_delete the
        //previous files (.stl and .gcode)
        //If we are opening a .stl file, we need to ic_action_delete the previous file only
        //if it was a .gcode file.
        //We have to do this here because user can cancel the opening of the file and the
        //Print Panel would appear empty if we clear the data list

        String filePath = "";
        if (mFile != null) filePath = mFile.getAbsolutePath();

        Geometry.relocateIfOverlaps(this, mDataList);

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

    public File getFile() {
        return mFile;
    }

    public int[] getCurrentPlate() {
        return mCurrentPlate;
    }


}
