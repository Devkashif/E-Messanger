package com.example.mdkashif.new_fire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import info.hoang8f.widget.FButton;

public class SetupProfile extends AppCompatActivity {


    CircleImageView UserDp;
    DatabaseReference Udatabase;
    FirebaseUser currentuser;
    StorageReference UsersDP, Userthumbimg;
    FirebaseAuth sAuth;
    EditText UserStatus;
    FButton Donebtn;
    Bitmap thumbBitmap;
    TextView skp;
    private final static int Galary_pic=1;

    ProgressDialog SprogressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        SprogressDialog = new ProgressDialog(this);

        UserDp = findViewById(R.id.userdp);
        UserStatus = findViewById(R.id.userstatus);
        Donebtn = findViewById(R.id.update_profile);

        sAuth = FirebaseAuth.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        String OnlineUserId = currentuser.getUid();

        skp = findViewById(R.id.skp);
        skp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainintent = new Intent(SetupProfile.this,MainActivity.class);
                startActivity(mainintent);
            }
        });

        Udatabase = FirebaseDatabase.getInstance().getReference().child("User").child(OnlineUserId);
        UsersDP = FirebaseStorage.getInstance().getReference().child("profile_image");
        Userthumbimg = FirebaseStorage.getInstance().getReference().child("Thumb_Image");



        Udatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String ProfileImg = dataSnapshot.child("Image").getValue().toString();

                Picasso.with(SetupProfile.this).load(ProfileImg).placeholder(R.drawable.userdefalt).into(UserDp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UserDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galaryIntent = new Intent();
                galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent,Galary_pic);
            }
        });

        Donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SprogressDialog.setTitle("Uploading...");
                SprogressDialog.setMessage("Please Wait");
                SprogressDialog.show();
                String User_status = UserStatus.getText().toString();
                Upload_Status(User_status);

            }
        });
    }

    private void Upload_Status(String user_status) {

        if (TextUtils.isEmpty(user_status)){
            SprogressDialog.cancel();
            Toast.makeText(this, "Please Write Your Status", Toast.LENGTH_SHORT).show();
        }
        else {
            Udatabase.child("Status").setValue(user_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        SprogressDialog.dismiss();
                        Intent mainIntent = new Intent(SetupProfile.this,MainActivity.class);
                        startActivity(mainIntent);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Galary_pic && resultCode == RESULT_OK && data != null){
            Uri imageuri = data.getData();

            if (imageuri == null){
                return;
            }else {
                File tempcroped = new File(getCacheDir(),"tempCropedFile");
                Uri destinationUri = Uri.fromFile(tempcroped);
                UCrop.of(imageuri,destinationUri).start(this);
            }
        }

        //profile pic storing in firebase
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){

            Uri resultUri = UCrop.getOutput(data);

            File thumb_file_uri = new File(resultUri.getPath());
            String UserId = sAuth.getCurrentUser().getUid();

            try {
                thumbBitmap = new Compressor(this).setMaxHeight(200).setMaxWidth(200).setQuality(50).compressToBitmap(thumb_file_uri);
            }catch (IOException e){
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            final byte[] thumbByte = byteArrayOutputStream.toByteArray();

            final StorageReference thumbimg_path = Userthumbimg.child(UserId +".jpg");
            final StorageReference filepath = UsersDP.child(UserId +".jpg");

            SprogressDialog.setTitle("Uploading...");
            SprogressDialog.setMessage("Please Wait");
            SprogressDialog.show();

            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){
                        Toast.makeText(SetupProfile.this, "Profile Image is Uploaded", Toast.LENGTH_SHORT).show();

                        //Retreive profile image from Firebase Storage

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {

                                final String dounloadUrl = uri.toString();
                                UploadTask uploadTask_thumbimg = thumbimg_path.putBytes(thumbByte);

                                uploadTask_thumbimg.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        thumbimg_path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {

                                                final String dounloadThumbUrl = uri.toString();
                                                if (task.isSuccessful()){

                                                    Map updateThmbimg = new HashMap();
                                                    updateThmbimg.put("Image",dounloadUrl);
                                                    updateThmbimg.put("Thumb_image", dounloadThumbUrl);

                                                    Udatabase.updateChildren(updateThmbimg).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(SetupProfile.this, "Profile image is Uploaded", Toast.LENGTH_SHORT).show();
                                                                SprogressDialog.dismiss();
                                                            }
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
                }
            });

        }
    }
}
