package android.app.printerapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SAMSUNG on 2017-11-17.
 */

public final class DatabaseHandler {

    private static DatabaseHandler instance;
    private ApiService apiService;

    private DatabaseHandler(){
//      Initialize the connection
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Accept", "application/json")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .readTimeout(20,TimeUnit.SECONDS).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
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
