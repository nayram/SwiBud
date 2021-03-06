package com.dev.swibud.activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.fragments.BaseFragment;
import com.dev.swibud.fragments.FragmentDisplayFriendLocations;
import com.dev.swibud.fragments.FriendsListFragment;
import com.dev.swibud.fragments.MeetupFragment;
import com.dev.swibud.fragments.ProfileFragment;
import com.dev.swibud.fragments.Profile_Fragment;
import com.dev.swibud.interfaces.AuthInterface;
import com.dev.swibud.interfaces.BackHandlerInterface;
import com.dev.swibud.interfaces.MainServiceInterface;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.GeneralFunctions;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;


import org.json.JSONException;
import org.json.JSONObject;

import androidsdk.devless.io.devless.interfaces.LogoutResponse;
import androidsdk.devless.io.devless.interfaces.SignUpResponse;
import androidsdk.devless.io.devless.main.Devless;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.Payload;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MainServiceInterface,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,BackHandlerInterface {

    @BindView(R.id.main_content)
    ConstraintLayout main_content;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    String fname,lname,phone,password;
    String TAG=getClass().getName();
    JSONObject userObj;
    View navView;
    TextView headerName;
    CircleImageView imgHeader;
    public GoogleApiClient googleApiClient;
    FragmentDisplayFriendLocations fragmentDisplayFriendLocations;
    android.support.v4.app.FragmentManager fm;
    private BaseFragment selectedFragment;
    ProgressDialog pDialog;

    Switch swVisibility;
    TextView tvActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.switch_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);


        Log.d(TAG,"package name: "+getPackageName());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        tvActionBarTitle=(TextView)findViewById(R.id.action_bar_title);
        swVisibility = (Switch)findViewById(R.id.switchAB);

        swVisibility.setChecked(GeneralFunctions.isVisible());

        swVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                GeneralFunctions.setVisibility(b);
                if (b)
                Toast.makeText(MainActivity.this, "Visibility mode activated", Toast.LENGTH_SHORT).show();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navView=navigationView.getHeaderView(0);
        headerName=(TextView)navView.findViewById(R.id.tvNavName);
        imgHeader=(CircleImageView)navView.findViewById(R.id.imgNav);
        imgHeader.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
        String user=GeneralFunctions.getUser(this);
        try {
            userObj=new JSONObject(user);
            if(!userObj.getString("first_name").equalsIgnoreCase("null"))
            headerName.setText(userObj.getString("first_name")+" "+userObj.getString("last_name"));

            if (userObj.has("username")){
                Constants.USER_ID=userObj.getString("username");
            }else{
                Constants.USERID=userObj.getString("first_name")+"_"+userObj.getString("last_name");
            }
            if (GeneralFunctions.getUserImage(this)!=null){

                String img=GeneralFunctions.getUserImage(this);
                if (!img.isEmpty() && !img.equalsIgnoreCase("null"))
                Glide.with(this)
                        .load(img)
                        .into(imgHeader);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setUpGClient();
        fm = getSupportFragmentManager();

        fragmentDisplayFriendLocations=new FragmentDisplayFriendLocations();
        fragmentDisplayFriendLocations.googleApiClient=googleApiClient;
        fragmentDisplayFriendLocations.getDeviceLocation();
        fragmentDisplayFriendLocations.updateLocationUI();
        pDialog=new ProgressDialog(this);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setMessage("Setting up user profile");
        setFragmentContent(fragmentDisplayFriendLocations, Constants.DISPLAY_FRIENDS_ON_MAP);

        boolean isSendbirdConnect=GeneralFunctions.isSendBirdConnected(this);

        Log.d(TAG,String.valueOf(isSendbirdConnect));
        if (!isSendbirdConnect){
            boolean isSendBirdLogin=GeneralFunctions.isSendBirdLogin(this);
            String acc_token=GeneralFunctions.getSendBirdAccessToken(MainActivity.this);
            if (isSendBirdLogin && acc_token !=null){
                SendBird.connect(Constants.USER_ID,acc_token, new SendBird.ConnectHandler() {
                    @Override
                    public void onConnected(User user, SendBirdException e) {
                        if (e != null) {
                            e.printStackTrace();
                            // Error.
                            return;
                        }
                        GeneralFunctions.setSendBirdConnect(MainActivity.this,true);
                        Log.d(TAG,user.toString());

                    }
                });
            }else{
                com.dev.swibud.pojo.User body= new com.dev.swibud.pojo.User();
                body.user_id=Constants.USER_ID;
                body.nickname=headerName.getText().toString();
                body.issue_access_token=true;
                String url=GeneralFunctions.getUserImage(this);
                if (url !=null && !url.isEmpty() && !url.equals("null"))
                body.profile_url=GeneralFunctions.getUserImage(this);

                pDialog.show();
                loginSendBirdUser(body);
            }

        }
        if (!GeneralFunctions.tutorialShown()){
            GeneralFunctions.setTutorialVisibility(true);
            TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.switchAB),
                            "Visibility Mode", "This feature is to show or hide your location from your friends")
                        /*.outerCircleColor(R.color.red)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.red)  // Specify the color of the description text
                        .textColor(R.color.blue)            // Specify a color for both the title and description text
                        // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)   */        // Specify a custom drawable to draw as the target
                            .targetRadius(30),null);

        }
        /*else{
            com.dev.swibud.pojo.User body= new com.dev.swibud.pojo.User();
            body.user_id=Constants.USER_ID;
            body.nickname=headerName.getText().toString();
            body.issue_access_token=true;
            String url=GeneralFunctions.getUserImage(this);
            if (url !=null && !url.isEmpty() && !url.equals("null"))
                body.profile_url=GeneralFunctions.getUserImage(this);

            pDialog.show();
            loginSendBirdUser(body);

        }*/
        //fm.beginTransaction().replace(R.id.main_content, fragmentDisplayFriendLocations).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            setTitle("SwiBud");
            tvActionBarTitle.setText("SwiBud");

            setFragmentContent(fragmentDisplayFriendLocations,Constants.DISPLAY_FRIENDS_ON_MAP);

