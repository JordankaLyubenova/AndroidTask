package com.ylyubenova.projects.android.androidtask;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ylyubenova.projects.android.androidtask.fragments.BarsListFragment;
import com.ylyubenova.projects.android.androidtask.fragments.CustomMapFragment;
import com.ylyubenova.projects.android.androidtask.fragments.PlaceholderFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private CustomMapFragment customMapFragment;
    private BarsListFragment barsListFragment;
    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public CustomMapFragment getCustomMapFragmentIfCreated() {
        return this.customMapFragment;
    }

    public BarsListFragment getBarsListFragmentIfCreated() {
        return this.barsListFragment;
    }


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            this.customMapFragment = new CustomMapFragment();
            return this.customMapFragment;
        }
        if (position == 0) {
            this.barsListFragment = new BarsListFragment();
            /*if (realmBars != null) {
                this.barsListFragment.refreshData(realmBars);
            }*/
            return this.barsListFragment;
        }
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.tab_name_places);
            case 1:
                return context.getResources().getString(R.string.tab_name_map);

        }
        return null;
    }
}