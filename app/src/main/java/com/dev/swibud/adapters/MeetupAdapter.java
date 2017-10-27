package com.dev.swibud.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.viewholders.MeetupViewHolder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 10/8/17.
 */

public class MeetupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    JSONArray jsonArray;

    public MeetupAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
}
