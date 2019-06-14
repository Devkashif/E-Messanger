package com.example.mdkashif.new_fire;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

import info.hoang8f.widget.FButton;
//import io.opencensus.tags.Tag;

public class Register_activity extends AppCompatActivity {
    TextInputLayout mDisplayName;
    TextInputLayout mEmail;
    TextInputLayout mPass;
    FButton mCreate_btn;
    FirebaseAuth mAuth;
    ProgressDialog mRegprogress;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_activity);
        mAuth = FirebaseAuth.getInstance();
        //Toolbar

        Toolbar mtoob= findViewById(R.id.reg_toolbar);
        setSupportActionBar(mtoob);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TextInputLayout

        mDisplayName=findViewById(R.id.reg_display);
        mEmail=findViewById(R.id.reg_Email);
        mPass=findViewById(R.id.reg_pass);

        mRegprogress=new ProgressDialog(this);

        mCreate_btn=findViewById(R.id.reg_crt_btn);
        mCreate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name=mDisplayName.getEditText().getText().toString();
                String email=mEmail.getEditText().getText().toString();
                String pass=mPass.getEditText().getText().toString();
                if (pass.length()<6){
                    mRegprogress.hide();
                    mPass.setError("Password is Too Week");
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mRegprogress.hide();
                    mEmail.setError("Please Enter a Valid Email");

                }
                else if (!TextUtils.isEmpty(display_name)|| !TextUtils.isEmpty(email)|| !TextUtils.isEmpty(pass)){

                    mRegprogress.setTitle("Registering User...");
                    mRegprogress.setMessage("Please Wait While we create Your Account");
                    mRegprogress.setCanceledOnTouchOutside(false);
                    mRegprogress.show();

                    register_user(display_name,email,pass);
                }


            }
        });


    }

    private void register_user(final String display_name, final String email, final String pass) {
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                 //  Log.e("Its working or not", "Account is created");
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("Name", display_name);
                    userMap.put("Status", "Hey there I am using Easychat App");
                    userMap.put("Image", "default");
                    userMap.put("Thumb_image", "default");

                    mDatabaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mRegprogress.dismiss();
                                Intent mainIntent = new Intent(Register_activity.this, SetupProfile.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });
                }else if (!isConnected(Register_activity.this)){
                    mRegprogress.cancel();
                }

            }
        });
    }
    public boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo==null || !netinfo.isConnected() || !netinfo.isAvailable()){

            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
