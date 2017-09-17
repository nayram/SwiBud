package com.dev.swibud.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.activities.MainActivity;
import com.dev.swibud.interfaces.AuthInterface;
import com.dev.swibud.interfaces.MainServiceInterface;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.GeneralFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidsdk.devless.io.devless.interfaces.RequestResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by nayrammensah on 9/3/17.
 */

public class Profile_Fragment extends Fragment{
    @BindView(R.id.edtFirstName)
    EditText edtFirstName;
    @BindView(R.id.edtLastName)
    EditText edtLastName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPhone)
    EditText edtPhone;
    @BindView(R.id.imgView)
    ImageView imgView;

    @BindView(R.id.profile_image)
    CircleImageView profile_image;

    @BindView(R.id.progress_bar_profile)
    ProgressBar progress_bar_profile;

    MainServiceInterface authCallback;
    Context ctx;

    String TAG=getClass().getName();

    @OnClick(R.id.btnSaveProfile) void saveProfile(){
        save();

    }
    @OnClick(R.id.profile_image) void openImagePicker(){
        launchImagePicker();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.frag_profile,container,false);
        ButterKnife.bind(this,rootView);


        String user= GeneralFunctions.getUser(getContext());
        if (user!=null){
            Log.d(TAG,user);
            try {
                JSONObject userJson=new JSONObject(user);
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

        }else{
            Toast.makeText(getActivity(), "User is null", Toast.LENGTH_SHORT).show();
        }

        if (GeneralFunctions.getUserImage(getActivity())!=null){
            String img=GeneralFunctions.getUserImage(getActivity());
            Glide.with(this)
                    .load(img)
                    .into(profile_image);
        }

        return rootView;
    }


    void showProgress(){

        progress_bar_profile.setVisibility(View.VISIBLE);
    }

    void hideProgress(){
        progress_bar_profile.setVisibility(View.GONE);
    }

    void launchImagePicker(){
        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(getActivity())
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected uri
                        Glide.with(getActivity())
                                .load(uri)
                                .into(profile_image);

                        Log.d(TAG,uri.toString());
                        GeneralFunctions.saveUserImage(uri.toString(),getActivity());
                        authCallback.updateProfileImage();


                    }
                })
                .create();

        tedBottomPicker.show(getActivity().getSupportFragmentManager());
    }

    void save(){
        List<String> params=new ArrayList<>(Arrays.asList(
                edtEmail.getText().toString(),"","",edtPhone.getText().toString(),edtFirstName.getText().toString(),
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
                        Toast.makeText(getContext(), "Details saved successfully", Toast.LENGTH_SHORT).show();
                        authCallback.updateProfileDetails();
                    }else{
                        Toast.makeText(getContext(), "Failed to save profile details", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Failed to save profile details", Toast.LENGTH_SHORT).show();
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


}
