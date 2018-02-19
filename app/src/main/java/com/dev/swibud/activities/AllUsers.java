package com.dev.swibud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.dev.swibud.R;
import com.dev.swibud.adapters.GuestInviteAdapter;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.DividerItemDecoration;
import com.dev.swibud.utils.GeneralFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.GetDataResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nayrammensah on 1/23/18.
 */

public class AllUsers extends BaseActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    @BindView(R.id.rec_guests)
    RecyclerView rec_guests;

    @BindView(R.id.pgLoading)
    ProgressBar progressBar;

    JSONArray contacts;

    public static JSONArray participant_array;

    String TAG=getClass().getName();
    private int meetup_id;

    int resultStatus=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_guests);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(1101);
                finish();
                onBackPressed();
            }
        });

        setTitle("Add Guests");
        DividerItemDecoration  dividerItemDecoration = new DividerItemDecoration(getResources().getDrawable(R.drawable.divider));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rec_guests.setLayoutManager(linearLayoutManager);
        rec_guests.addItemDecoration(dividerItemDecoration);

        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle=getIntent().getExtras();
        if (bundle !=null){
            meetup_id=bundle.getInt("meetup_id");
        }

        App.devless.getData("users", "devlessUser", new GetDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject resp=new JSONObject(response.toString());
                    contacts=resp.getJSONArray("payload");
                    removeItem(1);
                    removeItem(GeneralFunctions.getUserId());
                    GuestInviteAdapter adapter=new GuestInviteAdapter(AllUsers.this,contacts,participant_array,meetup_id);
                    rec_guests.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                Log.d(TAG,message.toString());
                progressBar.setVisibility(View.GONE);
            }
        });



    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setResult(1101);
        finish();
    }

    public void showProgressBar(boolean status){
        if (status){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }



    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        setResult(1101);
        finish();
    }

    void removeItem(int id){
        for (int i=0;i<contacts.length();i++){
            try {
                if (contacts.getJSONObject(i).getInt("id")==id){
                    contacts.remove(i);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCallbackResult(int status){
        Log.d(TAG,"Status "+status);
        resultStatus=status;
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt("updateGuest",1102);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        setResult(status);
    }
}
