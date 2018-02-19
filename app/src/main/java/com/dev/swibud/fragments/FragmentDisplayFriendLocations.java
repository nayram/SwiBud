package com.dev.swibud.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.activities.MainActivity;
import com.dev.swibud.activities.OpenChatActivity;
import com.dev.swibud.adapters.ContactAdapter;
import com.dev.swibud.interfaces.MainServiceInterface;
import com.dev.swibud.pojo.ExtraUserProfile;
import com.dev.swibud.pojo.Users;
import com.dev.swibud.pojo.UsersList;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.utils.PersonRenderer;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.EditDataResponse;
import androidsdk.devless.io.devless.interfaces.GetDataResponse;
import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 8/6/17.
 */

public class FragmentDisplayFriendLocations extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,OnRequestPermissionsResultCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,ClusterManager.OnClusterClickListener<Users>,
        ClusterManager.OnClusterInfoWindowClickListener<Users>, ClusterManager.OnClusterItemClickListener<Users>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Users>{

    private FusedLocationProviderClient mFusedLocationProviderClient;
    MaterialDialog dialog;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    GoogleMap mMap;
    String TAG=getClass().getName();
    Location mLastKnownLocation;
    float DEFAULT_ZOOM= 17;
    boolean updateState=false;

    String serviceName="UserExraDetails";
    ProgressDialog pDialog;

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

    private ClusterManager<Users> mClusterManager;

    UsersList usersArrayList;

    @BindView(R.id.mapProgress)
    ProgressBar mapProgress;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_map,container,false);
        ButterKnife.bind(this,rootView);
        pDialog=new ProgressDialog(getActivity());
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");

        Log.d(TAG,"Firsbase Instanse ID "+FirebaseInstanceId.getInstance().getToken());

        if (GeneralFunctions.getFCMToken() ==null){
            String token= FirebaseInstanceId.getInstance().getToken();
            if (token !=null){
                checkRegAvailability(token);
            }
        }else{
            Log.d(TAG,"Firebase Instance ID "+GeneralFunctions.getFCMToken());
        }
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
//        mMap.setMaxZoomPreference(15);


        mMap.setOnMarkerClickListener(this);


        getDeviceLocation();
       updateLocationUI();
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));

        /*mClusterManager = new ClusterManager<Users>(getActivity(), mMap);
        mClusterManager.setRenderer(new PersonRenderer(getContext(),map,mClusterManager));*/
        /*mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);*/

        addItems();

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minc, 16));

       /* map.addMarker(new MarkerOptions()
                .title("Sydney")5.64159,-0.1545487
                .snippet("The most populous city in Australia.")
                .position(sydney));*/

    }

    private void addItems() {
        mapProgress.setVisibility(View.VISIBLE);
        App.devless.getData("users", "devlessUser", new GetDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                mapProgress.setVisibility(View.GONE);
//                Log.d(TAG,response.toString());
                try {
//                    Log.d("LogUtils",response.toString());
                    JSONObject resp=new JSONObject(response.toString());
//                    Log.d(TAG,resp.toString());
                    usersArrayList=new Gson().fromJson(response.toString(),UsersList.class);
//                    Log.d(TAG,usersArrayList.toString());

                    for (Users users : usersArrayList.payload){
                        if (users.userDetails.size()>0)
                            if (users.getId() != GeneralFunctions.getUserId())
                            if (users.getPosition() !=null){

                                View marker = LayoutInflater.from(getActivity()).inflate(R.layout.custom_marker_pin, null);
                                TextView numTxt = (TextView) marker.findViewById(R.id.tvPerson);
                                ImageView img=(ImageView)marker.findViewById(R.id.imgPerson);
                                numTxt.setText(users.getTitle());
                                Glide.with(getActivity())
                                        .load(users.userDetails.get(0).getUser_image())
                                        .into(img);
//                                Toast.makeText(ctx, "user has location", Toast.LENGTH_SHORT).show();
                                MarkerOptions mo= new MarkerOptions()
                                        .position(users.getPosition())
                                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker)));

                                mMap.addMarker(mo).setTag(users);


                            }
                        if (mLastKnownLocation !=null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        else {
                            mMap.moveCamera(CameraUpdateFactory.zoomIn());
                        }
                        
                    }
                    
//                   mClusterManager.cluster();
                    // getFollowers(resp);
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getContext(), "Failed to load Contacts", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                mapProgress.setVisibility(View.GONE);
//                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                mapProgress.setVisibility(View.GONE);
//                Log.d(TAG,message.toString());
            }
        });
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

    public void showDialog(final Users users){
//        Log.d("Friends",name);
        boolean wrapInScrollView = true;

        View user_view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user,null);
        TextView textView1=(TextView)user_view.findViewById(R.id.tvDialogUserName);
        CircleImageView img=(CircleImageView) user_view.findViewById(R.id.tvDialogUserProfile);
        textView1.setText(users.getTitle());
        Button btnChat=(Button)user_view.findViewById(R.id.btnDialogUser);
        Glide.with(getActivity())
                .load(users.userDetails.get(0).getUser_image())
                .into(img);

        dialog=new MaterialDialog.Builder(getActivity())
                .customView(user_view, wrapInScrollView).show();

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userString=GeneralFunctions.getUser(getActivity());
//                Log.d(TAG,userString);
                try {
                    final JSONObject object=new JSONObject(userString);
//                    mSelected.add(object.getString("username"));

                    String access=GeneralFunctions.getSendBirdAccessToken(getActivity());


                    pDialog.show();
                    SendBird.connect(object.getString("username"),access, new SendBird.ConnectHandler() {
                        @Override
                        public void onConnected(User user, SendBirdException e) {
                            if (e != null) {
                                pDialog.dismiss();
                                e.printStackTrace();
                                // Error.
                                Toast.makeText(getContext(), "Failed to establish connection", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            registerFcm(users,user.getUserId());


                        }
                    });

                    /*String access=GeneralFunctions.getSendBirdAccessToken(getActivity());

                    Log.d(TAG,access);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                
                dialog.dismiss();
            }
        });

    }


    void registerFcm(final Users users ,final String userId){
        if (FirebaseInstanceId.getInstance().getToken() == null){

            pDialog.dismiss();
            Toast.makeText(getContext(), "Failed to establish connection", Toast.LENGTH_SHORT).show();
            return;
        }

        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
                new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                        if (e != null) {
                            pDialog.dismiss();
                            e.printStackTrace();
                            // Error.
                            Toast.makeText(getContext(), "Failed to establish connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<String> mSelected=new ArrayList<>();
                        mSelected.add(users.username);
                        mSelected.add(userId);
                        createGroupChannel(mSelected,users.getTitle(),true);
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
                if (mLastKnownLocation!=null)
                checkUserIdAvailability();
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

            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    void getMyLocation(){
        mapProgress.setVisibility(View.VISIBLE);
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED){
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3600);
            locationRequest.setFastestInterval(3600);
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
                                //Log.d(TAG,mLastKnownLocation.toString());
                                if (mLastKnownLocation !=null){
                                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastKnownLocation.getLatitude(),
                                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                    GeneralFunctions.setLatitude(getContext(), (float) mLastKnownLocation.getLatitude());
                                    GeneralFunctions.setLongitude(getContext(), (float) mLastKnownLocation.getLongitude());
                                    checkUserIdAvailability();
                                    //setUserLocation(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLatitude());
                                }else {
                                    mMap.moveCamera(CameraUpdateFactory.zoomIn());
                                    mapProgress.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), "Not getting last known location", Toast.LENGTH_SHORT).show();
                                }

                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            mapProgress.setVisibility(View.GONE);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG +": ACTIVITY RESULT "+resultCode,data.toString());
        if (requestCode == REQUEST_CHECK_SETTINGS_GPS && resultCode == Activity.RESULT_OK){
            getMyLocation();
        }
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
    public boolean onMarkerClick(Marker marker) {;

        Users users= (Users) marker.getTag();
        showDialog(users);

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null) {
            /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(),
                            location.getLongitude()), DEFAULT_ZOOM));*/
            mapProgress.setVisibility(View.VISIBLE);
            updateLocationUI();
        }

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
        int id=GeneralFunctions.getUserId();
        Map<String, Object> dataToPost = new HashMap<>();
        dataToPost.put("lat",latitude);
        dataToPost.put("lng",longitude);
        dataToPost.put("user_id",id);

        showProgress(true);
        App.devless.postData("location", "user_location", dataToPost, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
//                Log.d(TAG,response.toString());
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
//                Log.d(TAG,errorMessage.toString());
            }


            @Override
            public void userNotAuthenticated(ErrorMessage message) {
//                Log.d(TAG,message.toString());
            }
        });


    }

    void checkUserIdAvailability(){

//        Toast.makeText(ctx, "Check UserId Availability", Toast.LENGTH_SHORT).show();
        Map<String, Object> params = new HashMap<>();
        params.put("where","users_id,"+GeneralFunctions.getUserId());
        App.devless.search("UserExraDetails", "user_extra_details", params, new SearchResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
               // hideProgress();
//                 Toast.makeText(ctx, "Search Success", Toast.LENGTH_SHORT).show();
//                Log.d(TAG+": "+serviceName,response.toString());
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
                    if (jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).length()>0){
//                        Toast.makeText(ctx, "Update Location Success", Toast.LENGTH_SHORT).show();
                        JSONObject profile=jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).getJSONObject(0);
                        updateService(profile);
                    }else{
//                        Toast.makeText(ctx, "Save Location Success", Toast.LENGTH_SHORT).show();
                        saveToService();
                    }

                } catch (JSONException e) {
                    //hideProgress();
//                    isHidden();
                    mapProgress.setVisibility(View.VISIBLE);
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
        params.put("users_id",String.valueOf(GeneralFunctions.getUserId()));
        params.put("user_image",GeneralFunctions.getUserImage(getContext()));
        params.put("longitude",mLastKnownLocation.getLongitude());
        params.put("latitude",mLastKnownLocation.getLatitude());
        params.put("fcm_token",FirebaseInstanceId.getInstance().getToken());
//        Log.d(TAG,params.toString());
        App.devless.postData(serviceName, "user_extra_details", params, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                //hideProgress();
                mapProgress.setVisibility(View.GONE);
                Log.d(TAG,"Save FCM "+response.toString());
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                //hideProgress();
                mapProgress.setVisibility(View.GONE);
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                //hideProgress();
                mapProgress.setVisibility(View.GONE);
                Log.d(TAG,message.toString());
            }
        });
    }

    void updateService(JSONObject proile){
        Map<String, Object> params = new HashMap<>();
         params.put("users_id",GeneralFunctions.getUserId());
        params.put("user_image",GeneralFunctions.getUserImage(getContext()));
        params.put("longitude",mLastKnownLocation.getLongitude());
        params.put("latitude",mLastKnownLocation.getLatitude());
        final String token=FirebaseInstanceId.getInstance().getToken();
        params.put("fcm_token",token);
        try {
            int id=proile.getInt("id");
            App.devless.edit(serviceName, "user_extra_details", params,String.valueOf(id), new EditDataResponse() {
                @Override
                public void onSuccess(ResponsePayload response) {
                    mapProgress.setVisibility(View.GONE);
                    Log.d(TAG,"Update FCM "+response.toString());
                    GeneralFunctions.setFCMToken(token);
                }

                @Override
                public void onFailed(ErrorMessage errorMessage) {
                    //hideProgress();
                    mapProgress.setVisibility(View.GONE);
                    Log.d(TAG,errorMessage.toString());
                }

                @Override
                public void userNotAuthenticated(ErrorMessage message) {
                    //hideProgress();
                    mapProgress.setVisibility(View.GONE);
                    Log.d(TAG,message.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void createGroupChannel(List<String> userIds, final String name, boolean distinct) {

        GroupChannel.createChannelWithUserIds(userIds, distinct, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                pDialog.dismiss();
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    Toast.makeText(ctx, "Failed to launch chat window. Try again!", Toast.LENGTH_SHORT).show();
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

    @Override
    public String getTagText() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onClusterClick(Cluster<Users> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().first_name;
       // Toast.makeText(getContext(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Users> cluster) {

    }

    @Override
    public boolean onClusterItemClick(Users item) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Users item) {

    }


    void updateService(JSONObject proile,String token){
        Map<String, Object> params = new HashMap<>();
        params.put("users_id",GeneralFunctions.getUserId());
        params.put("fcm_reg_id",token);
        try {
            int id=proile.getInt("id");
            App.devless.edit("devless", "user_profile", params,String.valueOf(id), new EditDataResponse() {
                @Override
                public void onSuccess(ResponsePayload response) {

                    Log.d(TAG,response.toString());
                }

                @Override
                public void onFailed(ErrorMessage errorMessage) {

                }

                @Override
                public void userNotAuthenticated(ErrorMessage message) {

                    Log.d(TAG,message.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void checkRegAvailability(final String token){

//        Toast.makeText(ctx, "Check UserId Availability", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"CHECK AVAILABILITY");
        Map<String, Object> params = new HashMap<>();
        params.put("where","users_id,"+GeneralFunctions.getUserId());

        App.devless.search("devless", "user_profile", params, new SearchResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                Log.d(TAG, "Availabiltty "+response.toString());
                try {
                    JSONObject jsonObject=new JSONObject(response.toString());
                    if (jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).length()>0){
//                        Toast.makeText(ctx, "Update Location Success", Toast.LENGTH_SHORT).show();
                        JSONObject profile=jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).getJSONObject(0);
                        updateService(profile,token);
                    }else{
//                        Toast.makeText(ctx, "Save Location Success", Toast.LENGTH_SHORT).show();
                        saveToService(token);
                    }

                } catch (JSONException e) {
                    //hideProgress();
                    e.printStackTrace();

                }
            }



            @Override
            public void userNotAuthenticated(ErrorMessage errorMessage) {
                Log.e(TAG,errorMessage.toString());
            }
        });
    }

    void saveToService(final String token){

        Map<String, Object> params = new HashMap<>();
        params.put("users_id",String.valueOf(GeneralFunctions.getUserId()));
        params.put("fcm_reg_id",token);
        Log.d(TAG,params.toString());
        App.devless.postData("devless", "user_profile", params, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                //hideProgress();
                Log.d(TAG,response.toString());
                GeneralFunctions.saveToken(token);

            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                //hideProgress();
                Log.d(TAG,errorMessage.toString());

            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {

                Log.d(TAG,message.toString());
            }
        });
    }
}
