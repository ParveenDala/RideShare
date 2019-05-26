package com.parveendala.rideshare.network;

import com.parveendala.rideshare.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class RetrofitClient {

    private static RetrofitClient connection;

    public static RetrofitClient getInstance() {
        if (connection == null) {
            connection = new RetrofitClient();
        }
        return connection;
    }

    private ApiService apiService;

    public ApiService getRetrofitService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(createDefaultClient())
                    .baseUrl(Constants.MAPS_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    private boolean isLogEnabled() {
        return true;
    }

    private OkHttpClient createDefaultClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (isLogEnabled()) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }
}
