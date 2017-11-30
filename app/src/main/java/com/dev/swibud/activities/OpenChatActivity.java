package com.dev.swibud.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dev.swibud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nayrammensah on 11/27/17.
 */

public class OpenChatActivity extends AppCompatActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_chat_act);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        setTitle("Meet Up");
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Bundle extra= getIntent().getExtras();
       if (extra!=null){
           setTitle(extra.getString("name",""));
       }
    }
}
