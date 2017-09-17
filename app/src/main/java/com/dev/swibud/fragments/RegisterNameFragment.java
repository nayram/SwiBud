package com.dev.swibud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.utils.GeneralFunctions;
import com.dev.swibud.interfaces.AuthInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nayrammensah on 8/2/17.
 */

public class RegisterNameFragment extends Fragment {

    AuthInterface authCallback;
    Context ctx;

    @BindView(R.id.edtFirstName)
    EditText edtFirstName;

    @BindView(R.id.edtLastName)
    EditText edtLastName;

    @BindView(R.id.fabNext)
    FloatingActionButton fabNext;

    Activity act;

    @OnClick(R.id.fabNext) void next(){

        if (edtFirstName.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Enter your first name", Toast.LENGTH_SHORT).show();
        }else if (edtLastName.getText().toString().toString().isEmpty()){
            Toast.makeText(getActivity(), "Enter your last name", Toast.LENGTH_SHORT).show();
        }else{
            authCallback.addFirstLastName(edtFirstName.getText().toString(),edtLastName.getText().toString());
            PhoneNumberAuthFragment phoneFragment=new PhoneNumberAuthFragment();
            GeneralFunctions.addFragmentFromRight(getActivity().getSupportFragmentManager(),phoneFragment,R.id.llHomeContainer);
        }

    }

    @OnClick(R.id.imgBack) void back(){
        getActivity().onBackPressed();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_register_name,container,false);
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
