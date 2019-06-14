package com.example.mdkashif.new_fire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import info.hoang8f.widget.FButton;

public class Login_Activity extends AppCompatActivity {


    TextInputLayout mEmail;
    TextInputLayout mPass;
    FButton login;
    ProgressDialog log_dialog;
    FirebaseAuth lAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        lAuth=FirebaseAuth.getInstance();
        log_dialog=new ProgressDialog(this);


        //TextInputLayout

        mEmail=findViewById(R.id.log_Email);
        mPass=findViewById(R.id.log_pass);

        //Toolbar
        //android.support.v7.widget.Toolbar mtoob= findViewById(R.id.logBar);
        /*setSupportActionBar(mtoob);
        getSupportActionBar().setTitle("Login");*/

        login=findViewById(R.id.logButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String log_email= mEmail.getEditText().getText().toString();
                String log_pass=mPass.getEditText().getText().toString();

                if (!TextUtils.isEmpty(log_email)|| !TextUtils.isEmpty(log_pass)){

                    log_dialog.setTitle("Loging...");
                    log_dialog.setMessage("Please Wait While User Login");
                    log_dialog.setCanceledOnTouchOutside(false);
                    log_dialog.show();

                    log_user(log_email,log_pass);

                }
            }
        });
    }

    private void log_user(String log_email, String log_pass) {

        lAuth.signInWithEmailAndPassword(log_email,log_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    log_dialog.dismiss();
                    Intent logIntent=new Intent(Login_Activity.this,MainActivity.class);
                    //if i want back on my chat Activity and then open app then on my chat app activity
                    logIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logIntent);
                    finish();

                }else {
                    log_dialog.hide();
                    Toast.makeText(Login_Activity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
