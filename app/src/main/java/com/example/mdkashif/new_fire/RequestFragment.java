package com.example.mdkashif.new_fire;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private View RequestView;
    private RecyclerView RequestList;
    private DatabaseReference UReqsDbRef;
    private DatabaseReference allURef, frndreqDatabase,FrndDatbase;
    private FirebaseAuth rAuth;
    private String Online_req_user_id;

    private FButton acceptReq;
    private FButton cancleReq;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        RequestView = inflater.inflate(R.layout.fragment_request, container, false);

        RequestList = RequestView.findViewById(R.id.UserReqList);

        rAuth = FirebaseAuth.getInstance();
        Online_req_user_id = rAuth.getCurrentUser().getUid();
        UReqsDbRef = FirebaseDatabase.getInstance().getReference().child("Freind_Request").child(Online_req_user_id);
        allURef = FirebaseDatabase.getInstance().getReference().child("User");
        frndreqDatabase = FirebaseDatabase.getInstance().getReference().child("Freind_Request");
        FrndDatbase = FirebaseDatabase.getInstance().getReference().child("Friends");

        RequestList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


        RequestList.setLayoutManager(linearLayoutManager);
        allURef.keepSynced(true);

        return RequestView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Request , RequestFragment.Requesthollder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, Requesthollder>
                (Request.class, R.layout.friend_req_list_layout, RequestFragment.Requesthollder.class, UReqsDbRef) {
            @Override
            protected void populateViewHolder(final Requesthollder viewHolder, Request model, final int position) {

               final String  ReqUserListId = getRef(position).getKey();

                final DatabaseReference Frnd_req_type = getRef(position).child("REQUEST_TYPE").getRef();

                Frnd_req_type.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            String Req_type = dataSnapshot.getValue().toString();

                            if (Req_type.equals("Received")){

                                allURef.child(ReqUserListId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String ReqUsernam = dataSnapshot.child("Name").getValue().toString();
                                        String ReeqUserStatus = dataSnapshot.child("Status").getValue().toString();
                                        String ReqUserThumbImage = dataSnapshot.child("Thumb_image").getValue().toString();

                                        viewHolder.setName(ReqUsernam);
                                        viewHolder.setStatus(ReeqUserStatus);
                                        viewHolder.setThumb_image(ReqUserThumbImage);

                                        FButton acceptbtn = viewHolder.ReqView.findViewById(R.id.ReqAccept);
                                        acceptbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Calendar setCurrentdate = Calendar.getInstance();
                                                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                final String saveCurrentdate = currentDate.format(setCurrentdate.getTime());

                                                FrndDatbase.child(Online_req_user_id).child(ReqUserListId).child("Date").setValue(saveCurrentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        FrndDatbase.child(ReqUserListId).child(Online_req_user_id).child("Date").setValue(saveCurrentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                frndreqDatabase.child(Online_req_user_id).child(ReqUserListId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        frndreqDatabase.child(ReqUserListId).child(Online_req_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                                Toast.makeText(getContext(), "Request Accept Succesfully", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });

                                            }
                                        });

                                        FButton Decline_req = viewHolder.ReqView.findViewById(R.id.ReqDec);
                                        Decline_req.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                frndreqDatabase.child(Online_req_user_id).child(ReqUserListId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        frndreqDatabase.child(ReqUserListId).child(Online_req_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                Toast.makeText(getContext(), "Request Decline Succesfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else if(Req_type.equals("Sent")) {

                                FButton Decline_req = viewHolder.ReqView.findViewById(R.id.ReqDec);
                                Decline_req.setText("Request Sent");
                                viewHolder.ReqView.findViewById(R.id.ReqAccept).setVisibility(View.INVISIBLE);

                               Decline_req.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       frndreqDatabase.child(Online_req_user_id).child(ReqUserListId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {

                                               frndreqDatabase.child(ReqUserListId).child(Online_req_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                   @Override
                                                   public void onSuccess(Void aVoid) {

                                                       Toast.makeText(getContext(), "Request Sent is Cancled Succesfully", Toast.LENGTH_SHORT).show();
                                                   }
                                               });
                                           }
                                       });
                                   }
                               });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                allURef.child(ReqUserListId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String ReqUserName = dataSnapshot.child("Name").getValue().toString();
                        String ReqUserStatus = dataSnapshot.child("Status").getValue().toString();
                        String ReqUThumbImage = dataSnapshot.child("Thumb_image").getValue().toString();
                        
                        viewHolder.setName(ReqUserName);
                        viewHolder.setStatus(ReqUserStatus);
                        viewHolder.setThumb_image(ReqUThumbImage);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent userprofileIntent = new Intent(getContext(), User_Profile_Activity.class);
                        userprofileIntent.putExtra("select_user_id",ReqUserListId);
                        startActivity(userprofileIntent);
                    }
                });

            }
        };

        RequestList.setAdapter(firebaseRecyclerAdapter);

    }



    public static class Requesthollder extends RecyclerView.ViewHolder{

        View ReqView;
        Context ctx;

        public Requesthollder(View itemView) {
            super(itemView);
            ReqView = itemView;


        }

        public void setName(String reqUserName) {

            TextView ReqName = ReqView.findViewById(R.id.UserReqName);
            ReqName.setText(reqUserName);

        }

        public void setStatus(String reqUserStatus) {

            TextView ReqUstatus = ReqView.findViewById(R.id.UReqStatus);
            ReqUstatus.setText(reqUserStatus);

        }

        public void setThumb_image(final String reqUThumbImage) {

            final CircleImageView RThumbImage =  ReqView.findViewById(R.id.ReqThumbImg);

            Picasso.with(ctx).load(reqUThumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefalt).into(RThumbImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(reqUThumbImage).placeholder(R.drawable.userdefalt).into(RThumbImage);

                }
            });
        }

    }

}
