package android.app.printerapp;

import android.app.printerapp.api.DatabaseHandler;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SAMSUNG on 2017-11-20.
 */

public class FileManager {


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

    public static void discardUnusedFiles(){

    }

    public static void downloadFile(final Context context){
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        Call<ResponseBody> call = databaseHandler.getApiService().downloadStlFile(1);
        call.enqueue(new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("PrintsSpecificFragment", "server contacted and has file");
                    new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = FileManager.writeResponseBodyToDisk(response.body(), context);
                            Log.d("PrintsSpecificFragment", "file download was a success? " + writtenToDisk);
                            return null;
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
    public static boolean writeResponseBodyToDisk(ResponseBody body, Context context) {
        try {
            // TODO: Set correct directory
            // TODO: Make sure space is available
            // TODO: Remove files after use
            // TODO: When cache getting full, start removing the oldest files

            File stlFile = new File(context.getDir("Octoprint", context.MODE_PRIVATE) + File.separator + "test.stl");

            Log.d("PrintsSpecificFragment", "directory: " + context.getCacheDir() +
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
}
