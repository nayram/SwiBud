package com.dev.swibud.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dev.swibud.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 9/4/17.
 */

public class FollowViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView profileImage;
    public TextView tvName;
    public Button btnFollow;

    public FollowViewHolder(View itemView) {
        super(itemView);
        profileImage = (CircleImageView) itemView.findViewById(R.id.imgUser);
        tvName=(TextView)itemView.findViewById(R.id.tvUserName);
        btnFollow=(Button)itemView.findViewById(R.id.btnFollow);
    }

}
