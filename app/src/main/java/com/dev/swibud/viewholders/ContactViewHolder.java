package com.dev.swibud.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nayrammensah on 11/1/17.
 */

public class ContactViewHolder extends RecyclerView.ViewHolder{

    CircleImageView imageView;
    TextView indicator,contactName;

    public ContactViewHolder(View itemView) {
        super(itemView);
        imageView=(CircleImageView)itemView.findViewById(R.id.img_participant);
        indicator=(TextView)itemView.findViewById(R.id.indicator);
        contactName=(TextView)itemView.findViewById(R.id.contact_name);
        imageView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_user));
    }

    public void setContactName(String name){
        contactName.setText(name);
    }

    public void setImage(String image){
        Glide.with(imageView.getContext())
                .load(image)
                .into(imageView);
    }

    public void setDummyImage(Context ctx){
        imageView.setImageDrawable(ctx.getDrawable(R.drawable.ic_user));
    }

    public void setIndicator (boolean isFollower){
        if (isFollower){
            indicator.setBackground(indicator.getContext().getResources().getDrawable(R.drawable.bg_follow_indicator));
        }else{
            indicator.setBackground(indicator.getContext().getResources().getDrawable(R.drawable.bg_not_follow_indicator));
        }
    }
}
