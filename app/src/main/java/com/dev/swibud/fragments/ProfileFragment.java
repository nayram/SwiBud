package com.dev.swibud.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
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
import com.dev.swibud.pojo.Users;
import com.dev.swibud.utils.App;
import com.dev.swibud.utils.GeneralFunctions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    @BindView(R.id.profile_image)
    CircleImageView profile_image;

    @BindView(R.id.progress_bar_profile)
    ProgressBar progress_bar_profile;

    String TAG=getClass().getName();

    @OnClick(R.id.btnSaveProfile) void saveProfile(){
        launchMainActivity();

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

    void launchImagePicker(){
        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(getActivity())
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected uri
                        Glide.with(getActivity())
                                .load(uri)
                                .into(profile_image);
/*                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        // Get the cursor
                        Cursor cursor = getActivity().getContentResolver().query(uri,
                                filePathColumn, null, null, null);

                        // Move to first row
                        cursor.moveToFirst();
                        uri.

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imgDecodableString = cursor.getString(columnIndex);*/
                       // cursor.close();
                        Log.d(TAG,uri.toString());
                        GeneralFunctions.saveUserImage(uri.toString(),getActivity());
                      /*  try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    List<Palette.Swatch> swatches = palette.getSwatches();

                                    // collapsingToolbar.setContentScrimColor(swatches.get(1).getRgb());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        Log.e(TAG,"Above lollipop ");
                                        imgView.setBackgroundColor(swatches.get(2).getRgb());
                                       // if (getArguments() !=null){
                                            toolbar.setBackgroundColor(swatches.get(2).getRgb());
                                       // }
                                        // collapsingToolbar.setStatusBarScrimColor(swatches.get(3).getRgb());
                                    }


                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/

                    }
                })
                .create();

        tedBottomPicker.show(getActivity().getSupportFragmentManager());
    }

    void launchMainActivity(){

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
}
