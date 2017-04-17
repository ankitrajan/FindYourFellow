package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

public class TrackFriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Switch trackingSwitch;

    private ListView listView;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logofinal480);

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();

        menu.getItem(0).setChecked(false);

        //Inflate bottom navigation items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.manage_friends:
                        goToManageActivity();
                        return true;
                    case R.id.information:
                        goToInformationActivity();
                        return true;
                    case R.id.sign_out:
                        userSignOut();
                        startActivity(new Intent(TrackFriendsActivity.this, MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        });

        //Lock into portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        trackingSwitch = (Switch) findViewById(R.id.trackSwitch);

        listView = (ListView) findViewById(R.id.myFriends);

        //Setup custom adapter
        final TrackAdapter friendsAdapter = new TrackAdapter(getApplicationContext(), R.layout.track_item);
        listView.setAdapter(friendsAdapter);

        final String thisUser = mAuth.getCurrentUser().getUid().toString();

        final Firebase trackingRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Information/Tracking");

        trackingRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String canTrack = dataSnapshot.getValue().toString();

                    //Display correct status of switch depending on value in database
                    if (canTrack.equals("yes"))
                    {
                        //Start tracking if allowed
                        trackingSwitch.setChecked(true);
                        Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
                        startService(intent);
                    }
                    else
                    {
                        //Stop tracking if not allowed
                        trackingSwitch.setChecked(false);
                        Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
                        stopService(intent);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getCode());
            }
        });

        trackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //Update value and location service when switch state changed
                if(isChecked){
                    Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
                    startService(intent);
                    trackingRef.setValue("yes");
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
                    stopService(intent);
                    trackingRef.setValue("no");
                }
            }
        });

        Firebase friendRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Friends");

        friendRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                //Display all friends
                final String friendName = dataSnapshot.getValue().toString();
                final String friendKey = dataSnapshot.getKey().toString();

                Firebase infoRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + friendKey + "/Information");

                infoRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2)
                    {
                            //Get coordinates so that it can be sent to adapter where distance and status are calculated
                            final String friendLat = dataSnapshot2.child("Latitude").getValue().toString();
                            final String friendLong = dataSnapshot2.child("Longitude").getValue().toString();

                            Firebase userRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Information");

                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot3) {

                                        //Also send user coordinates so that distance can be calculated
                                        String userLat = dataSnapshot3.child("Latitude").getValue().toString();
                                        String userLong = dataSnapshot3.child("Longitude").getValue().toString();

                                        boolean myTest = friendsAdapter.isAlreadyInList(friendKey);

                                        //if friend is already in the list and location is changed, replace old value in adapter,
                                        // else add friend to adapter
                                        if (myTest)
                                            friendsAdapter.replaceList(friendKey, friendLat, friendLong, userLat, userLong);
                                        else
                                            friendsAdapter.add(friendName, friendKey, friendLat, friendLong, userLat, userLong);

                                        friendsAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    System.out.println("The read failed: " + firebaseError.getCode());
                                }
                            });
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getCode());
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getCode());
            }
        });
    }

    private void userSignOut()
    {
        Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
        stopService(intent);
        mAuth.signOut();
    }


    void goToManageActivity()
    {
        Intent intent = new Intent(TrackFriendsActivity.this, ManageFriendsActivity.class);
        startActivity(intent);
    }

    void goToInformationActivity()
    {
        Intent intent = new Intent(TrackFriendsActivity.this, InformationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {

        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(0).setChecked(false);

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}