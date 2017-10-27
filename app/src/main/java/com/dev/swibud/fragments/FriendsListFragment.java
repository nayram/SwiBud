package com.dev.swibud.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.adapters.FollowersAdapter;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.DividerItemDecoration;
import com.dev.swibud.utils.GeneralFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.GetDataResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.main.Devless;
import androidsdk.devless.io.devless.messages.ErrorMessage;

import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nayrammensah on 9/3/17.
 */

public class FriendsListFragment extends Fragment {

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    String TAG=getClass().getName();
    FollowersAdapter adapter;
    DividerItemDecoration dividerItemDecoration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.frag_invite_contacts,container,false);
        ButterKnife.bind(this,rootView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(linearLayoutManager);
        progressBar.setVisibility(View.VISIBLE);
        dividerItemDecoration = new DividerItemDecoration(getResources().getDrawable(R.drawable.divider));
        //recyclerView.addItemDecoration(dividerItemDecoration);

        App.devless.getData("users", "devlessUser", new GetDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG,response.toString());
                try {
                    Log.d(TAG,response.toString());
                    JSONObject resp=new JSONObject(response.toString());
                    getFollowers(resp);
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
        return rootView;
    }


    void getFollowers(final JSONObject allUsers){

        App.devless.getData("followers", "followers", new GetDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                Log.d(TAG,"Followers "+response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject resp=new JSONObject(response.toString());
                    JSONArray folowers=resp.getJSONObject(Constants.Payload).getJSONArray(Constants.Result);
                    Log.d(TAG,"Followers length "+folowers.length());
                    adapter=new FollowersAdapter(getContext(),allUsers,folowers);
                    recView.setAdapter(adapter);
                    recView.addItemDecoration(dividerItemDecoration);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load contacts. Please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load contacts. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
