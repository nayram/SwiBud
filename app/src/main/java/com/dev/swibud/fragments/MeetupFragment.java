package com.dev.swibud.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.swibud.R;
import com.dev.swibud.activities.CreateMeetupActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nayrammensah on 9/4/17.
 */

public class MeetupFragment extends Fragment {

    @BindView(R.id.createMeetup)
    FloatingActionButton createMeetup;
    @OnClick(R.id.createMeetup) void newMeetup(){
        getActivity().startActivityForResult(new Intent(getActivity(), CreateMeetupActivity.class),120);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.frag_list_meetup,container,false);
        ButterKnife.bind(this,rootView);

        return rootView;
    }
}
