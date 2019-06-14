package com.example.mdkashif.new_fire;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private RecyclerView fndlist;
    private DatabaseReference fndDatabaseRef;
    private DatabaseReference frndDbRef;
    private FirebaseAuth frndAuth;
    private String  online_user_id;
    private View userfrndView;
    private Dialog UserProImg;

    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userfrndView = inflater.inflate(R.layout.fragment_friend, container, false);
        fndlist = userfrndView.findViewById(R.id.frndlist);
        frndAuth = FirebaseAuth.getInstance();
        online_user_id=frndAuth.getUid();
        fndDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        frndDbRef = FirebaseDatabase.getInstance().getReference().child("User");
        fndlist.setLayoutManager(new LinearLayoutManager(getContext()));

        UserProImg = new Dialog(getContext());

        frndDbRef.keepSynced(true);

        return userfrndView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<FriendsAdapter, FriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendsAdapter, FriendViewHolder>
                (FriendsAdapter.class, R.layout.userlistlayout, FriendViewHolder.class, fndDatabaseRef) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, FriendsAdapter model, int position) {

                viewHolder.setDate(model.getDate());

                final String FrndListID = getRef(position).getKey();
                frndDbRef.child(FrndListID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        final String FrndName = dataSnapshot.child("Name").getValue().toString();
                        String Fnd_Thumb_Img = dataSnapshot.child("Thumb_image").getValue().toString();

                        viewHolder.setFriendName(FrndName);
                        viewHolder.setThumbImage(Fnd_Thumb_Img);

                        CircleImageView UserDP = viewHolder.FrndView.findViewById(R.id.allUserprofile);
                        UserDP.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserProImg.setContentView(R.layout.userprofileimg);
                                final ImageView Dp = UserProImg.findViewById(R.id.userprofimg);
                                UserProImg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                                UserProImg.show();

                                frndDbRef.child(FrndListID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String LoadImag = dataSnapshot.child("Image").getValue().toString();
                                        Picasso.with(getContext()).load(LoadImag).placeholder(R.drawable.userdefalt).into(Dp);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        });

                        viewHolder.FrndView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence option[] = new CharSequence[]
                                        {
                                                FrndName + "'s Profile", "Send Message"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Option");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position == 0){

                                            Intent profileIntent = new Intent(getContext(), User_Profile_Activity.class);
                                            profileIntent.putExtra("select_user_id", FrndListID);
                                            startActivity(profileIntent);
                                        }
                                        if (position == 1){

                                            if(dataSnapshot.child("Online").exists()){

                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                chatIntent.putExtra("select_user_id", FrndListID);
                                                chatIntent.putExtra("Name", FrndName);
                                                startActivity(chatIntent);


                                            }else {
                                                frndDbRef.child(FrndListID).child("Online").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                        chatIntent.putExtra("select_user_id", FrndListID);
                                                        chatIntent.putExtra("Name", FrndName);
                                                        startActivity(chatIntent);

                                                    }
                                                });
                                            }


                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                        if (dataSnapshot.hasChild("Online")){

                            String Online_Status = (String) dataSnapshot.child("Online").getValue().toString();

                            viewHolder.setUserOnline(Online_Status);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        fndlist.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{

        View FrndView;
        Context ctx;

        public FriendViewHolder(View itemView) {
            super(itemView);

            FrndView=itemView;
        }

        public  void setFriendName(String FrndName) {

            TextView Fname = (TextView) FrndView.findViewById(R.id.allUserName);
            Fname.setText(FrndName);

        }

        public  void setThumbImage(final String Fnd_Thumb_Img) {

            final CircleImageView Fimage = (CircleImageView) FrndView.findViewById(R.id.allUserprofile);

            Picasso.with(ctx).load(Fnd_Thumb_Img).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefalt).into(Fimage, new Callback() {
                @Override
                public void onSuccess() {


                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(Fnd_Thumb_Img).placeholder(R.drawable.userdefalt).into(Fimage);
                }
            });
        }



        public void setDate(String date){

            TextView Fdate = (TextView) FrndView.findViewById(R.id.allUserStatus);

            Fdate.setText(date);
        }

        public void setUserOnline(String online_status) {

            ImageView Online_Visibility = (ImageView) FrndView.findViewById(R.id.online_status);

            if (online_status.equals("true")){
                Online_Visibility.setVisibility(View.VISIBLE);
            }
            else {

                Online_Visibility.setVisibility(View.INVISIBLE);
            }
        }
    }

}
