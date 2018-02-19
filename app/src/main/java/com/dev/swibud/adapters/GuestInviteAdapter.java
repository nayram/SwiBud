package com.dev.swibud.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.activities.AllUsers;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.viewholders.GuestInviteViewHolder;
import com.dev.swibud.viewholders.GuestViewHolder;
import com.dev.swibud.viewholders.InviteContactViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.DeleteResponse;
import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nayrammensah on 1/23/18.
 */

public class GuestInviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    JSONArray jsonArray,guests;
    AllUsers allUsers;
    int meetup_id;
    String TAG=getClass().getName();


    public GuestInviteAdapter(AllUsers allUsers , JSONArray jsonArray, JSONArray guests,int meetup_id) {
        this.context = allUsers.getApplicationContext();
        this.jsonArray = jsonArray;
        this.guests=guests;
        this.meetup_id=meetup_id;
        this.allUsers=allUsers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_guest_item, parent, false);
        GuestInviteViewHolder viewHolder= new GuestInviteViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
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

                                try {
                                    if (b)
                                    addGuest(userObj,((GuestInviteViewHolder) holder));
                                    else removeGuest(userObj.getInt("id"),((GuestInviteViewHolder) holder));
                                }catch (JSONException ex){
                                    ex.printStackTrace();
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

    void addGuest(final JSONObject userObj,final GuestInviteViewHolder holder) throws JSONException {

        Map<String, Object> dataToPost = new HashMap<>();

        dataToPost.put("meetups_meetup_id", meetup_id);
        dataToPost.put("users_id", userObj.getInt("id"));
        dataToPost.put("accepted",0);
        dataToPost.put("completed",0);



        allUsers.showProgressBar(true);

        App.devless.postData(Constants.MeetUpService, Constants.Invites, dataToPost, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {

                Log.d(TAG,response.toString());
                try {
                    JSONObject object=new JSONObject(response.toString());
                    if (object.getInt("status_code")==609){
                        allUsers.setCallbackResult(1102);
                        Toast.makeText(context, "Guest added successfully", Toast.LENGTH_SHORT).show();
                        if (userObj.getJSONArray("userDetails").length()>0)
                            if (userObj.getJSONArray("userDetails").getJSONObject(0).has("fcm_token")
                                    && !userObj.getJSONArray("userDetails").getJSONObject(0)
                                    .getString("fcm_token").equalsIgnoreCase("null")) {
                                String token = userObj.getJSONArray("userDetails")
                                        .getJSONObject(0).getString("fcm_token");
                                String title="Meet Up Invitation";
                                String message="You've been invited to a meet up";
                                notifyUser(title,message,token);
                            }else {
                                allUsers.showProgressBar(false);
                            }
                        else allUsers.showProgressBar(false);

                    }else {
                        allUsers.showProgressBar(false);
                    }

                } catch (JSONException e) {
                    allUsers.showProgressBar(false);
                    holder.tog_invite_status.setChecked(false);
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to add guest", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                holder.tog_invite_status.setChecked(false);
                allUsers.showProgressBar(false);
                Log.d(TAG,errorMessage.toString());
                Toast.makeText(context, "Failed to add guest", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                holder.tog_invite_status.setChecked(false);
                allUsers.showProgressBar(false);
                Log.d(TAG,message.toString());
                Toast.makeText(context, "Failed to add guest", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyUser(String title, String message, String token) {
        App.messagingService.sendFCMNotificationMeetupInvite(Constants.MEETUP_INVITATION,title,
                message,token,String.valueOf(meetup_id)).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                allUsers.showProgressBar(false);
                Log.d(TAG,response.toString());
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                allUsers.showProgressBar(false);
                t.printStackTrace();

            }
        });
    }

    void removeGuest(int id, final GuestInviteViewHolder holder){

        allUsers.showProgressBar(true);

        Map<String, Object> params = new HashMap<>();
        params.put("where", "meetups_meetup_id,"+String.valueOf(meetup_id));
        params.put("where", "users_id,"+String.valueOf(id));


        App.devless.search("DeleteInvite", "tbl_participant", params, new SearchResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                allUsers.showProgressBar(false);
                Log.d(TAG,response.toString());
                try {
                    JSONObject object=new JSONObject(response.toString());
                    if (object.getInt("status_code")==1001){
                        allUsers.setCallbackResult(1102);
                        Toast.makeText(context, "Guest was removed successfully", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    holder.tog_invite_status.setChecked(true);
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to remove guest", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void userNotAuthenticated(ErrorMessage errorMessage) {
                holder.tog_invite_status.setChecked(true);
                allUsers.showProgressBar(false);
                Log.d(TAG,errorMessage.toString());
                Toast.makeText(context, "Failed to remove guest from the guest list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
