package com.example.mdkashif.new_fire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import info.hoang8f.widget.FButton;

public class Wecome_page extends AppCompatActivity {

    FButton reg_btn,logBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_wecome_page);

            logBtn = findViewById(R.id.logbtn);
            logBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login = new Intent(Wecome_page.this, Login_Activity.class);
                    startActivity(login);
                }
            });
            reg_btn = findViewById(R.id.wel_reg_btn);
            reg_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_reg = new Intent(Wecome_page.this, Register_activity.class);
                    startActivity(intent_reg);
                }
            });
        }
}
