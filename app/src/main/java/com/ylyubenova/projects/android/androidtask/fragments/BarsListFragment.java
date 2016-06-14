package com.ylyubenova.projects.android.androidtask.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ylyubenova.projects.android.androidtask.BarsRVAdapter;
import com.ylyubenova.projects.android.androidtask.R;
import com.ylyubenova.projects.android.androidtask.RecyclerItemClickListener;
import com.ylyubenova.projects.android.androidtask.model.Location;
import com.ylyubenova.projects.android.androidtask.model.RealmBar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/5/16.
 */
public class BarsListFragment extends Fragment {
    public static int FRAGMENT_ID=55678;
    public static final String FRAGMENT_BARS_LIST="fragment_bar_list";
    List<RealmBar> bars;
    RecyclerView recyclerView;
    OnBarSelectedListener selectionListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycleView);
        if(savedInstanceState!=null){
            bars=savedInstanceState.getParcelableArrayList(FRAGMENT_BARS_LIST);
        }
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        BarsRVAdapter adapter = new BarsRVAdapter(bars);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        selectionListener.onBarPlaceSelected(position,FRAGMENT_ID);

                    }
                })
        );
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void refreshData(List<RealmBar> bars){
        this.bars=bars;
        BarsRVAdapter adapter = new BarsRVAdapter(this.bars);
        if(recyclerView!=null)
        recyclerView.setAdapter(adapter);

    }
    public void showToast(int position){

        Toast.makeText(getActivity(),""+bars.get(position).getBarName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            selectionListener = (OnBarSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnBarSelectedListener");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(FRAGMENT_BARS_LIST, (ArrayList<? extends Parcelable>) bars);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){
            bars=savedInstanceState.getParcelableArrayList(FRAGMENT_BARS_LIST);
        }
    }
}
