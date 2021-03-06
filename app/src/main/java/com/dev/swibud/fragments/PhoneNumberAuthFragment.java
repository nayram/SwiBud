package com.dev.swibud.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.activities.HomeActivity;
import com.dev.swibud.activities.MainActivity;
import com.dev.swibud.interfaces.AuthInterface;
import com.dev.swibud.pojo.ExtraUserProfile;
import com.dev.swibud.pojo.User;
import com.dev.swibud.pojo.Users;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.GeneralFunctions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidsdk.devless.io.devless.interfaces.LoginResponse;
import androidsdk.devless.io.devless.interfaces.LogoutResponse;
import androidsdk.devless.io.devless.interfaces.RequestResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.interfaces.SignUpResponse;
import androidsdk.devless.io.devless.main.Devless;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.Payload;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nayrammensah on 8/4/17.
 */

public class PhoneNumberAuthFragment extends Fragment {


    AuthInterface authCallback;
    Context ctx;
    Activity act;

    public PhoneNumberAuthFragment() {
    }

    @BindView(R.id.edtPhone)
    EditText edtPhone;

    @BindView(R.id.ccp)
    CountryCodePicker ccp;

    @BindView(R.id.llPhoneNumber)
    LinearLayout llPhoneNumber;

    @BindView(R.id.llVerification)
    LinearLayout llVerification;

    @BindView(R.id.edtVerification)
    EditText edtVerification;

    @BindView(R.id.tvCaption)
    TextView tvCaption;

    String TAG=getClass().getName();
    String mVerificationId;

    PhoneAuthProvider.ForceResendingToken mResendToken;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthCredential mCredential;

    boolean isVerificationStage=false;

    ProgressDialog pDialog;

    @OnClick(R.id.fabNext) void next() {

        App.devless.logout(new LogoutResponse() {
            @Override
            public void onLogOutSuccess(ResponsePayload response) {
                Log.d(TAG,String.valueOf(response));
                login();
            }
        });


    }

    @OnClick(R.id.imgBack) void back(){
        getActivity().finish();

    }

