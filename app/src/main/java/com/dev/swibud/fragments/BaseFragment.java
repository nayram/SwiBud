package com.dev.swibud.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.dev.swibud.interfaces.BackHandlerInterface;

/**
 * Created by nayrammensah on 11/24/17.
 */

public abstract class BaseFragment extends Fragment{
    protected BackHandlerInterface backHandlerInterface;
    public abstract String getTagText();
    public abstract boolean onBackPressed();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity()  instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        backHandlerInterface.setSelectedFragment(this);
    }
}
