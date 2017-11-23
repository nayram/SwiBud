package com.dev.swibud.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.fragments.FriendsListFragment;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.viewholders.ContactViewHolder;
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
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 10/31/17.
 */

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context ctx;
    String TAG=getClass().getName();
    JSONArray jsonArray;
    FriendsListFragment flf;


    public ContactAdapter(Context ctx, JSONArray jsonArray,FriendsListFragment flf) {
        this.ctx = ctx;
        this.jsonArray = jsonArray;
        this.flf=flf;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.layout_contacts, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContactViewHolder){
            final boolean[] isFollowing = {false};
            JSONObject followObj=null;
            try {
                final JSONObject userObj=jsonArray.getJSONObject(position);
                if (userObj !=null){
                    if (!userObj.getString("first_name").equals("null"))
                        ((ContactViewHolder) holder).setContactName(userObj.getString("first_name")+" "+userObj.getString("last_name"));
                    else{
                        ((ContactViewHolder) holder).setContactName(userObj.getString("username"));
                    }

                    if (userObj.getJSONArray("userDetails").length()>0){
                        String img=userObj.getJSONArray("userDetails").getJSONObject(0).getString("user_image");
                        ((ContactViewHolder) holder).setImage(img);

                    }else ((ContactViewHolder) holder).setDummyImage(ctx);
                    JSONArray followers=userObj.getJSONArray(Constants.FOLLOWERS);

                    if (followers !=null){
                        Log.d(TAG,"Followers "+followers.length());
                        for (int i=0;i<followers.length(); i++){
                            Log.d(TAG,followers.getJSONObject(i).getInt("follower_id")+" - " +GeneralFunctions.getUserId(ctx));
                            if (followers.getJSONObject(i).getInt("follower_id")==GeneralFunctions.getUserId(ctx) ){
                                Log.d(TAG,"IS FOLLOWER");
                                followObj=followers.getJSONObject(i);
                                isFollowing[0] =true;
                                ((ContactViewHolder) holder).setIndicator(true);
                            }else{
//                                followObj=followers.getJSONObject(i);
                                isFollowing[0] =false;
                                ((ContactViewHolder) holder).setIndicator(false);
                            }
                        }
                    }else {
                        isFollowing[0] =false;
                        ((ContactViewHolder) holder).setIndicator(false);
                    }


                    final JSONObject finalFollowObj = followObj;
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        Log.d(TAG,"LAUNCH BOTTOM SHEET");
                            flf.showBottomSheet(userObj,isFollowing[0],finalFollowObj,position);

                        }
                    });

                }else{
                    isFollowing[0] =false;
                    ((ContactViewHolder) holder).setIndicator(false);
                }
            }catch (JSONException ex){
                ex.printStackTrace();
                Toast.makeText(ctx, "failed to load contacts", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public void updateArray(JSONArray contactsArray) {
        jsonArray=contactsArray;
        notifyDataSetChanged();

    }
}
