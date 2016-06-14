package com.ylyubenova.projects.android.androidtask;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ylyubenova.projects.android.androidtask.model.Location;
import com.ylyubenova.projects.android.androidtask.model.RealmBar;

import java.util.List;

/**
 * Created by user on 5/5/16.
 */
public class BarsRVAdapter extends RecyclerView.Adapter<BarsRVAdapter.BarInfoViewHolder>{
    List<RealmBar> barsList;

    public BarsRVAdapter(List<RealmBar> barsList) {
        this.barsList = barsList;
    }

    @Override
    public int getItemCount() {
        if(barsList!=null)
        return barsList.size();
        else return 0;
    }
    public static class BarInfoViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView barName;
        TextView distanceTo;

        BarInfoViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            barName = (TextView)itemView.findViewById(R.id.bar_name);
            distanceTo = (TextView)itemView.findViewById(R.id.distance_to);

        }
    }

    @Override
    public BarInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bar_card_view, viewGroup, false);
        BarInfoViewHolder pvh = new BarInfoViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(BarInfoViewHolder holder, int position) {
        holder.barName.setText(barsList.get(position).getBarName());
        if (LocationTracker.getInstance().getLastLocation() != null) {
            holder.distanceTo.setText(barsList.get(position).calculateDistance(LocationTracker.getInstance().getLastLocation()));
        }

    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