    @OnClick(R.id.btnResendCode) void resendCode(){
        isVerificationStage=true;
        pDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                ccp.getFullNumber()+edtPhone.getText().toString(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                mResendToken);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.frag_phone_auth,container,false);
        ButterKnife.bind(this,rootView);
        pDialog=new ProgressDialog(getActivity());
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading..");
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                pDialog.dismiss();
                isVerificationStage=true;
                llPhoneNumber.setVisibility(View.GONE);
                tvCaption.setText("Enter verification code");
                llVerification.setVisibility(View.VISIBLE);
                edtVerification.setText(credential.getSmsCode());

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(TAG, "onVerificationFailed", e);
                pDialog.dismiss();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    //mPhoneNumberField.setError("Invalid phone number.");
                    isVerificationStage=false;
                    tvCaption.setText("Enter your phone number :");
                    llVerification.setVisibility(View.GONE);
                    llPhoneNumber.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Credentials");
                    Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.e(TAG, "Too many");
                    Toast.makeText(getActivity(), "Quota exceeded.", Toast.LENGTH_SHORT).show();;
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

//                super.onCodeSent(verificationId, token);
                Log.d(TAG, "onCodeSent:" + verificationId);
                pDialog.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;
                isVerificationStage=true;
                tvCaption.setText("Enter verification Code");
                llVerification.setVisibility(View.VISIBLE);
                llPhoneNumber.setVisibility(View.GONE);




            }
        };

        return rootView;
    }



    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            pDialog.show();
                            Log.d("PhoneVERifier", "signInWithCredential:success");
                            signUpUser();
                        } else {
                            pDialog.dismiss();
                            Log.w("PhoneVERifier", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                pDialog.dismiss();
                                Toast.makeText(getActivity(), "Verification code is invalid! Request for a new code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    void loginUser(){

        ArrayList<String> list=new ArrayList<>(Arrays.asList(null,
                ccp.getFullNumber() + edtPhone.getText(),null,
                edtPhone.getText().toString(),null,null,null
        ));

//        Log.d(TAG,"Phone number "+ccp.getFullNumber()+edtPhone.getText().toString());
        Log.d(TAG,"password "+ edtPhone.getText().toString());
        App.devless.loginWithPhoneNumberAndPassword(ccp.getFullNumber() + edtPhone.getText().toString(),
                edtPhone.getText().toString(), App.sp, new LoginResponse() {
            @Override
            public void onLogInSuccess(ResponsePayload payload) {
                Log.e(TAG,payload.toString());


                try {
                    JSONObject jsob=new JSONObject(payload.toString());
                    Log.d(TAG+" Login",jsob.toString());
                    JSONObject results=jsob.getJSONObject("profile");
                    GeneralFunctions.saveUser(results.toString(),getContext());

                    GeneralFunctions.setUserId(getActivity(),results.getInt("id"));
                    //launchProfile();
                    getUserData();


                } catch (JSONException e) {
                    pDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(ctx, "Log in failed! Try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLogInFailed(ErrorMessage errorMessage) {
                pDialog.dismiss();
               Log.e("LoginFailed","LoginFailed "+errorMessage.toString());
                Toast.makeText(ctx, "Log in failed! Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getUserData(){
        Map<String, Object> params = new HashMap<>();
        params.put("where","users_id,"+GeneralFunctions.getUserId());
        App.devless.search("UserExraDetails", "user_extra_details", params, new SearchResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject=new JSONObject(response.toString());
                    if (jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).length()>0){

                        JSONObject profile=jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).getJSONObject(0);
                        ExtraUserProfile extraUserProfile=new ExtraUserProfile(profile.getDouble(Constants.LAT),
                                profile.getDouble(Constants.LNG),profile.getString(Constants.USER_IMAGE),profile.getInt(Constants.ID));
                        GeneralFunctions.setUserExtraDetail(getContext(),new Gson().toJson(extraUserProfile));
                        GeneralFunctions.saveUserImage(profile.getString(Constants.USER_IMAGE),getContext());

                    }

                }catch (JSONException ex){
                    ex.printStackTrace();
                }

                startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finish();
            }

            @Override
            public void userNotAuthenticated(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
                pDialog.dismiss();
                startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finish();
            }
        });
    }

    void signUpUser(){
        //Log.d(TAG,"Phone number "+ccp.getFullNumberWithPlus()+edtPhone.getText().toString());
        Log.d(TAG,"password "+ edtPhone.getText().toString());
        App.devless.signUpWithPhoneNumberAndPassword(ccp.getFullNumber() + edtPhone.getText().toString(),
                edtPhone.getText().toString(), App.sp, new SignUpResponse() {
                    @Override
                    public void onSignUpSuccess(Payload payload) {
                        pDialog.dismiss();
                        Log.d("SignUpSuccess",new Gson().toJson(payload.toString()));
                        try {
                            JSONObject jobj=new JSONObject(payload.toString());
                            JSONObject profile=jobj.getJSONObject("result").getJSONObject("profile");
                            Log.d(TAG,profile.toString());
                            GeneralFunctions.saveUser(profile.toString(),getActivity());
                            GeneralFunctions.setUserId(getActivity(),profile.getInt("id"));
                            launchProfile();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Sign up failed. Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSignUpFailed(ErrorMessage errorMessage) {
                        Log.e("SignUpFailed","SignUpFailed "+errorMessage.toString());
                        loginUser();
                    }
                });
    }

    void launchProfile(){
        ProfileFragment profileFragment=new ProfileFragment();
        Bundle basket=new Bundle();
        basket.putBoolean("Auth",true);
        GeneralFunctions.addFragmentFromRight(getActivity().getSupportFragmentManager(),profileFragment,R.id.llHomeContainer);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;
        if (context instanceof AuthInterface) {
            authCallback = (AuthInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    void login(){
        if (!edtPhone.getText().toString().trim().isEmpty() && edtPhone.getText().toString().trim().length() <=10) {
            Log.d(TAG,"Phone Number "+ccp.getFullNumberWithPlus() + edtPhone.getText().toString());


            if (!isVerificationStage){
                pDialog.show();
//               isVerificationStage Toast.makeText(ctx, ccp.getFullNumberWithPlus() + edtPhone.getText().toString(), Toast.LENGTH_SHORT).show();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        ccp.getFullNumberWithPlus() + edtPhone.getText().toString(),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        getActivity(),               // Activity (for callback binding)
                        mCallbacks);
            } else{
                if (!edtVerification.getText().toString().trim().isEmpty() && edtVerification.getText().toString().trim().length()==6
                        && !mVerificationId.isEmpty()) {
                    pDialog.show();
                    mCredential = PhoneAuthProvider.getCredential(mVerificationId, edtVerification.getText().toString());
                    signInWithPhoneAuthCredential(mCredential);
                }else {
                    pDialog.dismiss();
                    Toast.makeText(getActivity(), "Please provide a valid verification code", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            pDialog.dismiss();
            Toast.makeText(getActivity(), "Please provide a valid phone number", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        authCallback = null;
    }
}
