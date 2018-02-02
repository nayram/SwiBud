package com.dev.swibud.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toolbar;

import com.dev.swibud.R;
import com.google.android.gms.maps.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nayrammensah on 1/30/18.
 */

public class GuestStatusActivity extends BaseActivity {

    @BindView(R.id.appBar)
    AppBarLayout appBarLayout;

    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycGuest)
    RecyclerView recycGuest;

    @BindView(R.id.meetup_map_view)
    MapView meetup_map_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout_accept_invite);
        ButterKnife.bind(this);


    }
}
