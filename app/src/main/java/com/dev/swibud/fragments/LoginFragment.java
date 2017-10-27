package com.dev.swibud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.interfaces.AuthInterface;

import androidsdk.devless.io.devless.interfaces.SignUpResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.Payload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nayrammensah on 8/2/17.
 */

public class LoginFragment extends Fragment {

    AuthInterface authCallback;
    Context ctx;
    String TAG=getClass().getName();

    @BindView(R.id.phone)
    EditText phone;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.email_sign_in_button)
    Button email_sign_in_button;

    @BindView(R.id.login_progress)
    ProgressBar login_progresss;

    @OnClick(R.id.email_sign_in_button) void login(){
        if(!phone.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){
            login_progresss.setVisibility(View.VISIBLE);
            login(phone.getText().toString(),password.getText().toString());
        }else{
            Toast.makeText(getActivity(), "Provide phone number and password", Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R.id.tvRegister) void register(){


        GeneralFunctions.addFragmentFromRight(getActivity().getSupportFragmentManager(),new RegisterNameFragment(),R.id.llHomeContainer);
        //authCallback.addFragment("register_name");
    }

    @OnClick(R.id.imgClose) void close(){
        getActivity().finish();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.activity_login,container,false);
        ButterKnife.bind(this,rootView);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;
        if (context instanceof AuthInterface) {
            authCallback = (AuthInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AuthInterface");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        authCallback = null;
    }

    void login(String phone,String password){
        App.devless.signUpWithPhoneNumberAndPassword(phone, password, App.sp, new SignUpResponse() {
            @Override
            public void onSignUpSuccess(Payload payload) {
                login_progresss.setVisibility(View.GONE);
                Log.d(TAG,"Success Payload :"+payload.toString());
            }

            @Override
            public void onSignUpFailed(ErrorMessage errorMessage) {
                login_progresss.setVisibility(View.GONE);
                Log.d(TAG,"Error: "+errorMessage.toString());
            }
        });
    }


}
