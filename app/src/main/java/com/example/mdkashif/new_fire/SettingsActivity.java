package com.example.mdkashif.new_fire;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
//import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;
//import io.grpc.Compressor;

public class SettingsActivity extends AppCompatActivity {

    DatabaseReference mDatabaseReference;
    FirebaseUser mfirebaseUser;
    FButton changeProfileImg;
    FButton changeStutas;
    Dialog changUserstatus;
    ImageView Dilogimage;
    EditText typeStatus;
    Button doneStatus;
    TextView dis_name;
    TextView user_status;
    CircleImageView profile_img;
    ProgressDialog log_dialog;
    FirebaseAuth mAuth;
    StorageReference storageReferenceProfileImg,thumbStorageImgRef;
    Bitmap thumb_bitmap=null;
    private final static int Galary_pic = 1;
    Dialog Userprofileimg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changUserstatus=new Dialog(this);

        Userprofileimg = new Dialog(this);
        log_dialog=new ProgressDialog(this);

        changeStutas=findViewById(R.id.change_status_btn);
        dis_name=findViewById(R.id.prfName);
        user_status=findViewById(R.id.prfStatus);
        profile_img=(CircleImageView) findViewById(R.id.prfimage);
        changeProfileImg=findViewById(R.id.change_img_btn);
        mAuth=FirebaseAuth.getInstance();
        mfirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mfirebaseUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(current_uid);
        mDatabaseReference.keepSynced(true);

        //provide refrense of firebase storage
        storageReferenceProfileImg = FirebaseStorage.getInstance().getReference().child("profile_image");
        thumbStorageImgRef = FirebaseStorage.getInstance().getReference().child("Thumb_Image");

        //For Retriew Data From FirebaseDatabase Root user

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String Display_name=dataSnapshot.child("Name").getValue().toString();
                final String prof_img=dataSnapshot.child("Image").getValue().toString();
                String User_status=dataSnapshot.child("Status").getValue().toString();
                //String Thumb_profile_img=dataSnapshot.child("thumb_Image").getValue().toString();
                dis_name.setText(Display_name);
                user_status.setText(User_status);

                Picasso.with(SettingsActivity.this).load(prof_img).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefalt)
                        .into(profile_img, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(SettingsActivity.this).load(prof_img).placeholder(R.drawable.userdefalt).into(profile_img);
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // for Change profile image
        changeProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galaryIntent=new Intent();
                galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent,Galary_pic);
            }
        });
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserImgage();
            }
        });

        // For Change Status

        changeStutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
            }
        });
    }

    private void openUserImgage() {

        Userprofileimg.setContentView(R.layout.userprofileimg);
        Dilogimage = Userprofileimg.findViewById(R.id.userprofimg);
        Userprofileimg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        Userprofileimg.show();
        Userprofileimg.setCanceledOnTouchOutside(true);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userImg = dataSnapshot.child("Image").getValue().toString();
                Picasso.with(SettingsActivity.this).load(userImg).into(Dilogimage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void showDialogBox(){
        changUserstatus.setContentView(R.layout.changestatus);
        typeStatus = changUserstatus.findViewById(R.id.typeSt);
        doneStatus = changUserstatus.findViewById(R.id.status);
        changUserstatus.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        changUserstatus.show();
        doneStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_status=typeStatus.getText().toString();
                upload_status(new_status);
                log_dialog.setTitle("Updating...");
                log_dialog.setMessage("Please Wait");
                log_dialog.show();
            }
        });
    }

    private void upload_status(String new_status) {
        if(TextUtils.isEmpty(new_status)){
            Toast.makeText(SettingsActivity.this, "Please Write Your Status", Toast.LENGTH_SHORT).show();
        }else {
            mDatabaseReference.child("Status").setValue(new_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       log_dialog.dismiss();
                       changUserstatus.dismiss();
                       Toast.makeText(SettingsActivity.this, "Your Status Updated", Toast.LENGTH_SHORT).show();
                   }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //include crop funcnaility
        if(requestCode==Galary_pic && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();

            if(imageUri==null){

                return;
            }else {

                File tempcroped=new File(getCacheDir(),"tempCropedFile");
                Uri destinationUri =Uri.fromFile(tempcroped);
                UCrop.of(imageUri,destinationUri).start(this);

            }
        }
        //profile pic storing in firebase
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

             Uri resultUri = UCrop.getOutput(data);

             File thumb_file_uri = new File(resultUri.getPath());

            String user_id = mAuth.getCurrentUser().getUid();

            try {
                thumb_bitmap=new id.zelory.compressor.Compressor(this)
                        .setMaxHeight(200).setMaxWidth(200).setQuality(50).compressToBitmap(thumb_file_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            final byte[] thumb_byte= byteArrayOutputStream.toByteArray();
            
            final StorageReference thumbImg_path = thumbStorageImgRef.child(user_id +".jpg");
            final StorageReference filepath = storageReferenceProfileImg.child(user_id +".jpg");

            log_dialog.setTitle("Uploading Image...");
            log_dialog.setMessage("Please Wait");
            log_dialog.show();
            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(SettingsActivity.this, "Profile pic is sucssesfull Upload in Firebase", Toast.LENGTH_LONG).show();

                        //Retreive profile image from Firebase Storage

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {

                                final String downloadUrl = uri.toString();
                                UploadTask uploadTask_thumbImg = thumbImg_path.putBytes(thumb_byte);

                                uploadTask_thumbImg.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbImg_task) {
                                       thumbImg_path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> thumb_task) {
                                                final  String dounload_thumbUrl = uri.toString();
                                                if (thumb_task.isSuccessful()){
                                                    Map update_thumb_img = new HashMap();
                                                    update_thumb_img.put("Image",downloadUrl);
                                                    update_thumb_img.put("Thumb_image",dounload_thumbUrl);

                                                    mDatabaseReference.updateChildren(update_thumb_img).
                                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    Toast.makeText(SettingsActivity.this, "Profile pic is scsessfull upload", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }
                                        });

                                    }
                                });


                            }
                        });

                        log_dialog.dismiss();
                    }else {
                        Toast.makeText(SettingsActivity.this, "Profile Pic Can't be Store in Firebase", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

    }
}
