package com.bitlove.fetlife.model.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

public class FetLifeService {

    private static final String LOGON_BASE_URL = "https://fetlife.com";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_TOKEN_REFRESH = "refresh_token";
    public static final String AUTH_HEADER_PREFIX = "Bearer ";

    private final FetLifeApi fetLifeApi;

    private int lastResponseCode = -1;

    public FetLifeService() {

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                //response.body().string();
                lastResponseCode = response.code();
                return response;
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        fetLifeApi = new Retrofit.Builder()
                .baseUrl(LOGON_BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
                .create(FetLifeApi.class);
    }

    public FetLifeApi getFetLifeApi() {
        return fetLifeApi;
    }

    public int getLastResponseCode() {
        return lastResponseCode;
    }
}
