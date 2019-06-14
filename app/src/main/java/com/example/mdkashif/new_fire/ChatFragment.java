package com.example.mdkashif.new_fire;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.mdkashif.new_fire.Notification.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View MyChatView;
    private RecyclerView userchatlist;
    private DatabaseReference UserChatRef;
    private DatabaseReference UserDBRef;
    private FirebaseAuth currentChatAuth;
    private String Chat_user_Online_id;
    private Dialog UserDp;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyChatView = inflater.inflate(R.layout.fragment_chat, container, false);
        userchatlist = MyChatView.findViewById(R.id.chatlist);
        currentChatAuth = FirebaseAuth.getInstance();
        Chat_user_Online_id = currentChatAuth.getCurrentUser().getUid();
        UserChatRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(Chat_user_Online_id);
        UserDBRef = FirebaseDatabase.getInstance().getReference().child("User");


        UserDp = new Dialog(getContext());

        userchatlist.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userchatlist.setLayoutManager(linearLayoutManager);

        UserDBRef.keepSynced(true);

        updateToken(FirebaseInstanceId.getInstance().getToken());
        return MyChatView;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Token");
        Token token1 = new Token(token);
        reference.child(Chat_user_Online_id).setValue(token1);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chat, ChatFragment.ChatViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>
                (Chat.class, R.layout.userlistlayout, ChatFragment.ChatViewHolder.class, UserChatRef) {
            @Override
            protected void populateViewHolder(final ChatViewHolder viewHolder, final Chat model, int position) {

                final String ChatUserListId = getRef(position).getKey();

                UserDBRef.child(ChatUserListId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        final String ChatUserName = dataSnapshot.child("Name").getValue().toString();
                        String Chat_thumb_img = dataSnapshot.child("Thumb_image").getValue().toString();
                        String Chat_user_status = dataSnapshot.child("Status").getValue().toString();
                        viewHolder.setChatName(ChatUserName);
                        viewHolder.setThumbImage(Chat_thumb_img);
                        viewHolder.setStatus(Chat_user_status);

                        CircleImageView Dp = viewHolder.chatView.findViewById(R.id.allUserprofile);
                        Dp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                UserDp.setContentView(R.layout.userprofileimg);
                                final ImageView imageView = UserDp.findViewById(R.id.userprofimg);
                                UserDp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                                UserDp.show();
                                UserDp.setCanceledOnTouchOutside(true);
                                UserDBRef.child(ChatUserListId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String userImg = dataSnapshot.child("Image").getValue().toString();
                                        Picasso.with(getContext()).load(userImg).placeholder(R.drawable.userdefalt).into(imageView);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });


                        viewHolder.chatView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (dataSnapshot.child("Online").exists()){

                                            Intent chatIN = new Intent(getContext(), ChatActivity.class);
                                            chatIN.putExtra("select_user_id", ChatUserListId);
                                            chatIN.putExtra("Name", ChatUserName);
                                            startActivity(chatIN);


                                }else {
                                    UserDBRef.child(ChatUserListId).child("Online").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("select_user_id", ChatUserListId);
                                            chatIntent.putExtra("Name", ChatUserName);
                                            startActivity(chatIntent);


                                        }
                                    });
                                }
                            }
                        });

                        if (dataSnapshot.hasChild("Online")){

                            String UserChatOnlineIconStatus = dataSnapshot.child("Online").getValue().toString();
                            viewHolder.setUserOnline(UserChatOnlineIconStatus);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        userchatlist.setAdapter(recyclerAdapter);
    }

    private void openProfileImages() {




    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        View chatView;
        Context ctx;

        public ChatViewHolder(View itemView) {
            super(itemView);

            chatView = itemView;
        }
        public void setChatName(String ChatUname){
            TextView ChatUName = chatView.findViewById(R.id.allUserName);
            ChatUName.setText(ChatUname);
        }
        public void setThumbImage(final String ChatThumbImg){

            final CircleImageView chatThumbImg = chatView.findViewById(R.id.allUserprofile);

            Picasso.with(ctx).load(ChatThumbImg).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefalt).into(chatThumbImg, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(ChatThumbImg).placeholder(R.drawable.userdefalt).into(chatThumbImg);
                }
            });
        }

        public void setUserOnline(String online_status){

            ImageView chaticon = chatView.findViewById(R.id.online_status);
            if (online_status.equals("true")){

                chaticon.setVisibility(View.VISIBLE);

            }else{
                chaticon.setVisibility(View.INVISIBLE);
            }

        }

        public void setStatus(String status) {

            TextView chatUserStatus = chatView.findViewById(R.id.allUserStatus);
            chatUserStatus.setText(status);
        }
    }
}
