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

import androidsdk.devless.io.devless.interfaces.DeleteResponse;
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
    JSONArray jsonArray,followers;

    public FollowersAdapter(Context ctx, JSONObject jsonObject,JSONArray followers) {
        this.ctx = ctx;
        this.jsonObject = jsonObject;
        this.followers=followers;
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
            JSONObject followerObj=null;
            boolean isFollowing=false;
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

                   if (userObj.getJSONArray("userDetails").length()>0){
                       String img=userObj.getJSONArray("userDetails").getJSONObject(0).getString("user_image");
                       Glide.with(ctx)
                               .load(img)
                               .into(((FollowViewHolder) holder).profileImage);

                   }

                    //JSONArray followers=userObj.getJSONArray("following");
                    Log.d(TAG,"Followers length "+followers.length());
                    for (int i=0;i<followers.length(); i++){
                        Log.d(TAG,"Follower Id: "+followers.getJSONObject(i).getInt("follower_id")+" - UserObj "+GeneralFunctions.getUserId(ctx));
                        Log.d(TAG,"User Id: "+followers.getJSONObject(i).getInt("user_id")+" - "+userObj.getInt("id"));
                        if (followers.getJSONObject(i).getInt("follower_id")==GeneralFunctions.getUserId(ctx) &&
                                followers.getJSONObject(i).getInt("user_id")==userObj.getInt("id")){
                            followerObj=followers.getJSONObject(i);
                            isFollowing=true;
                            ((FollowViewHolder) holder).btnFollow.setBackground(ctx.getResources().getDrawable(R.drawable.followbtn));
                            ((FollowViewHolder) holder).btnFollow.setTextColor(ctx.getResources().getColor(R.color.white));
                            ((FollowViewHolder) holder).btnFollow.setText("Unfollow");
                        }
                    }
                    final JSONObject finalFollowerObj = followerObj;
                    ((FollowViewHolder) holder).btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           // App.devless.methodCall();

                            Map<String, Object> dataToChange = new HashMap<>();
                            dataToChange.put("follower_id", GeneralFunctions.getUser(ctx));
                            try {
                                dataToChange.put("user_id",userObj.getInt("id"));
                                dataToChange.put("follower_id",GeneralFunctions.getUserId(ctx));
                                dataToChange.put("accepted",0);
                                ((FollowViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                                ((FollowViewHolder) holder).btnFollow.setVisibility(View.GONE);
                                if (finalFollowerObj !=null){
                                    ((FollowViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                                    App.devless.delete("followers", "followers", finalFollowerObj.getString("id"), new DeleteResponse() {
                                        @Override
                                        public void onSuccess(ResponsePayload response) {
                                            Log.d(TAG,response.toString());
                                            ((FollowViewHolder) holder).progressBar.setVisibility(View.GONE);
                                            ((FollowViewHolder) holder).btnFollow.setBackground(ctx.getResources().getDrawable(R.drawable.unfollowbtn));
                                            ((FollowViewHolder) holder).btnFollow.setTextColor(ctx.getResources().getColor(R.color.black));
                                            ((FollowViewHolder) holder).btnFollow.setText("Follow");
                                            ((FollowViewHolder) holder).btnFollow.setVisibility(View.VISIBLE);

                                        }

                                        @Override
                                        public void onFailed(ErrorMessage errorMessage) {
                                            Log.d(TAG,errorMessage.toString());
                                            ((FollowViewHolder) holder).progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void userNotAuthenticated(ErrorMessage message) {
                                            Log.d(TAG,message.toString());
                                            ((FollowViewHolder) holder).progressBar.setVisibility(View.GONE);
                                        }
                                    });

                                }else{
                                    App.devless.postData("followers", "followers", dataToChange, new PostDataResponse() {
                                        @Override
                                        public void onSuccess(ResponsePayload response) {
                                            ((FollowViewHolder) holder).progressBar.setVisibility(View.GONE);
                                            ((FollowViewHolder) holder).btnFollow.setVisibility(View.VISIBLE);
                                            Log.d(TAG,response.toString());
                                            ((FollowViewHolder) holder).btnFollow.setBackground(ctx.getResources().getDrawable(R.drawable.followbtn));
                                            ((FollowViewHolder) holder).btnFollow.setTextColor(ctx.getResources().getColor(R.color.white));
                                            ((FollowViewHolder) holder).btnFollow.setText("Unfollow");
                                        }

                                        @Override
                                        public void onFailed(ErrorMessage errorMessage) {
                                            ((FollowViewHolder) holder).progressBar.setVisibility(View.GONE);
                                            ((FollowViewHolder) holder).btnFollow.setVisibility(View.VISIBLE);
                                            Log.d(TAG,errorMessage.toString());
                                        }

                                        @Override
                                        public void userNotAuthenticated(ErrorMessage message) {
                                            ((FollowViewHolder) holder).progressBar.setVisibility(View.GONE);
                                            ((FollowViewHolder) holder).btnFollow.setVisibility(View.VISIBLE);
                                            Log.d(TAG,message.toString());
                                        }
                                    });
                                }

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
