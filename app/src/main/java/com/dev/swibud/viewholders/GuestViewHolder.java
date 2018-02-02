package com.dev.swibud.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 1/22/18.
 */

public class GuestViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.guestImage)
    CircleImageView guestImage;

    @BindView(R.id.tvGuestName)
    TextView tvName;

    @BindView(R.id.tvGuestUsername)
    TextView tvUserName;

    @BindView(R.id.btnRemovePerson)
    public Button btnRemovePerson;

    @BindView(R.id.progress_loading)
    public ProgressBar progressBar;

    public GuestViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setName(String name){
        this.tvName.setText(name);
    }

    public void setUsername(String username){
        this.tvUserName.setText(username);
    }

    public void setGuestImage(String url){
        Glide.with(this.guestImage.getContext())
                .load(url)
                .into(this.guestImage);
    }

}
