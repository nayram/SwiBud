package com.dev.swibud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.adapters.GuestAdapter;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.GetDataResponse;
import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nayrammensah on 1/21/18.
 */

public class ActivityGuests extends BaseActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    @BindView(R.id.rec_guests)
    RecyclerView rec_guests;

    @BindView(R.id.pgLoading)
    ProgressBar pgLoading;

    public static JSONArray jsonArray;

    public static int meetup_id;

    GuestAdapter adapter;

    private String TAG=getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_guests);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("Guests");
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getResources().getDrawable(R.drawable.divider));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rec_guests.setLayoutManager(linearLayoutManager);
        rec_guests.addItemDecoration(dividerItemDecoration);
//      Toast.makeText(this,"json Array "+jsonArray.length(),Toast.LENGTH_SHORT).show();

        adapter=new GuestAdapter(jsonArray,this);

        //rec_guests.setAdapter(adapter);*
       /* Bundle bundle=getIntent().getExtras();
        if (bundle !=null){
            meetup_id=bundle.getInt("meetup_id");
            Toast.makeText(this,meetup_id+"",Toast.LENGTH_LONG).show();
            loadGuests();
        }*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     getMenuInflater().inflate(R.menu.menu_guest,menu);
     return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                AllUsers.participant_array=jsonArray;
                Intent intent=new Intent(getApplicationContext(),AllUsers.class);
                startActivityForResult(intent,301);
                break;
            default:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void loadGuests(){
        Log.d(TAG,String.valueOf(meetup_id));
        Map<String, Object> params = new HashMap<>();
        params.put("where", "meetups_meetup_id,"+String.valueOf(meetup_id));
//        params.put("meetup_id",String.valueOf(meetup_id));
//        Toast.makeText(this,meetup_id,Toast.LENGTH_LONG).show();
        pgLoading.setVisibility(View.VISIBLE);

        App.devless.search(Constants.MeetupGuests, Constants.dummyTable, params, new SearchResponse() {
           @Override
           public void onSuccess(ResponsePayload response) {
               pgLoading.setVisibility(View.GONE);
               Log.d(TAG,response.toString());
           }

           @Override
           public void userNotAuthenticated(ErrorMessage errorMessage) {
               pgLoading.setVisibility(View.GONE);
               Log.d(TAG,errorMessage.toString());
           }
       });

        /*App.devless.postData(Constants.MeetupGuests, Constants.dummyTable, params, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                pgLoading.setVisibility(View.GONE);
                Log.d(TAG,response.toString());
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                pgLoading.setVisibility(View.GONE);
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                pgLoading.setVisibility(View.GONE);
                Log.d(TAG,message.toString());
            }
        });*/

       /* pgLoading.setVisibility(View.VISIBLE);
        App.devless.getData("meetups", "invites", new GetDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                try{
                    JSONObject res=new JSONObject(response.toString());
                    JSONArray payload=res.getJSONArray(Constants.Payload);
                    adapter.addData(payload);
                }catch (JSONException ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {

            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {

            }`
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== 301 && resultCode==RESULT_OK){

        }
    }
}
