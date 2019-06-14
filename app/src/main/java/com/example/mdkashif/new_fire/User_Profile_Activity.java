package com.example.mdkashif.new_fire;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class User_Profile_Activity extends AppCompatActivity {

    Button SendFriendRequestButton;
    Button DeclineFriendRequestButton;
    ImageView FrndProfileImg;
    TextView friendDisplayName;
    TextView friendStatus;
    DatabaseReference frndDatabaseRef, FrndReqRef, FrndRef, NotificationRef;
    FirebaseAuth Curent_user_id;
    String CURRENT_STATE;
    String Sender_user_id;
    String Receiver_user_id;
    ProgressDialog mprog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile_);
        mprog = new ProgressDialog(this);

        FrndProfileImg = findViewById(R.id.frndprofileimg);
        friendDisplayName = findViewById(R.id.frndDisplayName);
        friendStatus = findViewById(R.id.friendStatus);
        SendFriendRequestButton =  findViewById(R.id.frnd_req_btn);
        DeclineFriendRequestButton =  findViewById(R.id.dec_frnd_req_btn);

        android.support.v7.widget.Toolbar mtoobar = findViewById(R.id.userProfileBar);
        setSupportActionBar(mtoobar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        mprog.setTitle("Uploading Profile....");
        mprog.setMessage("Please Wait");
        mprog.show();

        CURRENT_STATE = "not_freind";
        Sender_user_id = Curent_user_id.getInstance().getUid();
        FrndReqRef = FirebaseDatabase.getInstance().getReference().child("Freind_Request");
        frndDatabaseRef = FirebaseDatabase.getInstance().getReference().child("User");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        frndDatabaseRef.keepSynced(true);
        FrndRef = FirebaseDatabase.getInstance().getReference().child("Friends");


        Receiver_user_id = getIntent().getExtras().get("select_user_id").toString();

        frndDatabaseRef.child(Receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String FrendName = dataSnapshot.child("Name").getValue().toString();
                String FrndStats = dataSnapshot.child("Status").getValue().toString();
                String FrndImg   = dataSnapshot.child("Image").getValue().toString();
                friendDisplayName.setText(FrendName);
                friendStatus.setText(FrndStats);
                Picasso.with(User_Profile_Activity.this).load(FrndImg).placeholder(R.drawable.userdefalt).into(FrndProfileImg);

                FrndReqRef.child(Sender_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (dataSnapshot.hasChild(Receiver_user_id)){

                                String req_type = dataSnapshot.child(Receiver_user_id).child("REQUEST_TYPE").getValue().toString();

                                if (req_type.equals("Sent")){

                                    CURRENT_STATE = "Request_sent";
                                    SendFriendRequestButton.setText("Cancle Freind Request");

                                }
                                else if (req_type.equals("Received")){

                                    CURRENT_STATE = "Request_received";
                                    SendFriendRequestButton.setText("Accept Friend Request");

                                    DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                    DeclineFriendRequestButton.setEnabled(true);

                                    DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            DeclineFriendRequest();

                                        }
                                    });
                                }
                            }

                        else {
                            FrndRef.child(Sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(Receiver_user_id)){
                                        CURRENT_STATE = "Friends";
                                        SendFriendRequestButton.setText("UnFriend");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mprog.dismiss();

        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);


        if (!Sender_user_id.equals(Receiver_user_id)) {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SendFriendRequestButton.setEnabled(false);

                    if (CURRENT_STATE.equals("not_freind")) {

                        SendFriendRequestToPerson();
                    }

                    if (CURRENT_STATE.equals("Request_sent")) {

                        CancleFreindRequest();
                    }

                    if (CURRENT_STATE.equals("Request_received")) {

                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("Friends")) {

                        Unfriend();

                    }

                }
            });
        }else {
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void DeclineFriendRequest() {

        FrndReqRef.child(Sender_user_id).child(Receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FrndReqRef.child(Receiver_user_id).child(Sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            SendFriendRequestButton.setEnabled(true);
                            CURRENT_STATE = "not_freind";
                            SendFriendRequestButton.setText("Send Freind Request");

                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                            DeclineFriendRequestButton.setEnabled(false);
                        }
                    });
                }
            }
        });

    }


    private void Unfriend() {

        FrndRef.child(Sender_user_id).child(Receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FrndRef.child(Receiver_user_id).child(Sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                SendFriendRequestButton.setEnabled(true);
                                CURRENT_STATE = "not_freind";
                                SendFriendRequestButton.setText("Send Friend Request");

                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    private void AcceptFriendRequest() {

        Calendar calfordate = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("dd-mm-yyyy");
        final String SaveCurrentDate = currentDate.format(calfordate.getTime());

        FrndRef.child(Sender_user_id).child(Receiver_user_id).child("Date").setValue(SaveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FrndRef.child(Receiver_user_id).child(Sender_user_id).child("Date").setValue(SaveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FrndReqRef.child(Sender_user_id).child(Receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FrndReqRef.child(Receiver_user_id).child(Sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "Friend";
                                            SendFriendRequestButton.setText("UnFriend");

                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);

                                        }
                                    });
                                }
                            }
                        });

                    }
                });
            }
        });


    }

    private void CancleFreindRequest() {

        FrndReqRef.child(Sender_user_id).child(Receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FrndReqRef.child(Receiver_user_id).child(Sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            SendFriendRequestButton.setEnabled(true);
                            CURRENT_STATE = "not_freind";
                            SendFriendRequestButton.setText("Send Freind Request");

                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                            DeclineFriendRequestButton.setEnabled(false);
                        }
                    });
                }
            }
        });
    }

    private void SendFriendRequestToPerson() {

        FrndReqRef.child(Sender_user_id).child(Receiver_user_id).child("REQUEST_TYPE").setValue("Sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            HashMap<String, String> notificationdata = new HashMap<String,String>();
                            notificationdata.put("from",Sender_user_id);
                            notificationdata.put("Type","Request");
                            NotificationRef.child(Receiver_user_id).push().setValue(notificationdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    FrndReqRef.child(Receiver_user_id).child(Sender_user_id).child("REQUEST_TYPE").setValue("Received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "Request_sent";
                                            SendFriendRequestButton.setText("Cancle Friend Request");

                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);

                                        }
                                   });

                                }
                            });
                             }
                    }
                });
    }
}
