package com.ylyubenova.projects.android.androidtask;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

/**
 * Created by user on 5/6/16.
 */
public class LocationTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    public Location mLastLocation;
    GoogleApiClient client;
    private static LocationTracker locationTracker;

    public void setLocationTrackerListner(LocationTrackerListner locationTrackerListner) {
        this.locationTrackerListner = locationTrackerListner;
    }

    private LocationTrackerListner locationTrackerListner;

    public interface LocationTrackerListner{
          void onLocationUpdate(Location location);
    }

    Context context;

    public static LocationTracker getInstance(){
        if(locationTracker==null)
            locationTracker =new LocationTracker();
        return locationTracker;
    }

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    public com.ylyubenova.projects.android.androidtask.model.Location getLastLocation(){
        com.ylyubenova.projects.android.androidtask.model.Location myLocation=null;
        if(mLastLocation!=null) {
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();
            myLocation = new com.ylyubenova.projects.android.androidtask.model.Location(lat, lng);
        }
        return myLocation;
    }
    public String getLocationString(){
        if(mLastLocation!=null) return mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if( ContextCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    client);
            if (mLastLocation != null) {
                locationTrackerListner.onLocationUpdate(mLastLocation);
            } else {
                //request location updates
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void setGlobalContext(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        locationTrackerListner.onLocationUpdate(location);

    }

    // Declaring a Location Manager
}
