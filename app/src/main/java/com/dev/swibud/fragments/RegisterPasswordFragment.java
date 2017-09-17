package com.dev.swibud.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.activities.MainActivity;
import com.dev.swibud.interfaces.AuthInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nayrammensah on 8/3/17.
 */

public class RegisterPasswordFragment extends Fragment {

    AuthInterface authCallback;
    Context ctx;
    Activity act;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    @OnClick(R.id.imgBack) void back(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.fabNext) void next(){
        if (edtPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Enter your password", Toast.LENGTH_SHORT).show();
        }else if (edtPassword.getText().toString().length()<6){
            Toast.makeText(getActivity(), "Password must have at least five characters", Toast.LENGTH_SHORT).show();
        }else{
            authCallback.addPassword(edtPassword.getText().toString());
            authCallback.registerCredentials();
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_register_password,container,false);
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
                    + " must implement OnFragmentInteractionListener");
        }

    }



    @Override
    public void onDetach() {
        super.onDetach();
        authCallback = null;
    }
}
