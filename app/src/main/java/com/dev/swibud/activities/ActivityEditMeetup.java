package com.dev.swibud.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.adapters.PlaceArrayAdapter;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.GeneralFunctions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.EditDataResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nayrammensah on 1/21/18.
 */

public class ActivityEditMeetup extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, DatePickerDialog.OnDateSetListener,
        DatePickerDialog.OnDateChangedListener,TimePickerDialog.OnTimeSetListener{

    public static JSONObject jobj;

    @BindView(R.id.tool_bar)
    Toolbar toolbar;

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

    @BindView(R.id.pgMeetup)
    ProgressBar pgMeetup;

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

    String selectedLocation="",TAG=getClass().getName();
    LatLng latlng;
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

    @OnClick(R.id.btnSaveChanges) void UpdateMeetup(){
        if (validateInputs()){
            updateMeetup();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetup_edit);
        ButterKnife.bind(this);

        now=Calendar.getInstance();
        selectedCalendar=Calendar.getInstance();
        timeCalendar=Calendar.getInstance();
        setSupportActionBar(toolbar);
        setTitle("Edit Meetup");
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

        if (jobj !=null){
            try{
                edtTitle.setText(jobj.getString("name"));
                edtDate.setText(jobj.getString("date"));
                edtTime.setText(jobj.getString("time"));
                selectedLocation=jobj.getString("location");
                autLocation.setText(jobj.getString("location"));
                latlng=new LatLng(jobj.getDouble("latitude"),jobj.getDouble("longitude"));

                if (jobj.get("description") !=null && !jobj.getString("description").equalsIgnoreCase("null"))
                edtDetails.setText(jobj.getString("description"));

            }catch (JSONException ex){
                ex.printStackTrace();
            }

        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateChanged() {

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
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        timeCalendar.set(Calendar.HOUR,hourOfDay);
        timeCalendar.set(Calendar.MONTH,minute);
        timeCalendar.set(Calendar.SECOND,second);
        SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder(
                timeFormat.format(timeCalendar.getTime()));
        edtTime.setText(spannableStringBuilder4);
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
        }
        return result;
    }

    void updateMeetup(){

        Map<String, Object> params = new HashMap<>();
        params.put("time",edtTime.getText().toString());
        params.put("date",edtDate.getText().toString());
        params.put("name",edtTitle.getText().toString());
        params.put("users_id", GeneralFunctions.getUserId());
        params.put("timestamp",String.valueOf(selectedCalendar.getTime()));
        params.put("location",autLocation.getText().toString());
        params.put("description",edtDetails.getText().toString());

        if (latlng !=null){
            params.put("latitude",latlng.latitude);
            params.put("longitude",latlng.longitude);
        }

        try {
            showProgressBar(true);
            App.devless.edit(Constants.MeetUpService, Constants.MEETUPTBL, params, jobj.getString("id"), new EditDataResponse() {
                @Override
                public void onSuccess(ResponsePayload response) {
                    Log.d(TAG,response.toString());
                    showProgressBar(false);
                }

                @Override
                public void onFailed(ErrorMessage errorMessage) {
                    Log.d(TAG,errorMessage.toString());
                    showProgressBar(false);
                }

                @Override
                public void userNotAuthenticated(ErrorMessage message) {
                    Log.d(TAG,message.toString());
                    showProgressBar(false);
                }
            });
        }catch (Exception ex){

        }

    }

    void showProgressBar(boolean value){
        if (value){
            pgMeetup.setVisibility(View.VISIBLE);
        }else{
            pgMeetup.setVisibility(View.GONE);
        }
    }
}
