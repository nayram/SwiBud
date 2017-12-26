package com.dev.swibud.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.ListenerService;
import com.dev.swibud.R;
import com.dev.swibud.activities.MainActivity;
import com.dev.swibud.pojo.ExtraUserProfile;
import com.dev.swibud.pojo.Users;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.Constants;
import com.dev.swibud.utils.GeneralFunctions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.EditDataResponse;
import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.interfaces.RequestResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by nayrammensah on 8/18/17.
 */

public class ProfileFragment extends Fragment {

    @BindView(R.id.edtFirstName)
    EditText edtFirstName;
    @BindView(R.id.edtLastName)
    EditText edtLastName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPhone)
    EditText edtPhone;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.imgView)
    ImageView imgView;

    @BindView(R.id.edtUserName)
    EditText edtUserName;

    @BindView(R.id.profile_image)
    CircleImageView profile_image;

    @BindView(R.id.progress_bar_profile)
    ProgressBar progress_bar_profile;

    @BindView(R.id.imgLoading)
    RelativeLayout imgLoading;

    String TAG=getClass().getName();

    JSONObject userJson;

    private String serviceName="UserExraDetails";

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;

    @OnClick(R.id.btnSaveProfile) void saveProfile(){
        validateInputs();

    }

    @OnClick(R.id.profile_image) void openImagePicker(){
        launchImagePicker();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.profile,container,false);
        ButterKnife.bind(this,rootView);
        if (getArguments()==null){
            toolbar.setVisibility(View.GONE);
        }else{
            toolbar.setTitle("Setup Profile");
            toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_close));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }
        String user= GeneralFunctions.getUser(getContext());
        if (user!=null){
            Log.d(TAG,user);
            try {
                userJson=new JSONObject(user);
                if (userJson!=null){
                    if (userJson.getString("first_name")!=null && !userJson.getString("first_name").equals("null") )
                    edtFirstName.setText(userJson.getString("first_name"));
                    if (userJson.getString("last_name") !=null  && !userJson.getString("last_name").equals("null"))
                    edtLastName.setText(userJson.getString("last_name"));
                    if (userJson.getString("phone_number") !=null  && !userJson.getString("phone_number").equals("null"))
                    edtPhone.setText(userJson.getString("phone_number"));
                    if (userJson.getString("email")!=null  && !userJson.getString("email").equals("null"))
                    edtEmail.setText(userJson.getString("email"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*Users userObj=new Gson().fromJson(user, Users.class);

            Toast.makeText(getActivity(), new Gson().toJson(userObj), Toast.LENGTH_SHORT).show();
            if (userObj!=null){
                Users profile=userObj;
                if (profile!=null) {
                    if (profile.first_name != null)
                        edtFirstName.setText(profile.first_name);

                    if (profile.last_name != null)
                        edtLastName.setText(profile.last_name);

                    if (profile.email != null)
                        edtEmail.setText(profile.email);

                    if (profile.phone_number != null)
                        edtPhone.setText(profile.phone_number);
                    }
            }*/
        }else{
            Toast.makeText(getActivity(), "User is null", Toast.LENGTH_SHORT).show();
        }




        return rootView;
    }

    void showProgress(){

        progress_bar_profile.setVisibility(View.VISIBLE);
    }

    void hideProgress(){
        progress_bar_profile.setVisibility(View.GONE);
    }

    void showImgLoading(){
        imgLoading.setVisibility(View.VISIBLE);
    }

    void hideImgLoading(){
        imgLoading.setVisibility(View.GONE);
    }

    void launchImagePicker(){

        int permissionWriteEXTERNAL= ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionWriteEXTERNAL == PackageManager.PERMISSION_GRANTED){
            openPicker();
        }else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

    }




    void launchMainActivity() throws JSONException {

        List<String> params=new ArrayList<>(Arrays.asList(
                edtEmail.getText().toString(),userJson.getString("phone_number"),edtUserName.getText().toString(),edtPhone.getText().toString(),edtFirstName.getText().toString(),
                edtLastName.getText().toString()
        ));
        showProgress();
        //App.devless.updateProfile();
        App.devless.methodCall("devless", "updateProfile", params, new RequestResponse() {
            @Override
            public void onSuccess(ResponsePayload responsePayload) {
                hideProgress();
                Log.d(TAG,responsePayload.toString());

                try {
                    JSONObject jsonObject=new JSONObject(responsePayload.toString());
                    JSONObject user= jsonObject.getJSONObject("payload").getJSONObject("result");
                    if (user.getBoolean("status")){
                        GeneralFunctions.saveUser(user.toString(),getActivity());
                        startActivity(new Intent(getActivity(),MainActivity.class));
                        getActivity().finish();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
                // Users.Profile user=new Gson().fromJson(responsePayload.toString(),Users.Profile.class);
                //GeneralFunctions.saveUser(new Gson().toJson(user.profile),getActivity());
                //startActivity(new Intent(getActivity(), MainActivity.class));
            }

            @Override
            public void userNotAuthenticated(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
                hideProgress();
                Toast.makeText(getActivity(), "Failed to save profile details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void validateInputs() {

        View focusView = null;
        boolean cancel = false;

        edtEmail.setError(null);
        edtUserName.setError(null);
        edtFirstName.setError(null);
        edtLastName.setError(null);
        edtPhone.setError(null);
        edtPhone.setError(null);

        String email = edtEmail.getText().toString();
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String userName = edtUserName.getText().toString();
        String phoneNumber = edtPhone.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            edtFirstName.setError(getString(R.string.error_field_required));
            focusView = edtFirstName;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            edtLastName.setError(getString(R.string.error_invalid_email));
            focusView = edtLastName;
            cancel = true;
        }

        if (TextUtils.isEmpty(userName)) {
            edtUserName.setError(getString(R.string.error_field_required));
            focusView = edtUserName;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getString(R.string.error_field_required));
            focusView = edtUserName;
            cancel = true;
        } else if (!Profile_Fragment.isEmailValid(email)) {
            edtEmail.setError(getString(R.string.error_invalid_email));
            focusView = edtUserName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            try {
                launchMainActivity();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void checkUserIdAvailability(final String imgUrl){
        Map<String, Object> params = new HashMap<>();
        params.put("where","users_id,"+GeneralFunctions.getUserId());
        App.devless.search(serviceName, "user_extra_details", params, new SearchResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                Log.d(TAG,"Check availability "+response.toString());
                try {
                    JSONObject jsonObject=new JSONObject(response.toString());
                    if (GeneralFunctions.getUserExtraDetail(getContext())==null){
                        if (jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).length()>0){
                            JSONObject profile=jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).getJSONObject(0);
                            ExtraUserProfile eup=new ExtraUserProfile(profile.getDouble("latitude"),
                                    profile.getDouble("longitude"),profile.getString("user_image"),profile.getInt("id"));
                            GeneralFunctions.setUserExtraDetail(getContext(),new Gson().toJson(eup));
                        }
                    }
                    if (jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).length()>0){

                        updateService(imgUrl);
                    }else{
                        saveToService(imgUrl);
                    }

                } catch (JSONException e) {
                    hideImgLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void userNotAuthenticated(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
            }
        });
    }

    void saveToService(final String imgUrl){
        Map<String, Object> params = new HashMap<>();
        params.put("users_id",String.valueOf(GeneralFunctions.getUserId()));
        params.put("user_image",imgUrl);
        params.put("longitude",GeneralFunctions.getLongitude(getContext()));
        params.put("latitude",GeneralFunctions.getLatitude(getContext()));
        Log.d(TAG,params.toString());
        App.devless.postData(serviceName, "user_extra_details", params, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                hideImgLoading();
                try {
                    JSONObject jsonObject=new JSONObject(response.toString());
                    if (jsonObject.has(Constants.Payload)){
                        if (jsonObject.getJSONObject(Constants.Payload).has(Constants.EntryId)){
                            ExtraUserProfile eup=new ExtraUserProfile(GeneralFunctions.getLatitude(getContext()),GeneralFunctions.getLatitude(getContext())
                                    ,imgUrl,jsonObject.getJSONObject(Constants.Payload).getInt(Constants.EntryId));
                            GeneralFunctions.setUserExtraDetail(getContext(),new Gson().toJson(ExtraUserProfile.class));
                            Log.d(TAG,response.toString());
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
//                    printStackTraceToast.makeText(ctx, "Failed to save ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                hideImgLoading();
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                hideImgLoading();
                Log.d(TAG,message.toString());
            }
        });
    }

    void openPicker(){
        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(getActivity())
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(final Uri uri) {
                        // here is selected uri
                        showImgLoading();
                        String requestId = MediaManager.get()
                                .upload(uri).unsigned("tderom9l")
                                .callback(new ListenerService() {
                                    @Nullable
                                    @Override
                                    public IBinder onBind(Intent intent) {
                                        return null;
                                    }

                                    @Override
                                    public void onStart(String requestId) {

                                    }

                                    @Override
                                    public void onProgress(String requestId, long bytes, long totalBytes) {
                                        Log.d(TAG,String.valueOf((bytes/totalBytes)*100));

                                    }

                                    @Override
                                    public void onSuccess(String requestId, Map resultData) {
                                        //hideProgress();
                                        Log.d(TAG,resultData.toString());
                                        Toast.makeText(getActivity(), resultData.get("url").toString(), Toast.LENGTH_LONG).show();
                                        Glide.with(getActivity())
                                                .load(uri)
                                                .into(profile_image);

                                        Log.d(TAG,uri.toString());
                                        GeneralFunctions.saveUserImage(uri.toString(),getActivity());

                                        checkUserIdAvailability(resultData.get("url").toString());

                                    }

                                    @Override
                                    public void onError(String requestId, ErrorInfo error) {
                                        hideImgLoading();
                                        Toast.makeText(getActivity(), error.getDescription(), Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void onReschedule(String requestId, ErrorInfo error) {
                                        Log.d(TAG,error.toString());

                                    }
                                }).dispatch();

                    }
                })
                .create();

        tedBottomPicker.show(getActivity().getSupportFragmentManager());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPicker();
                }
            }

        }
    }

    void updateService(final String imgUrl){
        Map<String, Object> params = new HashMap<>();
        // params.put("users_id",GeneralFunctions.getUserId());
        params.put("user_image",imgUrl);
        params.put("longitude",GeneralFunctions.getLongitude(getContext()));
        params.put("latitude",GeneralFunctions.getLatitude(getContext()));
        params.put("where","id,"+GeneralFunctions.getUserId());
        App.devless.edit(serviceName, "user_extra_details", params, GeneralFunctions.getUser(getContext()), new EditDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                hideImgLoading();
                 ExtraUserProfile euP=new Gson().fromJson(GeneralFunctions.getUserExtraDetail(getContext()),ExtraUserProfile.class);
                    euP.setLatitude(GeneralFunctions.getLatitude(getContext()));
                    euP.setLongitude(GeneralFunctions.getLongitude(getContext()));
                euP.setUser_image(imgUrl);
                GeneralFunctions.setUserExtraDetail(getContext(),new Gson().toJson(euP));
                Log.d(TAG,response.toString());
            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                hideImgLoading();
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {
                hideImgLoading();
                Log.d(TAG,message.toString());
            }
        });
    }


}
