package com.dev.swibud.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.activities.CreateMeetupActivity;
import com.dev.swibud.viewholders.FollowViewHolder;
import com.dev.swibud.viewholders.InviteContactViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by nayrammensah on 10/4/17.
 */

public class InviteContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context ctx;
    JSONObject jsonObject;
    String TAG=getClass().getName();
    JSONArray jsonArray;
    CreateMeetupActivity activity;
    Map<String,JSONObject> objectMap;

    public InviteContactAdapter(CreateMeetupActivity activity, JSONArray jsonArray, Map<String, JSONObject> objectMap) {
        this.activity=activity;
        this.ctx = activity.getApplicationContext();
        this.jsonArray = jsonArray;
        this.objectMap=objectMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.layout_invites, parent, false);

        return new InviteContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InviteContactViewHolder){
            try {
                final JSONObject userObj=jsonArray.getJSONObject(position);
                if (userObj !=null){
                    if (!userObj.getString("first_name").equals("null"))
                        ((InviteContactViewHolder) holder).tvName.setText(userObj.getString("first_name")+" "+userObj.getString("last_name"));
                    else{
                        ((InviteContactViewHolder) holder).tvName.setText(userObj.getString("username"));
                    }

                    if (userObj.getJSONArray("userDetails").length()>0){
                        String img=userObj.getJSONArray("userDetails").getJSONObject(0).getString("user_image");
                        Glide.with(ctx)
                                .load(img)
                                .into(((InviteContactViewHolder) holder).profileImage);

                    }

                    if (objectMap.containsKey(userObj.getInt("id"))){
                        ((InviteContactViewHolder) holder).invCheckbox.setChecked(true);
                    }

                    ((InviteContactViewHolder) holder).invCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked){
                                try {
                                    activity.addSelectedContact(userObj.getInt("id"),userObj);
                                }catch (JSONException ex){
                                    ex.printStackTrace();
                                    buttonView.setChecked(false);

                                }
                            }else{
                                try {
                                    activity.removeSelectedContact(userObj.getInt("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    buttonView.setChecked(true);
                                }
                            }
                        }
                    });
                }
            }catch (JSONException ex){

            }
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
}
