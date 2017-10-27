package com.dev.swibud.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dev.swibud.R;
import com.dev.swibud.activities.CreateMeetupActivity;
import com.dev.swibud.adapters.MeetupAdapter;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import androidsdk.devless.io.devless.interfaces.GetDataResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.Payload;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nayrammensah on 9/4/17.
 */

public class MeetupFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.createMeetup)
    FloatingActionButton createMeetup;

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progress_bar_profile)
    ProgressBar progress_bar;
    @OnClick(R.id.createMeetup) void newMeetup(){
        getActivity().startActivityForResult(new Intent(getActivity(), CreateMeetupActivity.class),120);
    }
    String TAG=getClass().getName();
    MeetupAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.frag_list_meetup,container,false);
        ButterKnife.bind(this,rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(linearLayoutManager);
        progress_bar.setVisibility(View.VISIBLE);
        loadMeetups();

        return rootView;
    }

    void loadMeetups(){
        App.devless.getData("MeetupService", "dummyTable", new GetDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                progress_bar.setVisibility(View.GONE);
                try {
                    JSONObject object=new JSONObject(response.toString());
                    if (object.getInt("status_code")==1001){
                        adapter=new MeetupAdapter(getContext(),object.getJSONArray(Constants.Payload));
                        recView.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
                progress_bar.setVisibility(View.GONE);

            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                Log.d(TAG,message.toString());
                progress_bar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onRefresh() {
        loadMeetups();
    }
}
