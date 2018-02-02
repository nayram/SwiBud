package com.dev.swibud.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.utils.App;
import com.dev.swibud.viewholders.GuestViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidsdk.devless.io.devless.interfaces.DeleteResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;

/**
 * Created by nayrammensah on 1/21/18.
 */

public class GuestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    JSONArray guestArray;
    Context context;
    String TAG=getClass().getName();

    public GuestAdapter(JSONArray guestArray, Context context) {
        this.guestArray = guestArray;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_guest_item, parent, false);
        GuestViewHolder viewHolder= new GuestViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GuestViewHolder){
            try {
               final JSONObject obj=guestArray.getJSONObject(position);
                if (obj.getJSONArray("extraDetail").length()>0){
                    String img=obj.getJSONArray("extraDetail").getJSONObject(0).getString("user_image");
                    ((GuestViewHolder) holder).setGuestImage(img);
                }

                if (obj.getJSONArray("profile").length()>0){
                    String name=obj.getJSONArray("profile").getJSONObject(0).getString("first_name")+" "+ obj.getJSONArray("profile").getJSONObject(0).getString("last_name");
                    ((GuestViewHolder) holder).setName(name);
                    String username=obj.getJSONArray("profile").getJSONObject(0).getString("username");
                    ((GuestViewHolder) holder).setUsername(username);
                }

                ((GuestViewHolder) holder).btnRemovePerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ((GuestViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                            App.devless.delete("meetup", "invite", obj.getString("id"), new DeleteResponse() {
                                @Override
                                public void onSuccess(ResponsePayload response) {
                                    ((GuestViewHolder) holder).progressBar.setVisibility(View.GONE);
                                    Log.d(TAG,response.toString());

                                }

                                @Override
                                public void onFailed(ErrorMessage errorMessage) {
                                    ((GuestViewHolder) holder).progressBar.setVisibility(View.GONE);
                                    Log.d(TAG,errorMessage.toString());
                                }

                                @Override
                                public void userNotAuthenticated(ErrorMessage message) {
                                    ((GuestViewHolder) holder).progressBar.setVisibility(View.GONE);
                                    Log.d(TAG,message.toString());

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }catch (JSONException ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return guestArray.length();
    }

    void removeElemet(int position){
        guestArray.remove(position);
        notifyDataSetChanged();
    }

    public void addData(JSONArray payload) {
        guestArray=payload;
        notifyDataSetChanged();
    }
}
