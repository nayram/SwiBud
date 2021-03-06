package com.dev.swibud.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.activities.ActivityEditMeetup;
import com.dev.swibud.activities.ActivityGuests;
import com.dev.swibud.activities.GuestStatusActivity;
import com.dev.swibud.fragments.MeetupFragment;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.viewholders.MeetupViewHolder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidsdk.devless.io.devless.interfaces.DeleteResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 10/8/17.
 */

public class MeetupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    JSONArray jsonArray;
    MeetupFragment mFragment;
    String TAG=getClass().getName();

    public MeetupAdapter(MeetupFragment mFragment, JSONArray jsonArray) {
        this.mFragment=mFragment;
        this.context = mFragment.getContext();
        this.jsonArray = jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.llmeetup_layout, parent, false);
        MeetupViewHolder viewHolder= new MeetupViewHolder(view);
        viewHolder.mapViewListItemViewOnCreate(null);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MeetupViewHolder){
            try {
                final JSONObject jsonObject=jsonArray.getJSONObject(position);
                final MeetupViewHolder meetupViewHolder = (MeetupViewHolder) holder;
                meetupViewHolder.mapViewListItemViewOnResume();
                meetupViewHolder.mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        try {
                            if (jsonObject.get("latitude") !=null){
                                LatLng postion = new LatLng(jsonObject.getDouble("latitude"),
                                        jsonObject.getDouble("longitude"));
                                mMap.addMarker(new MarkerOptions().position(postion)
                                        .title(jsonObject.getString("location")));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(jsonObject.getDouble("latitude"),
                                                jsonObject.getDouble("longitude")), 14));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                meetupViewHolder.tvMeetup.setText(jsonObject.getString("name"));
                meetupViewHolder.tvLocation.setText(jsonObject.getString("location"));
                meetupViewHolder.tvDate.setText(jsonObject.getString("date")+" "+jsonObject.getString("time"));

                if (jsonObject.get("description") !=null && !jsonObject.getString("description").equalsIgnoreCase("null"))
                meetupViewHolder.tvDesc.setText(jsonObject.getString("description"));
                meetupViewHolder.llParticipants.removeAllViews();

                for (int i=0;i<jsonObject.getJSONArray("participants").length();i++){
                    JSONObject participant=jsonObject.getJSONArray("participants").getJSONObject(i);
                    View childLayout=LayoutInflater.from(meetupViewHolder.llParticipants.getContext())
                            .inflate(R.layout.participant_contents_layout,meetupViewHolder.llParticipants,false);

                    CircleImageView imageView= (CircleImageView)childLayout.findViewById(R.id.img_participant);
                    if (participant.getJSONArray("extraDetail").length()>0){
                        String img=participant.getJSONArray("extraDetail").getJSONObject(0).getString("user_image");
                        Glide.with(meetupViewHolder.llParticipants.getContext())
                                .load(img)
                                .into(imageView);
                    }

                    TextView tvProfName=(TextView)childLayout.findViewById(R.id.tv_participant_name);
                    if (participant.getJSONArray("profile").length()>0){
                        String name=participant.getJSONArray("profile").getJSONObject(0).getString("first_name")+" "+ participant.getJSONArray("profile").getJSONObject(0).getString("last_name");
                        tvProfName.setText(name);
                    }

                    meetupViewHolder.llParticipants.addView(childLayout);
                }

                meetupViewHolder.llParticipants.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      try {

                            if (GeneralFunctions.getUserId()==jsonObject.getInt("users_id")){
                                ActivityGuests.jsonArray=jsonObject.getJSONArray("participants");

                                Intent intent=new Intent(((MeetupViewHolder) holder).llParticipants.getContext(),ActivityGuests.class);

                                Bundle bundle=new Bundle();
                                bundle.putInt("meetup_id",jsonObject.getInt("id"));

                                intent.putExtras(bundle);
                                mFragment.startActivityForResult(intent,120);
                            }else{
                                GuestStatusActivity.meetup_json=jsonObject;
                                Intent intent=new Intent(((MeetupViewHolder) holder).llParticipants.getContext(),GuestStatusActivity.class);

                                mFragment.startActivityForResult(intent,120);
                            }


                       } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                meetupViewHolder.img_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            PopupMenu popupMenu=new PopupMenu(meetupViewHolder.img_more.getContext(),meetupViewHolder.img_more);
                            MenuInflater inflater = popupMenu.getMenuInflater();
                            if (GeneralFunctions.getUserId()==jsonObject.getInt("users_id"))
                                inflater.inflate(R.menu.meetup_options, popupMenu.getMenu());
                            else inflater.inflate(R.menu.meetup_participants_options, popupMenu.getMenu());

                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()){
                                        case R.id.mn_edit:
                                            ActivityEditMeetup.jobj=jsonObject;
                                            Intent intent=new Intent(context,ActivityEditMeetup.class);
                                            mFragment.startActivityForResult(intent,300);
                                            break;
                                        case R.id.mn_view_details:
                                            try {
                                                if (GeneralFunctions.getUserId()==jsonObject.getInt("users_id")){
                                                    ActivityGuests.jsonArray=jsonObject.getJSONArray("participants");

                                                    Intent intent1=new Intent(((MeetupViewHolder) holder).llParticipants.getContext(),ActivityGuests.class);

                                                    Bundle bundle=new Bundle();
                                                    bundle.putInt("meetup_id",jsonObject.getInt("id"));

                                                    intent1.putExtras(bundle);
                                                    mFragment.startActivityForResult(intent1,120);
                                                }else {
                                                    GuestStatusActivity.meetup_json=jsonObject;
                                                    Intent intent2=new Intent(((MeetupViewHolder) holder).llParticipants.getContext(),GuestStatusActivity.class);

                                                    mFragment.startActivityForResult(intent2,120);
                                                }


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case R.id.mn_delete:

                                                AlertDialog.Builder alert=new AlertDialog.Builder(context)
                                                        .setTitle("Delete Meetup")
                                                        .setMessage("You are about to delete a meetup. Do you want to continue?")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                try {
                                                                    deleteMeetup(jsonObject.getInt("id"),position);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        })
                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                            }
                                                        });
                                                alert.show();
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }

                    }
                });

                meetupViewHolder.foldingCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        meetupViewHolder.foldingCell.toggle(false);
                    }
                });

               // meetupViewHolder.tvDesc.setText();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    void deleteMeetup(int meetupId, final int position){
        mFragment.progress_bar.setVisibility(View.VISIBLE);
        App.devless.delete("meetups", "meetup", String.valueOf(meetupId),
                new DeleteResponse() {
                    @Override
                    public void onSuccess(ResponsePayload response) {
                        mFragment.progress_bar.setVisibility(View.GONE);
                        try {
                            JSONObject obj=new JSONObject(response.toString());
                            if (obj.getInt("status_code")==636){
                                jsonArray.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Meetup has been deleted successfully.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(ErrorMessage errorMessage) {
                        mFragment.progress_bar.setVisibility(View.GONE);
                        Log.d(TAG,errorMessage.toString());
                    }

                    @Override
                    public void userNotAuthenticated(ErrorMessage message) {
                        mFragment.progress_bar.setVisibility(View.GONE);
                        Log.d(TAG,message.toString());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
}
