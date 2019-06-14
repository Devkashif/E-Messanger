package com.example.mdkashif.new_fire;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mtoolbarl;
    private ViewPager mViewpager;
    private SectionPagerAddapter mSectionPagerAddapter;
    private TabLayout mTabLayout;
    FirebaseUser currentUser;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtoolbarl=findViewById(R.id.main_page_toolbar);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            String Online_user = mAuth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("User").child(Online_user);
        }

        setSupportActionBar(mtoolbarl);
        getSupportActionBar().setTitle("Easy chat");

        //Tabs
        mViewpager=findViewById(R.id.tab_pager);
        mSectionPagerAddapter=new SectionPagerAddapter(getSupportFragmentManager());
        mViewpager.setAdapter(mSectionPagerAddapter);
        mTabLayout=(TabLayout) findViewById(R.id.main_tab);
        mTabLayout.setupWithViewPager(mViewpager);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser==null){
           sendToStart();
        }
        else if (currentUser != null){

            userRef.child("Online").setValue("true");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
         if(currentUser !=null){

             userRef.child("Online").setValue(ServerValue.TIMESTAMP);
         }
    }

    private void sendToStart() {

        Intent intent=new Intent(MainActivity.this,Wecome_page.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.main_menu,menu);

         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()== R.id.Main_log_out){

            if (currentUser != null){
                userRef.child("Online").setValue(ServerValue.TIMESTAMP);
            }

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if (item.getItemId()==R.id.Main_setting){

            Intent settingIn=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingIn);
        }
        if (item.getItemId()==R.id.main_all_user){

            Intent allUser = new Intent(MainActivity.this,all_user_activity.class);
            startActivity(allUser);
        }

        return true;
    }
}
