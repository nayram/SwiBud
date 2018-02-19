package com.dev.swibud.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.dev.swibud.R;
import com.dev.swibud.adapters.GuestAdapter;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.DividerItemDecoration;
import com.dev.swibud.utils.GeneralFunctions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.EditDataResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nayrammensah on 1/30/18.
 */

public class GuestStatusActivity extends BaseActivity {

    @BindView(R.id.appBar)
    AppBarLayout appBarLayout;

    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.guest_tool_bar)
    Toolbar toolbar;

    @BindView(R.id.recycGuest)
    RecyclerView recycGuest;

    @BindView(R.id.meetup_location_map_view)
    MapView map;


    @BindView(R.id.tvMeetupLocation)
    TextView tvMeetupLocation;

    @BindView(R.id.tvMeetupDesc)
    TextView tvMeetupDesc;

    @BindView(R.id.progress_bar_profile)
    ProgressBar progressBar;

    public static JSONObject meetup_json;
    JSONObject participant_obj;
    JSONArray participant_array;
    GuestAdapter adapter;

    String TAG=getClass().getName();
    int status;

    String[]options={
            "Accept",
            "Decline",
            "Maybe"
    };


    @BindView(R.id.btnInvStatus)
    Button btnInvStatus;

    @OnClick(R.id.btnInvStatus) void invitationStatus(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("INVITATION : RSVP");
        alert.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        status=2;
                        break;
                    case 1:
                        status=3;
                        break;
                    case 2:
                        status=1;
                        break;
                }

                updateInvitationStatus(status);
            }
        });

        alert.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout_accept_invite);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        collapsingToolbarLayout.setTitle(" ");
        setTitle(" ");
        mapViewListItemViewOnCreate(savedInstanceState);

        try{
            mapViewListItemViewOnResume();
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    try {
                        if (meetup_json.get("latitude") != null){
                            LatLng postion = new LatLng(meetup_json.getDouble("latitude"),
                                    meetup_json.getDouble("longitude"));
                            googleMap.addMarker(new MarkerOptions().position(postion)
                                    .title(meetup_json.getString("location")));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(meetup_json.getDouble("latitude"),
                                            meetup_json.getDouble("longitude")), 14));
                        }

                    }catch (JSONException ex){
                            ex.printStackTrace();
                    }

                }
            });


            participant_array=meetup_json.getJSONArray("participants");

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getResources().getDrawable(R.drawable.divider));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

            recycGuest.setLayoutManager(linearLayoutManager);
            recycGuest.addItemDecoration(dividerItemDecoration);

            participants();

            adapter=new GuestAdapter(participant_array,this,false);
            recycGuest.setAdapter(adapter);

            tvMeetupLocation.setText(meetup_json.getString("location"));
            tvMeetupDesc.setText(meetup_json.getString("description"));

            switch (participant_obj.getInt("accepted")){
                case 0:
                    btnInvStatus.setText("Pending");
                    break;
                case 1:
                    btnInvStatus.setText("Maybe");
                    break;
                case 2:
                    btnInvStatus.setText("Accepted");
                    break;
                case 3:
                    btnInvStatus.setText("Declined");
                    break;

            }



        }catch (JSONException ex){
            ex.printStackTrace();
        }


//        Log.d(TAG,"Participant id"+participantsId+"");


    }

    void  participants(){
        for (int i=0;i<participant_array.length();i++){
            try {
                JSONObject obj=participant_array.getJSONObject(i);
                if (GeneralFunctions.getUserId() == obj.getInt("users_id")){
                    participant_obj=obj;
                    participant_array.remove(i);
                }
            }catch (JSONException ex){
                ex.printStackTrace();
            }


        }
    }

    public void mapViewListItemViewOnCreate(Bundle savedInstanceState) {
        if (map != null) {
            map.onCreate(savedInstanceState);
        }
    }

    public void mapViewListItemViewOnResume() {
        if (map != null) {
            map.onResume();
        }
    }

    public void mapViewListItemViewOnPause() {
        if (map != null) {
            map.onPause();
        }
    }

    public void mapViewListItemViewOnDestroy() {
        if (map != null) {
            map.onDestroy();
        }
    }

    public void mapViewListItemViewOnLowMemory() {
        if (map != null) {
            map.onLowMemory();
        }
    }

    public void mapViewListItemViewOnSaveInstanceState(Bundle outState) {
        if (map != null) {
            map.onSaveInstanceState(outState);
        }
    }

    void updateInvitationStatus(final int status){
        showProgressBar(true);

        Map<String, Object> params = new HashMap<>();
        params.put("accepted",status);

        try {
            App.devless.edit("meetups", "invites", params, String.valueOf(participant_obj.getInt("id")),
                    new EditDataResponse() {
                        @Override
                        public void onSuccess(ResponsePayload response) {
                            showProgressBar(false);

                            switch (status){
                                case 0:
                                    btnInvStatus.setText("Pending");
                                    break;
                                case 1:
                                    btnInvStatus.setText("Maybe");
                                    break;
                                case 2:
                                    btnInvStatus.setText("Accepted");
                                    break;
                                case 3:
                                    btnInvStatus.setText("Declined");
                                    break;

                            }

                            setResult(RESULT_OK);
                        }

                        @Override
                        public void onFailed(ErrorMessage errorMessage) {
                            showProgressBar(false);
                            Log.d(TAG,errorMessage.toString());
                            Toast.makeText(GuestStatusActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void userNotAuthenticated(ErrorMessage message) {
                            showProgressBar(false);
                            Log.d(TAG,message.toString());
                            Toast.makeText(GuestStatusActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (JSONException ex){
            ex.printStackTrace();
        }

    }

    void showProgressBar(boolean action){
        if (action){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }
}