//            setFragmentContent(fragmentDisplayFriendLocations,Constants.DISPLAY_FRIENDS_ON_MAP);
            //setFragmentContent(frag,Constants.DISPLAY_FRIENDS_ON_MAP);
           // GeneralFunctions.addFragmentFromRight(fm,frag,R.id.main_content);
        } else if (id == R.id.nav_contacts) {
            setTitle("People");
            tvActionBarTitle.setText("People");
            setFragmentContent(new FriendsListFragment(),Constants.CONTACTS);
            //GeneralFunctions.addFragmentFromRight(fm,new FriendsListFragment(),R.id.main_content);

        } else if (id == R.id.nav_meetups) {
            setTitle("Meetups");
            tvActionBarTitle.setText("Meetups");
            setFragmentContent(new MeetupFragment(),Constants.MEETUP);
            //GeneralFunctions.addFragmentFromRight(fm,new MeetupFragment(),R.id.main_content);

        } else if (id == R.id.nav_account) {
            setTitle("Profile");
            tvActionBarTitle.setText("Profile");
            setFragmentContent(new Profile_Fragment(),Constants.ACCOUNT);
            //GeneralFunctions.addFragmentFromRight(fm,new Profile_Fragment(),R.id.main_content);

        } else if (id == R.id.nav_logout) {

            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Log out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GeneralFunctions.logout(MainActivity.this);
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                            finish();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void completeProfile(){
      //  startActivity(new Intent(getActivity(),MainActivity.class));

    }

    private synchronized void setUpGClient() {
        if (googleApiClient==null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this,0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }

    }

    @Override
    public void updateProfileDetails() {
        String user=GeneralFunctions.getUser(this);
        try {
            userObj=new JSONObject(user);
            headerName.setText(userObj.getString("first_name")+" "+userObj.getString("last_name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateProfileImage() {
        if (GeneralFunctions.getUserImage(this)!=null){
            String img=GeneralFunctions.getUserImage(this);
            Glide.with(this)
                    .load(img)
                    .into(imgHeader);
        }
    }

    @Override
    public void setLocationSnackBar(String message) {
        showSnackbar(message);
    }

    public void showSnackbar(String message){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

        snackbar.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        fragmentDisplayFriendLocations.getLocationPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void setFragmentContent(Fragment fragment,String fragTag){
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment,fragTag);
        fragmentTransaction.commit();
    }

    @Override
    public void setSelectedFragment(BaseFragment backHandledFragment) {
        this.selectedFragment = backHandledFragment;
    }

    void signupSendBird(final com.dev.swibud.pojo.User user){
        App.swibudServices.registerSendBirdUser(user).enqueue(new Callback<com.dev.swibud.pojo.User>() {
            @Override
            public void onResponse(Call<com.dev.swibud.pojo.User> call, Response<com.dev.swibud.pojo.User> response) {
                Log.d(TAG,response.toString());
                pDialog.dismiss();
                Log.d(TAG, new Gson().toJson(response));
                if (response.body()!=null && !response.body().access_token.isEmpty()){
                    GeneralFunctions.setSendBirdAccessToken(MainActivity.this,response.body().access_token);
                    GeneralFunctions.setSendBirdLogin(MainActivity.this,true);
                    connectSendBirdUser(user.user_id,user.access_token);
                }

            }

            @Override
            public void onFailure(Call<com.dev.swibud.pojo.User> call, Throwable t) {
                pDialog.dismiss();
                t.printStackTrace();

            }
        });
    }

    void loginSendBirdUser(final com.dev.swibud.pojo.User user){
        App.swibudServices.getCurrentUser(user.user_id).enqueue(new Callback<com.dev.swibud.pojo.User>() {
            @Override
            public void onResponse(Call<com.dev.swibud.pojo.User> call, Response<com.dev.swibud.pojo.User> response) {
                Log.d(TAG,new Gson().toJson(response));
                if (response.body()!=null && response.body().access_token !=null && !response.body().access_token.isEmpty()){

                    GeneralFunctions.setSendBirdLogin(MainActivity.this,true);
                    GeneralFunctions.setSendBirdAccessToken(MainActivity.this,response.body().access_token);
                    connectSendBirdUser(user.user_id,response.body().access_token);
                    pDialog.dismiss();

                }else{
                    signupSendBird(user);
                }
            }

            @Override
            public void onFailure(Call<com.dev.swibud.pojo.User> call, Throwable t) {
                t.printStackTrace();
                signupSendBird(user);
            }
        });
    }

    void connectSendBirdUser(String userId,String acc_token){
        SendBird.connect(userId,acc_token, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    e.printStackTrace();
                    // Error.
                    return;
                }
                GeneralFunctions.setSendBirdConnect(MainActivity.this,true);
                Log.d(TAG,user.toString());

            }
        });
    }
}
