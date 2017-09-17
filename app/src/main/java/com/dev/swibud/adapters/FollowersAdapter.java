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
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.viewholders.FollowViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;

/**
 * Created by nayrammensah on 9/4/17.
 */

public class FollowersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context ctx;
    JSONObject jsonObject;
    String TAG=getClass().getName();
    JSONArray jsonArray;

    public FollowersAdapter(Context ctx, JSONObject jsonObject) {
        this.ctx = ctx;
        this.jsonObject = jsonObject;
        try {
            this.jsonArray=jsonObject.getJSONArray("payload");
        }catch (JSONException exception){
            exception.printStackTrace();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.contact_viewholder, parent, false);

        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FollowViewHolder){
            try {
                final JSONObject userObj=jsonArray.getJSONObject(position);
                if (userObj!=null){
                   /* Glide.with(ctx)
                            .load()
                    ((FollowViewHolder) holder).profileImage*/
                   if (!userObj.getString("first_name").equals("null"))
                   ((FollowViewHolder) holder).tvName.setText(userObj.getString("first_name")+" "+userObj.getString("last_name"));
                    else{
                       ((FollowViewHolder) holder).tvName.setText(userObj.getString("username"));
                   }
                    ((FollowViewHolder) holder).btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           // App.devless.methodCall();

                            Map<String, Object> dataToChange = new HashMap<>();
                            dataToChange.put("follower_id", GeneralFunctions.getUser(ctx));
                            try {
                                dataToChange.put("user_id",userObj.getInt("id"));
                                App.devless.postData("followers", "followers", dataToChange, new PostDataResponse() {
                                    @Override
                                    public void onSuccess(ResponsePayload response) {
                                        Log.d(TAG,response.toString());
                                        ((FollowViewHolder) holder).btnFollow.setBackground(ctx.getResources().getDrawable(R.drawable.followbtn));
                                    }

                                    @Override
                                    public void onFailed(ErrorMessage errorMessage) {
                                        Log.d(TAG,errorMessage.toString());
                                    }

                                    @Override
                                    public void userNotAuthenticated(ErrorMessage message) {
                                        Log.d(TAG,message.toString());
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
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
