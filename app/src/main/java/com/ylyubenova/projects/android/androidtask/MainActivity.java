package com.ylyubenova.projects.android.androidtask;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.ylyubenova.projects.android.androidtask.fragments.BarsListFragment;
import com.ylyubenova.projects.android.androidtask.fragments.CustomMapFragment;
import com.ylyubenova.projects.android.androidtask.fragments.OnBarSelectedListener;
import com.ylyubenova.projects.android.androidtask.model.Bars;
import com.ylyubenova.projects.android.androidtask.model.Location;
import com.ylyubenova.projects.android.androidtask.model.RealmBar;
import com.ylyubenova.projects.android.androidtask.model.Result;
import com.ylyubenova.projects.android.androidtask.rest.GooglePlacesAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnBarSelectedListener, LocationTracker.LocationTrackerListner {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public ArrayList<RealmBar> realmBars;
    LocationTracker locationTracker;
    private ProgressBar progressBar;

    private GoogleApiClient client;

    public static final String ACTIVITY_REALM_BARS="activity_realm_bars";
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realmBars = new ArrayList<RealmBar>();
        if (savedInstanceState != null) {
            realmBars = savedInstanceState.getParcelableArrayList(ACTIVITY_REALM_BARS);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationTracker = LocationTracker.getInstance();
        locationTracker.setGlobalContext(this.getApplicationContext());
        locationTracker.setLocationTrackerListner(this);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        String[] permissions = {"android.permission.ACCESS_COARSE_LOCATION"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(permissions, 100);
        } else {
            if (client == null) {
                // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                client = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(locationTracker)
                        .addOnConnectionFailedListener(locationTracker)
                        .addApi(LocationServices.API).build();
                locationTracker.client = client;
            }
            if(realmBars==null)
                getBarsFromGoogle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (client == null) {
                client = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(locationTracker)
                        .addOnConnectionFailedListener(locationTracker)
                        .addApi(LocationServices.API)
                        .build();
                locationTracker.client = client;
                client.connect();
            }
            getBarsFromGoogle();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (client != null) {
            client.connect();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (client != null) {
            client.disconnect();
        }
    }

    @Override
    public void onBarPlaceSelected(int position, int fragmentID) {
        if (fragmentID == BarsListFragment.FRAGMENT_ID) {
            mViewPager.setCurrentItem(1, true);
            SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager.getAdapter();
            CustomMapFragment mapFragment = adapter.getCustomMapFragmentIfCreated();
            if (mapFragment != null) {
                mapFragment.setCameraPosition(realmBars.get(position),position);
            }
        }

    }

    @Override
    public void onLocationUpdate(android.location.Location location) {
        getBarsFromGoogle();
    }


    public void getBarsFromGoogle() {
        // if(LocationTracker.getInstance().getLastLocation()==null) return;
        progressBar.setVisibility(View.VISIBLE);
        String location = locationTracker.getLocationString() + "\n";
        GooglePlacesAPI.Factory.getInstance().getNearestBars(location).enqueue(new Callback<Bars>() {
            @Override
            public void onResponse(Call<Bars> call, Response<Bars> response) {
                parseBarsFromResponse(response);
                refreshFragments();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Bars> call, Throwable t) {
                Log.e("Retrofit", "Failed");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void parseBarsFromResponse(Response<Bars> response) {
        Log.d("On Retrofit responese", "" + response.body().getStatus() + response.body().getHtmlAttributions());
        List<Result> results = response.body().getResults();
        realmBars= new ArrayList<RealmBar>();
        for (Result result : results) {
            String name = result.getName();
            Location location = result.getGeometry().getLocation();
            Log.d("Result:", "" + name + "location:" + result.getGeometry().getLocation().getLat() + "," + result.getGeometry().getLocation().getLng());
            realmBars.add(new RealmBar(name, location));
        }
    }

    public void refreshFragments() {
        SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager.getAdapter();
        if (adapter != null) {
            BarsListFragment barsListFragment = adapter.getBarsListFragmentIfCreated();
            if (barsListFragment != null) {
                barsListFragment.refreshData(realmBars);
            }
            CustomMapFragment mapFragment = adapter.getCustomMapFragmentIfCreated();
            if (mapFragment != null) {
                mapFragment.refreshData(realmBars);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ACTIVITY_REALM_BARS,realmBars);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            realmBars = savedInstanceState.getParcelableArrayList(ACTIVITY_REALM_BARS);
        }

    }
}
