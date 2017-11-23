package com.dev.swibud.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.dev.swibud.R;
import com.dev.swibud.interfaces.MainServiceInterface;
import com.dev.swibud.pojo.ExtraUserProfile;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.GeneralFunctions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.EditDataResponse;
import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nayrammensah on 8/6/17.
 */

public class FragmentDisplayFriendLocations extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,OnRequestPermissionsResultCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private FusedLocationProviderClient mFusedLocationProviderClient;
    MaterialDialog dialog;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    GoogleMap mMap;
    String TAG=getClass().getName();
    Location mLastKnownLocation;
    float DEFAULT_ZOOM= 14;

    String serviceName="UserExraDetails";

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mLocationPermissionGranted = false;
    MainServiceInterface authCallback;
    Context ctx;

    public GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;

    @BindView(R.id.mapProgress)
    ProgressBar mapProgress;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_map,container,false);
        ButterKnife.bind(this,rootView);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setUpGClient();setUpGClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLocationPermission();
        SupportMapFragment fragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap=map;
        /*LatLng minc = new LatLng(5.644429, -0.1537547);
        View marker = LayoutInflater.from(getActivity()).inflate(R.layout.custom_marker_pin, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.tvName);
        numTxt.setText("Nayram");


        mMap.addMarker(new MarkerOptions()
                .position(minc)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker))));



        LatLng anc=new LatLng(5.64159,-0.1545487);
        View marker1=LayoutInflater.from(getActivity()).inflate(R.layout.custom_marker_pin,null);
        TextView numTxt1=(TextView)marker1.findViewById(R.id.tvName);
        numTxt1.setText("Tsatsu");
        mMap.addMarker(new MarkerOptions()
                .position(anc)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker1))));

        LatLng lizzySports=new LatLng(5.6425082,-0.1541946);
        View marker2=LayoutInflater.from(getActivity()).inflate(R.layout.custom_marker_pin,null);
        TextView numTxt2=(TextView)marker2.findViewById(R.id.tvName);
        numTxt2.setText("Julius");
        mMap.addMarker(new MarkerOptions()
                .position(lizzySports)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker2))));



        marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Nayram Swibud");
            }
        });
        marker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Tsatsu Swibud");
            }
        });

        marker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Julius Swibud");
            }
        });*/


        mMap.setOnMarkerClickListener(this);

        /*LatLng maxInternational=new LatLng(5.64159,-0.1545487);
        View marker3=LayoutInflater.from(getActivity()).inflate(R.layout.custom_marker_pin,null);
        TextView numTxt3=(TextView)marker3.findViewById(R.id.tvName);
        numTxt3.setText("Edward");
        map.addMarker(new MarkerOptions()
                .position(maxInternational)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker3))));*/

        getDeviceLocation();
        updateLocationUI();
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minc, 16));

       /* map.addMarker(new MarkerOptions()
                .title("Sydney")5.64159,-0.1545487
                .snippet("The most populous city in Australia.")
                .position(sydney));*/

    }

    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void showDialog(String name){
        Log.d("Friends",name);
        boolean wrapInScrollView = true;

        View user_view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user,null);
        TextView textView1=(TextView)user_view.findViewById(R.id.tvDialogUserName);
        textView1.setText(name);
        Button btnChat=(Button)user_view.findViewById(R.id.btnDialogUser);
        dialog=new MaterialDialog.Builder(getActivity())
                .customView(user_view, wrapInScrollView).show();

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();

    }

    public void updateLocationUI() {
        if (mMap == null) {
            //Toast.makeText(getActivity(), "Set your ", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                //Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                if (googleApiClient !=null){
                    if (googleApiClient.isConnected()){
                        getMyLocation();
                    }
                }
               /* Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG,new Gson().toJson(task));

                        if (task.isSuccessful()) {
                            //Toast.makeText(getActivity(), "Task Successful", Toast.LENGTH_SHORT).show();
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if(mLastKnownLocation!=null)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            else authCallback.setLocationSnackBar("Turn on GPS location!");

                        } else {
                           // Toast.makeText(getActivity(), "Current Location is null", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });*/
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    void getMyLocation(){
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED){
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(googleApiClient, locationRequest, this);
            PendingResult result =
                    LocationServices.SettingsApi
                            .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied.
                            // You can initialize location requests here.
                            int permissionLocation = ContextCompat
                                    .checkSelfPermission(getActivity(),
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                mLastKnownLocation = LocationServices.FusedLocationApi
                                        .getLastLocation(googleApiClient);
                                if (mLastKnownLocation!=null){
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastKnownLocation.getLatitude(),
                                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                    GeneralFunctions.setLatitude(getContext(), (float) mLastKnownLocation.getLatitude());
                                    GeneralFunctions.setLongitude(getContext(), (float) mLastKnownLocation.getLongitude());
                                    checkUserIdAvailability();
                                    //setUserLocation(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLatitude());
                                }

                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied.
                            // But could be fixed by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                // Ask to turn on GPS automatically
                                status.startResolutionForResult(getActivity(),
                                        REQUEST_CHECK_SETTINGS_GPS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied.
                            // However, we have no way
                            // to fix the
                            // settings so we won't show the dialog.
                            // finish();
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       // getMyLocation();

    }

    private synchronized void setUpGClient() {
        if (googleApiClient==null){
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(),0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        switch (marker.getId()){
            case "m0":
                showDialog("Nayram");
                break;
            case "m1":
                showDialog("Tsatsu");
                break;
            case "m2":
                showDialog("Julius");
                break;
        }
        return false;
    }

    public void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
          //  Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            //Toast.makeText(getActivity(), "Permission request requested", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;

        if (context instanceof MainServiceInterface) {
            authCallback = (MainServiceInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }




    @Override
    public void onDetach() {
        super.onDetach();
        authCallback = null;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocationPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

   /* @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(getActivity());
            googleApiClient.disconnect();
            Log.d(TAG, "On Destroy");
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            //Toast.makeText(ctx, "On Pause", Toast.LENGTH_SHORT).show();
            googleApiClient.stopAutoManage(getActivity());
            googleApiClient.disconnect();
        }else{
            //Toast.makeText(ctx, "Second- On Pause", Toast.LENGTH_SHORT).show();
        }
    }

   /* @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(getActivity());
            googleApiClient.disconnect();
        }
    }*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(),
                        location.getLongitude()), DEFAULT_ZOOM));

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mapProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mapProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mapProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mapProgress.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }

    void setUserLocation(double latitude,double longitude){
        int id=GeneralFunctions.getUserId(getContext());
        Map<String, Object> dataToPost = new HashMap<>();
        dataToPost.put("lat",latitude);
        dataToPost.put("lng",longitude);
        dataToPost.put("user_id",id);

        showProgress(true);
        App.devless.postData("location", "user_location", dataToPost, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                Log.d(TAG,response.toString());
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                Log.d(TAG,message.toString());
            }
        });


    }

    void checkUserIdAvailability(){
        Map<String, Object> params = new HashMap<>();
        params.put("where","users_id,"+GeneralFunctions.getUserId(getActivity()));
        App.devless.search(serviceName, "user_extra_details", params, new SearchResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
               // hideProgress();
                Log.d(TAG,response.toString());
                try {
                    JSONObject jsonObject=new JSONObject(response.toString());
                    String userobjParam=GeneralFunctions.getUserExtraDetail(getActivity());
                    if (userobjParam==null){
                        if (jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).length()>0){
                            JSONObject profile=jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).getJSONObject(0);
                            ExtraUserProfile eup=new ExtraUserProfile(profile.getDouble("latitude"),
                                    profile.getDouble("longitude"),profile.getString("user_image"),profile.getInt("id"));
                            GeneralFunctions.setUserExtraDetail(getContext(),new Gson().toJson(eup));
                        }
                    }
                    if (jsonObject.getJSONObject("payload").getJSONArray("results").length()>0){

                        updateService();
                    }else{
                        saveToService();
                    }

                } catch (JSONException e) {
                    //hideProgress();
                    e.printStackTrace();
                }
            }

            @Override
            public void userNotAuthenticated(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
            }
        });
    }

    void saveToService(){
        Map<String, Object> params = new HashMap<>();
        params.put("users_id",String.valueOf(GeneralFunctions.getUserId(getContext())));
        params.put("user_image",GeneralFunctions.getUserImage(getContext()));
        params.put("longitude",mLastKnownLocation.getLongitude());
        params.put("latitude",mLastKnownLocation.getLatitude());
        Log.d(TAG,params.toString());
        App.devless.postData(serviceName, "user_extra_details", params, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                //hideProgress();

                Log.d(TAG,response.toString());
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                //hideProgress();
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                //hideProgress();
                Log.d(TAG,message.toString());
            }
        });
    }

    void updateService(){
        Map<String, Object> params = new HashMap<>();
        // params.put("users_id",GeneralFunctions.getUserId(getContext()));
        params.put("user_image",GeneralFunctions.getUserImage(getContext()));
        params.put("longitude",mLastKnownLocation.getLongitude());
        params.put("latitude",mLastKnownLocation.getLatitude());
        params.put("where","id,"+GeneralFunctions.getUserId(getContext()));
        App.devless.edit(serviceName, "user_extra_details", params, GeneralFunctions.getUser(getContext()), new EditDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {

                Log.d(TAG,response.toString());
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                //hideProgress();
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                //hideProgress();
                Log.d(TAG,message.toString());
            }
        });
    }
}
