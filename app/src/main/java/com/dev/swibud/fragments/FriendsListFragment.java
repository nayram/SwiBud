package com.dev.swibud.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.activities.CreateMeetupActivity;
import com.dev.swibud.activities.OpenChatActivity;
import com.dev.swibud.adapters.ContactAdapter;
import com.dev.swibud.adapters.FollowersAdapter;
import com.dev.swibud.pojo.Users;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.DividerItemDecoration;
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.viewholders.ContactViewHolder;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.DeleteResponse;
import androidsdk.devless.io.devless.interfaces.GetDataResponse;
import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.main.Devless;
import androidsdk.devless.io.devless.messages.ErrorMessage;

import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 9/3/17.
 */

public class FriendsListFragment extends BaseFragment {

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.rlBottomSheet)
    FrameLayout rlBottomSheet;
    @BindView(R.id.profileImg)
    CircleImageView profileImg;
    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvEmail)
    TextView tvEmal;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.btnChat)
            Button btnChat;


    String TAG=getClass().getName();
    FollowersAdapter adapter;
    DividerItemDecoration dividerItemDecoration;
    private GridLayoutManager lLayout;
    private BottomSheetBehavior mBottomSheetBehavior;

    final boolean[] isFollowingUser = {false};
    ContactAdapter contactAdapter;

    JSONArray contactsArray;
    JSONObject FollowerJsonObject;

    Map<String, Object> dataToChange;

    String userName="";
    ProgressDialog pDialog;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.frag_invite_contacts,container,false);
        ButterKnife.bind(this,rootView);
        mBottomSheetBehavior = BottomSheetBehavior.from(rlBottomSheet);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        lLayout = new GridLayoutManager(getActivity(), 3);
        recView.setHasFixedSize(true);
        recView.setLayoutManager(lLayout);
        //recView.setLayoutManager(linearLayoutManager);
        progressBar.setVisibility(View.VISIBLE);
        dividerItemDecoration = new DividerItemDecoration(getResources().getDrawable(R.drawable.divider));
        pDialog=new ProgressDialog(getActivity());
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        //recyclerView.addItemDecoration(dividerItemDecoration);

        App.devless.getData("users", "devlessUser", new GetDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG,response.toString());
                try {
                    Log.d("LogUtils",response.toString());
                    JSONObject resp=new JSONObject(response.toString());
                    try {
                         contactsArray=resp.getJSONArray("payload");
                        removeItem(1);
                        removeItem(GeneralFunctions.getUserId());
                         contactAdapter=new ContactAdapter(getActivity(),contactsArray,FriendsListFragment.this);
                        recView.setAdapter(contactAdapter);
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }

                   // getFollowers(resp);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load Contacts", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG,message.toString());
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChat();
            }
        });
        return rootView;
    }


    void removeItem(int id){
        for (int i=0;i<contactsArray.length();i++){
            try {
                if (contactsArray.getJSONObject(i).getInt("id")==id){
                    contactsArray.remove(i);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void showBottomSheet(final JSONObject json, boolean isFollowing, final JSONObject finaJsonObject, final int position){
//        mBottomSheetBehavior.setPeekHeight(10);
        try {
            if (json.has("userDetails"))
                if (json.getJSONArray("userDetails").length()>0){
                    final String img=json.getJSONArray("userDetails").getJSONObject(0).getString("user_image");

                    Glide.with(getActivity())
                            .load(img)
                            .into(profileImg);
                }
            else profileImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            if (json.has("username"))
            tvUsername.setText(json.getString("username"));

            if (json.has("email"))
                tvEmal.setText(json.getString("email"));
            if (json.has("first_name"))
                userName=json.getString("first_name");

            if (json.has("last_name"))
                if (userName.isEmpty())
                userName=json.getString("last_name");
                else userName=userName +" "+json.getString("last_name");


            isFollowingUser[0]=isFollowing;
            if (isFollowing){
                btnFollow.setBackground(getActivity().getResources().getDrawable(R.drawable.followbtn));
                btnFollow.setTextColor(getActivity().getResources().getColor(R.color.white));
                btnFollow.setText("Unfollow");
            }else{
                btnFollow.setBackground(getActivity().getResources().getDrawable(R.drawable.unfollowbtn));
                btnFollow.setTextColor(getActivity().getResources().getColor(R.color.black));
                btnFollow.setText("Follow");
            }

            dataToChange = new HashMap<>();
            try {

                dataToChange.put("user_id",json.getInt("id"));
                dataToChange.put("follower_id",GeneralFunctions.getUserId());
                dataToChange.put("accepted",0);


            }catch (JSONException ex){
                ex.printStackTrace();
            }

            FollowerJsonObject=finaJsonObject;

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btnFollow.setVisibility(View.GONE);


                    if (isFollowingUser[0]) {
                        unFollowUser(FollowerJsonObject,position);
                    } else {
                        followUser(dataToChange,position,json);
                    }


                }
            });

        }catch (JSONException ex){
            ex.printStackTrace();

        }


    }

    void unFollowUser(final JSONObject finalFollowObj, final int position){
        try {
            progress.setVisibility(View.VISIBLE);
        App.devless.delete("followers", "followers", finalFollowObj.getString("id"), new DeleteResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                Log.d(TAG, response.toString());
                progress.setVisibility(View.GONE);
                btnFollow.setBackground(getActivity().getResources().getDrawable(R.drawable.unfollowbtn));
                btnFollow.setTextColor(getActivity().getResources().getColor(R.color.black));
                btnFollow.setText("Follow");
                btnFollow.setVisibility(View.VISIBLE);
                isFollowingUser[0] = false;
                if (contactsArray !=null){
                    try{
                        Log.d(TAG,contactsArray.getJSONObject(position).getJSONArray(Constants.FOLLOWERS).length()+" "+contactsArray.getJSONObject(position));
                        for (int i=0;i<contactsArray.getJSONObject(position).getJSONArray(Constants.FOLLOWERS).length(); i++){
//                        Log.d(TAG,finalFollowObj.getJSONObject(i).getInt("follower_id")+" - " +GeneralFunctions.getUserId(ctx));
                            if (contactsArray.getJSONObject(position).getJSONArray(Constants.FOLLOWERS).getJSONObject(i).getInt("follower_id")==GeneralFunctions.getUserId() ){
                                Log.d(TAG,contactsArray.getJSONObject(position).getJSONArray(Constants.FOLLOWERS).getJSONObject(i).toString());
                                contactsArray.getJSONObject(position).getJSONArray(Constants.FOLLOWERS).remove(i);
                                Log.d(TAG,contactsArray.getJSONObject(position).getJSONArray(Constants.FOLLOWERS).toString());
                                contactAdapter.updateArray(contactsArray);
                            }
                        }
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                Log.d(TAG, errorMessage.toString());
                progress.setVisibility(View.GONE);
                btnFollow.setVisibility(View.VISIBLE);
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                Log.d(TAG, message.toString());
                progress.setVisibility(View.GONE);
                btnFollow.setVisibility(View.VISIBLE);
            }
        });

    } catch (JSONException e) {
        e.printStackTrace();
    }



    }

    void followUser(Map<String,Object> dataToChange, final int position, final JSONObject jsonObject){
        Log.d(TAG,"Follow User");
        progress.setVisibility(View.VISIBLE);
        App.devless.postData("followers", "followers", dataToChange, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                Log.d(TAG,response.toString());
                progress.setVisibility(View.GONE);
                btnFollow.setVisibility(View.VISIBLE);
                btnFollow.setBackground(getActivity().getResources().getDrawable(R.drawable.followbtn));
                btnFollow.setTextColor(getActivity().getResources().getColor(R.color.white));
                btnFollow.setText("Unfollow");
                isFollowingUser[0]=true;
                try {
                    JSONObject resp=new JSONObject(response.toString());

                    JSONObject jobj=new JSONObject();
                    JSONArray profile=new JSONArray();
                    if (resp.has(Constants.Payload)){
                        if (resp.getJSONObject(Constants.Payload).has(Constants.EntryId)){
                           int id= resp.getJSONObject(Constants.Payload).getInt(Constants.EntryId);
                           jobj.put("id",id);
                        }
                    }
                    jobj.put("devless_user_id",1);
                    jobj.put("follower_id",GeneralFunctions.getUserId());
                    jobj.put("accepted",0);
                    jobj.put("user_id",jsonObject.getInt("id"));
                    jobj.put("profile",profile);
                    Log.d(TAG,jobj.toString());
                    contactsArray.getJSONObject(position).getJSONArray(Constants.FOLLOWERS).put(jobj);
                    Log.d(TAG,contactsArray.getJSONObject(position).toString());
                    FollowerJsonObject=jobj;
                    Log.d(TAG,FollowerJsonObject.toString());
                    Log.d(TAG,contactsArray.toString());
                    contactAdapter.updateArray(contactsArray);

                }catch (JSONException ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                progress.setVisibility(View.GONE);
                btnFollow.setVisibility(View.VISIBLE);
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                progress.setVisibility(View.GONE);
                btnFollow.setVisibility(View.VISIBLE);
                Log.d(TAG,message.toString());
            }
        });
    }

    @Override
    public String getTagText() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        if (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }else{
            return false;
        }

    }

    void openChat(){
        String userString=GeneralFunctions.getUser(getActivity());
        Log.d(TAG,userString);
        try {
            final JSONObject object=new JSONObject(userString);
//                    mSelected.add(object.getString("username"));

            String access=GeneralFunctions.getSendBirdAccessToken(getActivity());


            showProgressBar(true);
            SendBird.connect(object.getString("username"),access, new SendBird.ConnectHandler() {
                @Override
                public void onConnected(User user, SendBirdException e) {
                    if (e != null) {
                        showProgressBar(false);
                        e.printStackTrace();
                        // Error.
                        Toast.makeText(getContext(), "Failed to establish connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    registerFcm(user);


                }
            });

                    /*String access=GeneralFunctions.getSendBirdAccessToken(getActivity());

                    Log.d(TAG,access);*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void showProgressBar(boolean status){
        if (status){
            pDialog.show();
        }else {
            pDialog.dismiss();
        }
    }

    void registerFcm( final User user){
        if (FirebaseInstanceId.getInstance().getToken() == null){

            showProgressBar(false);
            Toast.makeText(getContext(), "Failed to establish connection", Toast.LENGTH_SHORT).show();
            return;
        }

        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
                new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                        if (e != null) {
                            showProgressBar(false);
                            e.printStackTrace();
                            // Error.
                            Toast.makeText(getContext(), "Failed to establish connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<String> mSelected=new ArrayList<>();
                        mSelected.add(tvUsername.getText().toString());
                        mSelected.add(user.getUserId());
                        createGroupChannel(mSelected,userName,true);
                    }
                });
    }

    private void createGroupChannel(List<String> userIds, final String name, boolean distinct) {

        GroupChannel.createChannelWithUserIds(userIds, distinct, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                showProgressBar(false);
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to launch chat window. Try again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG,groupChannel.toString());

                Intent intent = new Intent(getContext(),OpenChatActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString(Constants.EXTRA_NEW_CHANNEL_URL,groupChannel.getUrl());
                bundle.putString("name",name);
                intent.putExtras(bundle);
                startActivity(intent);


            }
        });
    }
}
