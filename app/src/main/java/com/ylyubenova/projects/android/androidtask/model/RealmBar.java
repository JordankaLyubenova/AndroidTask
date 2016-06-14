package com.ylyubenova.projects.android.androidtask.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.ylyubenova.projects.android.androidtask.LocationTracker;
import com.ylyubenova.projects.android.androidtask.Utils;

/**
 * Created by user on 5/25/16.
 */
public class RealmBar implements Parcelable{
    private String barName;
    private Location barLocation;

    public RealmBar(String barName, Location barLocation) {
        this.barName = barName;
        this.barLocation = barLocation;
    }

    protected RealmBar(Parcel in) {
        barName = in.readString();
    }

    public static final Creator<RealmBar> CREATOR = new Creator<RealmBar>() {
        @Override
        public RealmBar createFromParcel(Parcel in) {
            return new RealmBar(in);
        }

        @Override
        public RealmBar[] newArray(int size) {
            return new RealmBar[size];
        }
    };

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public Location getBarLocation() {
        return barLocation;
    }

    public void setBarLocation(Location barLocation) {
        this.barLocation = barLocation;
    }

    public String calculateDistance(Location location){
        double distance= Utils.calculateDistance(barLocation.getLng(),barLocation.getLat(),location.getLng(),location.getLat());
        return String.format("%.2f km", distance / 1000);
    }
    public String calculateDistance(){
        Location location= LocationTracker.getInstance().getLastLocation();
        if(location==null) return null;
        double distance= Utils.calculateDistance(barLocation.getLng(),barLocation.getLat(),location.getLng(),location.getLat());
        return String.format("%.2f km", distance / 1000);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barName);
    }
}
