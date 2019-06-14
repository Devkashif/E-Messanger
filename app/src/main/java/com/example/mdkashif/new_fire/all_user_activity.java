package com.example.mdkashif.new_fire;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class all_user_activity extends AppCompatActivity {

    private RecyclerView allUserList;
    private DatabaseReference allUserDatabaseRef;
    private Dialog UserproImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user_activity);

        android.support.v7.widget.Toolbar mtoobar = findViewById(R.id.all_user);
        setSupportActionBar(mtoobar);
        getSupportActionBar().setTitle("All User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        allUserList = (RecyclerView) findViewById(R.id.userList);
        allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("User");
        allUserDatabaseRef.keepSynced(true);
        allUserList.setHasFixedSize(true);
        allUserList.setLayoutManager(new LinearLayoutManager(this));

        UserproImg = new Dialog(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<All_user_adapter, AllUserViewHolder> FRA=new FirebaseRecyclerAdapter<All_user_adapter, AllUserViewHolder>
                (All_user_adapter.class,R.layout.userlistlayout,AllUserViewHolder.class,allUserDatabaseRef) {
            @Override
            protected void populateViewHolder(AllUserViewHolder viewHolder, All_user_adapter model, final int position) {
                viewHolder.setUser_name(model.getName());
                viewHolder.setUser_status(model.getStatus());
                viewHolder.setThumb_image(getApplicationContext(),model.getThumb_image());

                CircleImageView UserDp = viewHolder.mView.findViewById(R.id.allUserprofile);
                UserDp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selectUserId = getRef(position).getKey();
                        UserproImg.setContentView(R.layout.userprofileimg);
                        final ImageView Dp = UserproImg.findViewById(R.id.userprofimg);
                        UserproImg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        UserproImg.show();

                        allUserDatabaseRef.child(selectUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String LoadImg = dataSnapshot.child("Image").getValue().toString();
                                Picasso.with(all_user_activity.this).load(LoadImg).placeholder(R.drawable.userdefalt).into(Dp);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String select_user_id = getRef(position).getKey();
                        Intent profile_intent=new Intent(all_user_activity.this,User_Profile_Activity.class);
                        profile_intent.putExtra("select_user_id",select_user_id);
                        startActivity(profile_intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    }
                });
            }
        };
        allUserList.setAdapter(FRA);

    }

}
