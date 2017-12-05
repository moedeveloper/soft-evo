package android.app.printerapp.library;

import android.app.printerapp.Log;
import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.Detail;
import android.app.printerapp.viewer.STLViewer;
import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SAMSUNG on 2017-11-20.
 */

public class FileManager {

    //List to keep track of all models loaded this instance
    private static List<File> detailModels = new ArrayList<>();

    //Deletes the folder we use to hold model files
    public static void deleteCache(Context context) {
        try {
            File dir = context.getDir("Octoprint", context.MODE_PRIVATE);
            deleteDir(dir);
        } catch (Exception e) {}
        detailModels.clear();
    }

    //Gets the modelFile from Detail as input
    public static File getModelFile(Detail detail){
        for(File file : detailModels){
            if((file.getName().replace(".stl", "")).equals(detail.getIdName())){
               return file;
            }
        }

        return null;
    }

    //Checks if model exists in system
    public static boolean modelExistsInSystem(Detail detail){
        return getModelFile(detail) != null;
    }

    //Download and opens file in the STLViewer
    public static void downloadAndOpenFile(final Context context, final STLViewer viewer, final Detail detail){
        manageCache(context);
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        Call<ResponseBody> call = databaseHandler.getApiService().downloadStlFile(Integer.parseInt(detail.getFileId()));
        call.enqueue(new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("PrintsSpecificFragment", "server contacted and has file");
                    new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = FileManager.writeResponseBodyToDisk(response.body(), context, detail.getIdName());
                            Log.d("PrintsSpecificFragment", "file download was a success? " + writtenToDisk);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            openFileInSTLViewer(viewer, detail);
                        }
                    }.execute();

                }else{
                    Log.d("PrintsSpecificFragment", "server contact failed");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("PrintsSpecificFragment", "error");
            }
        });
    }

    //Scans for all stl files in given directory
    public static File[] scanStlFiles(String path){
        File dir = new File(path);
        FileFilter filter = new FileFilter(){

            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().matches(".*\\.stl");
            }
        };
        return dir.listFiles(filter);
    }


//---------------------------------------------------------------------------------------
//          HELPER CLASSES
//---------------------------------------------------------------------------------------

    private static void openFileInSTLViewer(STLViewer viewer, Detail d) {
        viewer.optionClean();
        File modelFile = FileManager.getModelFile(d);

        if(modelFile != null){
            viewer.openFileDialog(modelFile.getAbsolutePath());
        }
    }

    //Write to disk
    private static boolean writeResponseBodyToDisk(ResponseBody body, Context context, String fileName) {
        try {
            // TODO: Set correct directory
            // TODO: Make sure space is available
            // TODO: Remove files after use
            // TODO: When cache getting full, start removing the oldest files

            File stlFile = new File(context.getDir("Octoprint", context.MODE_PRIVATE) + File.separator + fileName + ".stl");

            Log.d("PrintsSpecificFragment", "directory: " + context.getDir("Octoprint", context.MODE_PRIVATE) +
                    "\ntotal space:" + stlFile.getTotalSpace() + "\nfree space: " +
                    stlFile.getFreeSpace());

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(stlFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("PrintsSpecificFragment", "file download: " + fileSizeDownloaded + " of " + fileSize);

                    //Add model to our list reference of the files
                    detailModels.add(stlFile);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    //Removes all files in cache if the size of it exceeds 200mb
    private static void manageCache(Context context){
        File cacheDir = context.getDir("Octoprint", context.MODE_PRIVATE);
        if(getSizeOfFiles(cacheDir.listFiles()) >= 200000000){
            deleteCache(context);
        }
    }

    //Recursively gets size of files
    private static int getSizeOfFiles(File[] files){
        int totalSize = 0;
        for(File currentFile : files){
            if(currentFile == null){
                //Do nothing
            } else if(currentFile.isDirectory()){
                totalSize += getSizeOfFiles(currentFile.listFiles());
            } else {
                totalSize += currentFile.length();
            }
        }
        return totalSize;
    }

    //Recursively deletes all files in directory including the directory itself
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
