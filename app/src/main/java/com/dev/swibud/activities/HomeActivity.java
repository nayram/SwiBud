package com.dev.swibud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.fragments.PhoneNumberAuthFragment;
import com.dev.swibud.pojo.Users;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.fragments.LoginFragment;
import com.dev.swibud.fragments.RegisterPasswordFragment;
import com.dev.swibud.fragments.RegisterNameFragment;
import com.dev.swibud.interfaces.AuthInterface;
import com.google.gson.Gson;

import androidsdk.devless.io.devless.interfaces.SignUpResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.Payload;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nayrammensah on 8/2/17.
 */

public class HomeActivity extends AppCompatActivity implements AuthInterface{

    android.support.v4.app.FragmentManager mFragmentManager;
    String fname,lname,phone,password;
    String TAG=getClass().getName();

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        if (GeneralFunctions.getUser(getApplicationContext()) !=null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }


        mFragmentManager= getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.llHomeContainer,new PhoneNumberAuthFragment(),"home").addToBackStack("home").commit();

    }



    void setFragmentContent(android.support.v4.app.Fragment fragment, String url){
        android.support.v4.app.FragmentTransaction transaction =mFragmentManager.beginTransaction();
        transaction.replace(R.id.llHomeContainer,fragment,url);
        transaction.addToBackStack(url);
        transaction.commit();
    }

    @Override
    public void addFragment(String uri) {
        switch (uri){
            case "register_name":
                GeneralFunctions.addFragmentFromRightWithTag(mFragmentManager,new RegisterNameFragment(),R.id.llHomeContainer,uri);
                //setFragmentContent(new RegisterNameFragment(),uri);
                break;
            case "register_email":
                GeneralFunctions.addFragmentFromRightWithTag(mFragmentManager,new RegisterPasswordFragment(),R.id.llHomeContainer,uri);
                //setFragmentContent(new RegisterPasswordFragment(),uri);
                break;
            case "register_phone":
               // setFragmentContent();
                break;

        }
    }

    @Override
    public void removeFragment() {
        if (mFragmentManager.getBackStackEntryCount() > 0)
            mFragmentManager.popBackStack();
        else finish();
    }

    @Override
    public void addFirstLastName(String fname, String lname) {
        this.fname=fname;
        this.lname=lname;
    }

    @Override
    public void addPhoneNumber(String phone) {
        this.phone=phone;
    }

    @Override
    public void addPassword(String password) {
        this.password=password;
    }

    @Override
    public void registerCredentials() {
        progressBar.setVisibility(View.VISIBLE);
        App.devless.signUpWithPhoneNumberAndPassword(phone, password, App.sp, new SignUpResponse() {
            @Override
            public void onSignUpSuccess(Payload payload) {
                progressBar.setVisibility(View.GONE);
                Users.UsersResults user=new Gson().fromJson(payload.toString(),Users.UsersResults.class);
                Log.d(TAG,payload.toString());
                GeneralFunctions.saveUser(new Gson().toJson(user),getApplicationContext());
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                finish();

            }

            @Override
            public void onSignUpFailed(ErrorMessage errorMessage) {
                progressBar.setVisibility(View.GONE );
                Log.e(TAG,errorMessage.toString());
                Toast.makeText(getApplicationContext(),"User already exist",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
