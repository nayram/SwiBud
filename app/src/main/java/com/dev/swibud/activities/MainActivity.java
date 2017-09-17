package com.dev.swibud.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.fragments.FragmentDisplayFriendLocations;
import com.dev.swibud.fragments.FriendsListFragment;
import com.dev.swibud.fragments.MeetupFragment;
import com.dev.swibud.fragments.ProfileFragment;
import com.dev.swibud.fragments.Profile_Fragment;
import com.dev.swibud.interfaces.AuthInterface;
import com.dev.swibud.interfaces.MainServiceInterface;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.GeneralFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import androidsdk.devless.io.devless.interfaces.SignUpResponse;
import androidsdk.devless.io.devless.main.Devless;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.Payload;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MainServiceInterface {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navView=navigationView.getHeaderView(0);
        headerName=(TextView)navView.findViewById(R.id.tvNavName);
        imgHeader=(CircleImageView)navView.findViewById(R.id.imgNav);
        String user=GeneralFunctions.getUser(this);
        try {
            userObj=new JSONObject(user);
            headerName.setText(userObj.getString("first_name")+" "+userObj.getString("last_name"));
            if (GeneralFunctions.getUserImage(this)!=null){
                String img=GeneralFunctions.getUserImage(this);
                Glide.with(this)
                        .load(img)
                        .into(imgHeader);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.main_content, new FragmentDisplayFriendLocations()).commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            GeneralFunctions.addFragmentFromRight(getSupportFragmentManager(),new FragmentDisplayFriendLocations(),R.id.main_content);
        } else if (id == R.id.nav_contacts) {
            GeneralFunctions.addFragmentFromRight(getSupportFragmentManager(),new FriendsListFragment(),R.id.main_content);

        } else if (id == R.id.nav_meetups) {
            GeneralFunctions.addFragmentFromRight(getSupportFragmentManager(),new MeetupFragment(),R.id.main_content);

        } else if (id == R.id.nav_account) {
            GeneralFunctions.addFragmentFromRight(getSupportFragmentManager(),new Profile_Fragment(),R.id.main_content);
        } else if (id == R.id.nav_logout) {
            GeneralFunctions.logout(this);
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void completeProfile(){
      //  startActivity(new Intent(getActivity(),MainActivity.class));

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
}