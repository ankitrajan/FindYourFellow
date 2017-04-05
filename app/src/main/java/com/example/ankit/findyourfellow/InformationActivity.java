package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class InformationActivity extends AppCompatActivity {

    protected TextView appInfo;

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Information");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appInfo = (TextView) findViewById(R.id.appView);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();

        menu.getItem(2).setChecked(false);
        /*
        for (int i = 0, size = menu.size(); i < size; i++) {

            if(i == 2)
                menu.getItem(2).setChecked(true);
            else
                menu.getItem(i).setChecked(false);
        }
        */

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.manage_friends:
                        goToManageActivity();
                        return true;
                    //case R.id.information:
                        //goToInformationActivity();
                        //return true;
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

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        String thisUser = mAuth.getCurrentUser().getUid().toString();

        appInfo.setText("Your User Key is " + thisUser +
                        ". \nShare this with your friends so they could add you. \n \n" +
                        "Developers: \n" +
                        "Galal, Ahmed \n" +
                        "Pagkaliwangan, Dexter \n" +
                        "Mondlane, Amilcar \n" +
                        "Rajan, Ankit \n" +
                        "Tshimombi, Sylvain \n \n" +
                        "This app lets you keep track of friends. It also allows your " +
                        "friends to keep track of you so that a fun night out stays, " +
                        "well, a FUN night out. To do so, and by downloading this " +
                        "app, you allow us to store your location so that it can be " +
                        "relayed to your friends. Your location will not be sold to " +
                        "any third party. This app is not to be used in unethical " +
                        "ways. This App also doesn't mean you can drink as much " +
                        "as you want. Please follow the recommended amount set " +
                        "by your government. Apart from that, PARTY HARD!!! \n\nSpecial Thanks to Dr.Lynch, Dr. Patel, Mr. Fajardo and StackOverflow!");
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

    //void goToInformationActivity()
    //{
        //Intent intent = new Intent(InformationActivity.this, InformationActivity.class);
        //startActivity(intent);
    //}

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

    //manage what happens when options on the toolbar are clicked
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.track:
                goToTrackActivity();
                return true;
            case R.id.manage:
                goToManageActivity();
                return true;
            case R.id.signout:
                userSignOut();
                startActivity(new Intent(InformationActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/

}
