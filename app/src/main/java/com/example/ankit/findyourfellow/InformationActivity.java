package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class InformationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    protected Button userEdit;
    protected TextView appInfo;
    protected TextView thisUser;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Information");

        mAuth = FirebaseAuth.getInstance();

        userEdit = (Button) findViewById(R.id.editUser);

        appInfo = (TextView) findViewById(R.id.appView);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(2).setChecked(false);

        //Inflate bottom navigation items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.manage_friends:
                        goToManageActivity();
                        return true;
                    case R.id.sign_out:
                        userSignOut();
                        startActivity(new Intent(InformationActivity.this, MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.track_activity:
                        goToTrackActivity();
                        return true;
                }
                return false;
            }
        });

        userEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfileActivity();
            }
        });

        //Lock into portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        thisUser = (TextView) findViewById(R.id.userid);
        thisUser.setText (mAuth.getCurrentUser().getUid().toString());

        appInfo.setText("Developers: \n \n" +
                        "Galal, Ahmed \n" +
                        "Mondlane, Amilcar \n" +
                        "Pagkaliwangan, Dexter \n" +
                        "Rajan, Ankit \n" +
                        "Tshimombo, Sylvain \n \n" +
                        "This application would be able to track, locate and send " +
                        "coordinates of a person's location to their friends or acquaintances " +
                        "by recording distance and changes in direction that person makes as they travel. The whole purpose " +
                        "of this application is to allow users to locate their beloved ones to provide a sense of control " +
                        "and additional security and safety, letting the users know what is going on with their friends. To keep the accuracy of the data, please refrain from login " +
                        "in from multiple devices simultaneously. Your location will not be sold to any third party. This app is not to be used in unethical ways. " +
                        "This app also doesn't mean you can drink as much as you want. \n" +
                        "Please follow the recommended amount set " +
                        "by your government.  \n\n   Special Thanks to Dr. Lynch, Dr. Patel, Mr. Fajardo and StackOverflow!");
    }

    private void userSignOut()
    {
        Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
        stopService(intent);
        mAuth.signOut();
    }

    void goToTrackActivity()
    {
        Intent intent = new Intent(InformationActivity.this, TrackFriendsActivity.class);
        startActivity(intent);
    }

    void goToManageActivity()
    {
        Intent intent = new Intent(InformationActivity.this, ManageFriendsActivity.class);
        startActivity(intent);
    }

    void goToEditProfileActivity()
    {
        Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {

        Menu menu = bottomNavigationView.getMenu();

        menu.getItem(2).setChecked(false);

        super.onResume();
    }
}
