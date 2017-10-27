package com.dev.swibud.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.swibud.adapters.InviteContactAdapter;
import com.dev.swibud.pojo.Users;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.viewholders.InviteContactViewHolder;
import com.google.gson.internal.Streams;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.dev.swibud.R;

import androidsdk.devless.io.devless.interfaces.GetDataResponse;
import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import com.dev.swibud.adapters.PlaceArrayAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by nayrammensah on 9/4/17.
 */

public class CreateMeetupActivity extends AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, DatePickerDialog.OnDateSetListener,
        DatePickerDialog.OnDateChangedListener,TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.bottom_sheet_toolbar)
    Toolbar bottom_sheet_toolbar;
    @BindView(R.id.edtTitle)
    EditText edtTitle;
    @BindView(R.id.edtDate)
    EditText edtDate;
    @BindView(R.id.autoCompLocation)
    AutoCompleteTextView autLocation;
    @BindView(R.id.edtTime)
    EditText edtTime;
    @BindView(R.id.edtDetails)
    EditText edtDetails;
    @BindView(R.id.tvAddPeople)
    LinearLayout tvAllPeople;
    @BindView(R.id.rlBottomSheet)
    RelativeLayout rlBottomSheet;
    @BindView(R.id.recInviteContacts)
    RecyclerView recInviteContacts;
    @BindView(R.id.pgLoadContacts)
    ProgressBar pgLoadContacts;
    @BindView(R.id.pgCreateMeetup)
    ProgressBar pgCreateMeetup;
    @BindView(R.id.llGuests)
    LinearLayout llGuests;

    String selectedLocation="";

    private boolean loadContacts=false;
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private String LOG_TAG=getClass().getName();
    protected DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    protected DateFormat monthDateFormat = new SimpleDateFormat("MMM d, yyyy");
    Calendar now,selectedCalendar,timeCalendar;

    JSONArray contacts;

    private BottomSheetBehavior mBottomSheetBehavior;

    private GridLayoutManager lLayout;

    String TAG=getClass().getName();

    Map<String,JSONObject> selectedInvites=new HashMap<>();
    Map<String,JSONObject> setInvites=new HashMap<>();

    LatLng latlng;


    @OnClick(R.id.tvAddPeople) void launchBottomSheets(){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick(R.id.edtDate) void onClick(){

        dpd.setMinDate(now);
        dpd.registerOnDateChangedListener(this);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @OnClick (R.id.edtTime) void chooseTime(){

        tpd.setMinTime(now.get(Calendar.HOUR),now.get(Calendar.MINUTE),now.get(Calendar.SECOND));
        tpd.setOnTimeSetListener(this);
        tpd.show(getFragmentManager(),"Timepickerdialog");
    }

    @OnClick(R.id.btnCreate) void createMeetup(){
        if(validateInputs()){
            createMeetupService();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_meetup);
        ButterKnife.bind(this);
        mBottomSheetBehavior = BottomSheetBehavior.from(rlBottomSheet);
        now=Calendar.getInstance();
        selectedCalendar=Calendar.getInstance();
        timeCalendar=Calendar.getInstance();
        setSupportActionBar(toolbar);
        setTitle("Meet Up");
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        tpd=TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                false
        );

        bottom_sheet_toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_close,null));
        bottom_sheet_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottom_sheet_toolbar.setTitle("Invite Contact");
        bottom_sheet_toolbar.inflateMenu(R.menu.invite_guest);
        bottom_sheet_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.menu_add:
                        setInvites=selectedInvites;
                        llGuests.removeAllViews();
                        for (Map.Entry<String, JSONObject> entry : setInvites.entrySet())
                        {
                            View guestsImg= LayoutInflater.from(CreateMeetupActivity.this)
                                    .inflate(R.layout.participant_contents_layout,null);
                            CircleImageView circleImageView=(CircleImageView)guestsImg.findViewById(R.id.img_participant);
                            TextView tvProfName=(TextView)guestsImg.findViewById(R.id.tv_participant_name);
                            try {
                                if (entry.getValue().getJSONArray("userDetails").length()>0){
                                    String img=entry.getValue().getJSONArray("userDetails").getJSONObject(0).getString("user_image");
                                    Glide.with(CreateMeetupActivity.this)
                                            .load(img)
                                            .into(circleImageView);


                                }else{
                                    /*Glide.with(CreateMeetupActivity.this)
                                            .load(getResources().getDrawable(R.drawable.ic_action_user_white))
                                    .into(circleImageView);*/
                                }
                                if (!entry.getValue().getString("first_name").equals("null"))
                                    tvProfName.setText(entry.getValue().getString("first_name") + " "+entry.getValue().getString("last_name"));
                                else{
                                    tvProfName.setText(entry.getValue().getString("username"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            llGuests.addView(guestsImg);

                        }


                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                }
                return false;
            }
        });
        lLayout = new GridLayoutManager(CreateMeetupActivity.this, 2);
        recInviteContacts.setHasFixedSize(true);
        recInviteContacts.setLayoutManager(lLayout);

        autLocation.setThreshold(3);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        autLocation.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        autLocation.setAdapter(mPlaceArrayAdapter);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if (contacts==null || contacts.length()==0);{
                        if (!loadContacts)
                        displayContacts();
                    }

                        break;
                    default:

                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }

    void displayContacts(){
        pgLoadContacts.setVisibility(View.VISIBLE);
        App.devless.getData("users", "devlessUser", new GetDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                pgLoadContacts.setVisibility(View.GONE);
                Log.d(TAG,response.toString());
                try {
                    Log.d(TAG,response.toString());
                    JSONObject resp=new JSONObject(response.toString());
                    contacts=resp.getJSONArray("payload");
                    Log.d(TAG,"Length "+contacts.length());
                    if (contacts.length()>0){
                        loadContacts=true;

                    }
                    InviteContactAdapter adapter=new InviteContactAdapter(CreateMeetupActivity.this,contacts,setInvites);
                    recInviteContacts.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateMeetupActivity.this, "Failed to load Contacts", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                pgLoadContacts.setVisibility(View.GONE);
                Log.d(TAG,errorMessage.toString());
                Toast.makeText(CreateMeetupActivity.this, "Failed to load Contacts", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                pgLoadContacts.setVisibility(View.GONE);
                Log.d(TAG,message.toString());
                Toast.makeText(CreateMeetupActivity.this, "Failed to load Contacts", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            if (!selectedLocation.equals(autLocation.getText().toString()))
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            Log.d(LOG_TAG, String.valueOf(place.getLatLng()));
            latlng=place.getLatLng();

            autLocation.setText(place.getName().toString());
            selectedLocation=place.getName().toString();
            if (attributions != null) {
                Log.d(LOG_TAG, String.valueOf(Html.fromHtml(attributions.toString())));
            }
        }
    };

    @Override
    public void onBackPressed() {

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedCalendar.set(Calendar.YEAR,year);
        selectedCalendar.set(Calendar.MONTH,monthOfYear);
        selectedCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder(
                monthDateFormat.format(selectedCalendar.getTime()));
        edtDate.setText(spannableStringBuilder4);
    }

    @Override
    public void onDateChanged() {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        timeCalendar.set(Calendar.HOUR,hourOfDay);
        timeCalendar.set(Calendar.MONTH,minute);
        timeCalendar.set(Calendar.SECOND,second);
        SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder(
                timeFormat.format(timeCalendar.getTime()));
        edtTime.setText(spannableStringBuilder4);

    }

    public void addSelectedContact(int id,JSONObject object){
        selectedInvites.put(String.valueOf(id),object);
        Log.d(TAG,"Selected Contacts "+selectedInvites.size());

    }

    public void removeSelectedContact(int id){
        selectedInvites.remove(String.valueOf(id));
        Log.d(TAG,"Selected Contacts "+selectedInvites.size());
    }
    
    boolean validateInputs(){
        boolean result=true;
        if (edtTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter title of event", Toast.LENGTH_SHORT).show();
            result=false;
        }else if (edtDate.getText().toString().isEmpty()){
            Toast.makeText(this, "Select date", Toast.LENGTH_SHORT).show();
            result=false;
        }else if (edtTime.getText().toString().isEmpty()){
            Toast.makeText(this, "Select time", Toast.LENGTH_SHORT).show();
            result=false;
        }else if (autLocation.getText().toString().isEmpty()){
            Toast.makeText(this, "Add your location ", Toast.LENGTH_SHORT).show();
            result=false;
        }else if (selectedInvites.isEmpty()){
            Toast.makeText(this, "Please invite at least one person", Toast.LENGTH_SHORT).show();
            result=false;
        }
        return result;
    }

    void createMeetupService(){
        pgCreateMeetup.setVisibility(View.VISIBLE);
        Map<String,Object> params=new HashMap<>();
        params.put("time",edtTime.getText().toString());
        params.put("date",edtDate.getText().toString());
        params.put("name",edtTitle.getText().toString());
        params.put("users_id", GeneralFunctions.getUserId(this));
        params.put("timestamp",String.valueOf(selectedCalendar.getTime()));
        params.put("location",autLocation.getText().toString());
        params.put("description",edtDetails.getText().toString());

        if (latlng !=null){
            params.put("latitude",latlng.latitude);
            params.put("longitude",latlng.longitude);
        }



        App.devless.postData("meetups", "meetup", params, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                Log.d(TAG,response.toString());
                try{
                    JSONObject meetupResp=new JSONObject(response.toString());
                    ArrayList<Map<String,Object>> invitesArralist=new ArrayList<Map<String, Object>>();
                    if (meetupResp.getInt("status_code")==609){
                        int meetupId=meetupResp.getJSONObject(Constants.Payload).getInt("entry_id");
                        for (Map.Entry<String, JSONObject> entry : selectedInvites.entrySet())
                        {
                            System.out.println(entry.getKey() + "/" + entry.getValue());
                            Map<String,Object> invites=new HashMap<>();
                            invites.put("meetups_meetup_id",meetupId);
                            invites.put("users_id",entry.getKey());
                            invites.put("accepted",0);
                            invites.put("completed",0);
                            invitesArralist.add(invites);
                        }
                        addInvites(invitesArralist);

                    }else{
                        pgCreateMeetup.setVisibility(View.GONE);
                    }
                }catch (JSONException ex){
                    ex.printStackTrace();
                    pgCreateMeetup.setVisibility(View.GONE);

                }


            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
                pgCreateMeetup.setVisibility(View.GONE);

            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                Log.d(TAG,message.toString());
                pgCreateMeetup.setVisibility(View.GONE);

            }
        });
    }

    void addInvites(ArrayList<Map<String,Object>> param){
        App.devless.postMassData("meetups", "invites", param, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {

                Log.d(TAG,response.toString());
                pgCreateMeetup.setVisibility(View.GONE);
                setResult(RESULT_OK);

            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
                pgCreateMeetup.setVisibility(View.GONE);
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                pgCreateMeetup.setVisibility(View.GONE);
                Log.d(TAG,message.toString());

            }
        });
    }



}
