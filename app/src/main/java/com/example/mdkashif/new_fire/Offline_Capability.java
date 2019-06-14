package com.example.mdkashif.new_fire;

import android.app.Application;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
//import com.google.firestore.v1beta1.StructuredQuery;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import io.fabric.sdk.android.Fabric;

public class Offline_Capability extends Application {

    DatabaseReference userRefrence;
    FirebaseAuth userAuth;
    FirebaseUser curentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Load Profile picture Offline - Picasso

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(built);

        userAuth = FirebaseAuth.getInstance();
        curentUser = userAuth.getCurrentUser();

        if (curentUser != null){

            String Online_user = userAuth.getCurrentUser().getUid();

            userRefrence = FirebaseDatabase.getInstance().getReference().child("User").child(Online_user);

          userRefrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    userRefrence.child("Online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
