package com.dev.swibud.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.viewholders.GuestInviteViewHolder;
import com.dev.swibud.viewholders.GuestViewHolder;
import com.dev.swibud.viewholders.InviteContactViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nayrammensah on 1/23/18.
 */

public class GuestInviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    JSONArray jsonArray,guests;

    public GuestInviteAdapter(Context context ,JSONArray jsonArray, JSONArray guests) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.guests=guests;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_guest_item, parent, false);
        GuestInviteViewHolder viewHolder= new GuestInviteViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GuestInviteViewHolder){
            try {
                final JSONObject userObj=jsonArray.getJSONObject(position);
                if (userObj !=null){
                    if (!userObj.getString("first_name").equals("null"))
                        ((GuestInviteViewHolder) holder).setName(userObj.getString("first_name")+" "+userObj.getString("last_name"));
                    else{
                        ((GuestInviteViewHolder) holder).setName(userObj.getString("first_name")+" "+userObj.getString("last_name"));
                    }

                    if (userObj.getJSONArray("userDetails").length()>0){
                        String img=userObj.getJSONArray("userDetails").getJSONObject(0).getString("user_image");
                        ((GuestInviteViewHolder) holder).setGuestImage(img);
                    }

                    if (isGuest(userObj.getInt("id"))){
                        ((GuestInviteViewHolder) holder).tog_invite_status.setChecked(true);
                    }

                    ((GuestInviteViewHolder) holder).tog_invite_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            if (b){

                            }
                        }
                    });

                }

            }catch (JSONException ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    boolean isGuest(int id){
        boolean result =false;
        for (int i=0;i<guests.length();i++){
            try {
                if (guests.getJSONObject(i).getInt("users_id")==id){
                    result=true;
                    return result;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
