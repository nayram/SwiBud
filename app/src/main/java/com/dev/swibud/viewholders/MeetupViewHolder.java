package com.dev.swibud.viewholders;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.swibud.R;
import com.google.android.gms.maps.MapView;
import com.ramotion.foldingcell.FoldingCell;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 10/8/17.
 */

public class MeetupViewHolder extends RecyclerView.ViewHolder {

    public TextView tvMeetup,tvLocation,tvDate,tvDesc;
    public MapView mMapView;
    public FoldingCell foldingCell;
    public LinearLayout llParticipants;

    public MeetupViewHolder(View itemView) {
        super(itemView);
        tvMeetup=(TextView)itemView.findViewById(R.id.tvMeetup);
        tvLocation=(TextView)itemView.findViewById(R.id.tvLocation);
        tvDate=(TextView)itemView.findViewById(R.id.tvDate);
        tvDesc=(TextView)itemView.findViewById(R.id.tvDesc);
        mMapView=(MapView)itemView.findViewById(R.id.list_item_map_view_mapview);
        foldingCell=(FoldingCell)itemView.findViewById(R.id.folding_cell);
        llParticipants=(LinearLayout)itemView.findViewById(R.id.llParticipants);
    }

    public void mapViewListItemViewOnCreate(Bundle savedInstanceState) {
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
        }
    }

    public void mapViewListItemViewOnResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    public void mapViewListItemViewOnPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    public void mapViewListItemViewOnDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    public void mapViewListItemViewOnLowMemory() {
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    public void mapViewListItemViewOnSaveInstanceState(Bundle outState) {
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }
}
