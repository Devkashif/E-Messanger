package com.example.mdkashif.new_fire;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserViewHolder extends RecyclerView.ViewHolder{

    View mView;

    public AllUserViewHolder(View itemView) {
        super(itemView);

        mView=itemView;

    }
    public void setUser_name(String Name){
        TextView Uname=(TextView) mView.findViewById(R.id.allUserName);
        Uname.setText(Name);
    }
    public void setUser_status(String Status){
        TextView Ustatus=(TextView) mView.findViewById(R.id.allUserStatus);
        Ustatus.setText(Status);
    }
    public void setThumb_image(final Context cntx, final String Thumb_image){
       final CircleImageView Uimage=(CircleImageView) mView.findViewById(R.id.allUserprofile);

        Picasso.with(cntx).load(Thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefalt)
                .into(Uimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(cntx).load(Thumb_image).placeholder(R.drawable.userdefalt).into(Uimage);
                    }
                });
    }
}
