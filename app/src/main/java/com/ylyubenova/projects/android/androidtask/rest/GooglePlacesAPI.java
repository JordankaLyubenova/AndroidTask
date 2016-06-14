package com.ylyubenova.projects.android.androidtask.rest;

import com.ylyubenova.projects.android.androidtask.model.Bars;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by user on 5/24/16.
 */
public interface GooglePlacesAPI {
    public String BASE_URL="https://maps.googleapis.com/maps/api/place/";

    @GET("textsearch/json\n" +
            "?type=bar\n" +
            "&radius=50000\n" +
            "&key=GOOGLE_PLACES_KEY")
    Call<Bars> getNearestBars(@Query("location") String location);

    class Factory{
        private static GooglePlacesAPI service;

        public static GooglePlacesAPI getInstance(){
            if(service==null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(GooglePlacesAPI.class);
            }
                return service;
        }


    }

}
