package com.ylyubenova.projects.android.androidtask.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ylyubenova.projects.android.androidtask.LocationTracker;
import com.ylyubenova.projects.android.androidtask.R;
import com.ylyubenova.projects.android.androidtask.model.Location;
import com.ylyubenova.projects.android.androidtask.model.RealmBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/5/16.
 */
public class CustomMapFragment extends Fragment implements OnMapReadyCallback {


    private List<Marker> mMarkers;
    private List<RealmBar> realmBars;
    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;
    private LatLng cameraPositionLatLng;
    private static final String FRAGMENT_MAP_PLACES="fragment_map_places";
    private static final String FRAGMENT_CAMERA_POSITION="fragment_camera_position";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        if(savedInstanceState!=null){
            realmBars=savedInstanceState.getParcelableArrayList(FRAGMENT_MAP_PLACES);
            cameraPositionLatLng=savedInstanceState.getParcelable(FRAGMENT_CAMERA_POSITION);

        }else{
            cameraPositionLatLng = makeLatLngFromLocation(LocationTracker.getInstance().getLastLocation());
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, supportMapFragment).commit();
        } else {
            supportMapFragment.getMapAsync(this);
        }
        return supportMapFragment.getView();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void addMarkers(GoogleMap googleMap) {
        Log.d("Add Markers", "" + googleMap);
        mMarkers=new ArrayList<Marker>();
        if (realmBars != null) {
            for (RealmBar bar : realmBars) {
                MarkerOptions markerOptions = new MarkerOptions().position(
                        new LatLng(bar.getBarLocation().getLat(), bar.getBarLocation().getLng())).title(bar.getBarName()).snippet(bar.calculateDistance());
                // Changing marker icon
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                // adding marker

                Marker mMarker =googleMap.addMarker(markerOptions);
                mMarkers.add(mMarker);
            }
        }
    }


    public LatLng makeLatLngFromLocation(Location location) {
        LatLng latLng=null;
        if(location !=null) {
            latLng = new LatLng(location.getLat(), location.getLng());
        }
        return latLng;
    }


    public void refreshData(List<RealmBar> bars) {
        this.realmBars = bars;
        supportMapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

        }
        if (googleMap != null) {

            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            addMarkers(googleMap);

            if(cameraPositionLatLng!=null) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(cameraPositionLatLng).zoom(15.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                googleMap.moveCamera(cameraUpdate);
            }

        }

    }

    public void setCameraPosition(RealmBar realmBar,int position) {
        Marker marker =mMarkers.get(position);
        for(Marker markerCurrent:mMarkers){
            if(markerCurrent.isInfoWindowShown())
                markerCurrent.hideInfoWindow();
        }
        marker.showInfoWindow();
        cameraPositionLatLng = makeLatLngFromLocation(realmBar.getBarLocation());
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(FRAGMENT_MAP_PLACES, (ArrayList<? extends Parcelable>) realmBars);
        outState.putParcelable(FRAGMENT_CAMERA_POSITION,cameraPositionLatLng);
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){
            realmBars=savedInstanceState.getParcelableArrayList(FRAGMENT_MAP_PLACES);
            cameraPositionLatLng=savedInstanceState.getParcelable(FRAGMENT_CAMERA_POSITION);
            supportMapFragment.getMapAsync(this);
        }

    }
}
