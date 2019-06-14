package com.example.mdkashif.new_fire;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> user_msg_list;
    private FirebaseAuth MAAuth;
    private DatabaseReference userref;
    Context ctx;

    public MessageAdapter(List<Message> user_msg_list){
        this.user_msg_list = user_msg_list;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_retreiw_layout, parent ,false);
        MAAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        String Msg_Sender_id = MAAuth.getCurrentUser().getUid();

        Message messages = user_msg_list.get(position);
        String fromUserid = messages.getFrom();
        String fromMsgType = messages.getType();

        userref = FirebaseDatabase.getInstance().getReference().child("User").child(fromUserid);

        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Thumb_image")){
                    String ReceverImage = dataSnapshot.child("Thumb_image").getValue().toString();

                    Picasso.with(ctx).load(ReceverImage).placeholder(R.drawable.userdefalt).into(holder.RcvProfilePic);

                    String SenderImage = dataSnapshot.child("Thumb_image").getValue().toString();

                    Picasso.with(ctx).load(SenderImage).placeholder(R.drawable.userdefalt).into(holder.SenderprofilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (fromMsgType.equals("text")){

            holder.sndImageVw.setVisibility(View.INVISIBLE);

            holder.RcvMsgText.setVisibility(View.INVISIBLE);
            holder.RcvProfilePic.setVisibility(View.INVISIBLE);
            holder.SenderMsgText.setVisibility(View.INVISIBLE);
            holder.SenderprofilePic.setVisibility(View.INVISIBLE);

            if (fromUserid.equals(Msg_Sender_id)){

                holder.SenderMsgText.setBackgroundResource(R.drawable.chatborder);
                holder.SenderMsgText.setTextColor(Color.WHITE);
                holder.SenderMsgText.setText(messages.getMessage());

                holder.SenderMsgText.setVisibility(View.VISIBLE);
                holder.SenderprofilePic.setVisibility(View.VISIBLE);

            }else {


                holder.SenderMsgText.setVisibility(View.INVISIBLE);
                holder.SenderprofilePic.setVisibility(View.INVISIBLE);

                holder.RcvMsgText.setVisibility(View.VISIBLE);
                holder.RcvProfilePic.setVisibility(View.VISIBLE);

                holder.RcvMsgText.setBackgroundResource(R.drawable.chatborder);
                holder.RcvMsgText.setTextColor(Color.WHITE);
                holder.RcvMsgText.setText(messages.getMessage());
            }
        }else {

            holder.SenderMsgText.setVisibility(View.INVISIBLE);
            holder.SenderMsgText.setPadding(0,0,0,0);

            Picasso.with(holder.SenderprofilePic.getContext()).load(messages.getMessage()).placeholder(R.drawable.userdefalt)
                    .into(holder.sndImageVw);

        }

    }

    @Override
    public int getItemCount() {
        return user_msg_list.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView RcvMsgText;
        public TextView SenderMsgText;
        public CircleImageView SenderprofilePic;
        public CircleImageView RcvProfilePic;
        public ImageView sndImageVw;

        public MessageViewHolder(View view){
            super(view);

            RcvMsgText = view.findViewById(R.id.RcvMsg);
            RcvProfilePic = view.findViewById(R.id.RcvMsgPic);
            SenderMsgText = view.findViewById(R.id.SenderMsg);
            SenderprofilePic = view.findViewById(R.id.SenderMsgPic);
            sndImageVw = view.findViewById(R.id.sndImg);

        }
    }
}
