package android.app.printerapp;

import android.app.printerapp.api.DatabaseHandler;
import android.app.printerapp.model.Detail;
import android.app.printerapp.viewer.STLViewer;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;

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

    private static List<File> detailModels = new ArrayList<>();


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
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

    public static void deleteFile(File file, Context context){

    }

    public static File getModelFile(Detail detail){
        for(File file : detailModels){
            if((file.getName().replace(".stl", "")).equals(detail.getIdName())){
               return file;
            }
        }

        return null;
    }

    public static void discardUnusedFiles(){

    }

    public static boolean modelExistsInSystem(Detail detail){
        return getModelFile(detail) != null;
    }

    public static void downloadAndOpenFile(final Context context, final Detail detail){
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
                            openFileInSTL(context, detail);
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

    //Write to disk
    public static boolean writeResponseBodyToDisk(ResponseBody body, Context context, String fileName) {
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

    //STLViewer has static methods. This is not good.
    private static void openFileInSTL(Context mContext, Detail d) {
        STLViewer.optionClean();
        File modelFile = FileManager.getModelFile(d);

        if(modelFile != null){
            STLViewer.openFileDialog(modelFile.getAbsolutePath());
        }
    }
}
