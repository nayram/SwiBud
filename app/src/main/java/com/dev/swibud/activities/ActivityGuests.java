package com.dev.swibud.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.dev.swibud.adapters.MeetupAdapter;
import com.dev.swibud.fragments.MeetupFragment;
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

    int updateList=0;

    GuestAdapter adapter;

    private String TAG=getClass().getName();

    boolean updateMeetups=false;

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

        participants();
        adapter=new GuestAdapter(jsonArray,this,true);
        adapter.activityGuests=this;
        rec_guests.setAdapter(adapter);

        Bundle bundle=getIntent().getExtras();
        if (bundle !=null){
            meetup_id=bundle.getInt("meetup_id");
        }


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
                Bundle bundle=new Bundle();
                bundle.putInt("meetup_id",meetup_id);
                Intent intent=new Intent(getApplicationContext(),AllUsers.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,301);
                break;
            default:
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void loadGuests(){
        Log.d(TAG,String.valueOf(meetup_id));
        Map<String, Object> params = new HashMap<>();
        params.put("where", "id,"+String.valueOf(meetup_id));

        pgLoading.setVisibility(View.VISIBLE);

        App.devless.search("GetMeetup", "profile", params, new SearchResponse() {
           @Override
           public void onSuccess(ResponsePayload response) {
               pgLoading.setVisibility(View.GONE);
               Log.d(TAG,response.toString());
               try {
                   JSONObject object=new JSONObject(response.toString());

                   if (object.getInt("status_code")==1001){

                       jsonArray=object.getJSONArray(Constants.Payload).getJSONObject(0).getJSONArray("participants");
                       participants();
                       adapter.updateParticipants(jsonArray);
                   }

               } catch (JSONException e) {

                   e.printStackTrace();
               }


           }

           @Override
           public void userNotAuthenticated(ErrorMessage errorMessage) {
               pgLoading.setVisibility(View.GONE);
               Log.d(TAG,errorMessage.toString());
           }
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"Request code "+requestCode+" Result code "+resultCode);
        if (requestCode== 301 && resultCode==1102){
            updateList=1;
        }else if (requestCode== 301 && resultCode==1101){
            setResult(RESULT_OK);
            loadGuests();

            if (updateList ==1){
                Log.d(TAG,"Load Guest");
                updateMeetups=true;

            }

        }
    }

    public void setGuestArray(JSONArray guestArray) {
        jsonArray=guestArray;
        updateMeetups=true;
    }

    void  participants(){
        for (int i=0;i<jsonArray.length();i++){
            try {
                JSONObject obj=jsonArray.getJSONObject(i);
                if (GeneralFunctions.getUserId() == obj.getInt("users_id")){

                    jsonArray.remove(i);
                }
            }catch (JSONException ex){
                ex.printStackTrace();
            }


        }
    }

    public class GuestBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "MyBroadcastReceiver";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Bundle rec=intent.getExtras();
            if (rec !=null){
                if (rec.getInt("updateGuest")==1102){
                    Log.d(TAG,"update List "+1102);
                    updateList=1;
                }

            }
        }
    }
}
