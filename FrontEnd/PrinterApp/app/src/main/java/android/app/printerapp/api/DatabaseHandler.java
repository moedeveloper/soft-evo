package android.app.printerapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SAMSUNG on 2017-11-17.
 */

public final class DatabaseHandler {

    private static DatabaseHandler instance;
    private ApiService apiService;

    private DatabaseHandler(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

    }

    public static DatabaseHandler getInstance(){
        if(instance == null){
            instance = new DatabaseHandler();
        }
        return instance;
    }

    public ApiService getApiService(){
        return apiService;
    }

}
