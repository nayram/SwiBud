package com.dev.swibud.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.swibud.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 10/4/17.
 */

public class InviteContactViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView profileImage;
    public TextView tvName;
    public CheckBox invCheckbox;

    public InviteContactViewHolder(View itemView) {
        super(itemView);
        profileImage = (CircleImageView) itemView.findViewById(R.id.imgUser);
        tvName=(TextView)itemView.findViewById(R.id.tvInvName);
        invCheckbox=(CheckBox) itemView.findViewById(R.id.chkInvite);
    }

}
