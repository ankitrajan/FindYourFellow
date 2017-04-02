package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;




public class TrackFriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ArrayList<String> allFriend = new ArrayList<>();
    private ListView listView;
    private Switch trackingSwitch;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logomain2);



        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();

        menu.getItem(0).setChecked(false);

        /*
        Menu menu = bottomNavigationView.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {

            if(i == 0)
                menu.getItem(0).setChecked(true);
            else
                menu.getItem(i).setChecked(false);
        }
        */

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.manage_friends:
                        //setContentView(R.layout.activity_manage_friends);
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
                   //case R.id.track_activity:
                        //goToTrackFriendsActivity();
                        //return true;
                    //default:
                      //return super.onOptionsItemSelected(item);


                }


                return false;
            }
        });
        //getSupportActionBar().setTitle("Track Friends");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mAuth = FirebaseAuth.getInstance();

        listView = (ListView) findViewById(R.id.myFriends);
        trackingSwitch = (Switch) findViewById(R.id.trackSwitch);

        //final ArrayAdapter<String> friendsAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allFriend);

        final TrackAdapter friendsAdapter = new TrackAdapter(getApplicationContext(), R.layout.track_item);

        listView.setAdapter(friendsAdapter);

        final String thisUser = mAuth.getCurrentUser().getUid().toString();

        final Firebase trackingRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Information/Tracking");

        trackingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String canTrack = dataSnapshot.getValue().toString();


                    if (canTrack.equals("yes"))
                    {
                        trackingSwitch.setChecked(true);
                        Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
                        startService(intent);
                    }
                    else
                    {
                     trackingSwitch.setChecked(false);
                     Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
                     stopService(intent);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        trackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked){
                    Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
                    startService(intent);
                    trackingRef.setValue("yes");
                    //Toast.makeText(getApplicationContext(),"START", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
                    stopService(intent);
                    trackingRef.setValue("no");
                    //Toast.makeText(getApplicationContext(),"STOP", Toast.LENGTH_LONG).show();
                }

            }
        });

        //allFriends.add(thisUser);

        Firebase friendRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Friends");

        friendRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                //String friendName = dataSnapshot.getValue(String.class);
                final String friendName = dataSnapshot.getValue().toString();
                final String friendKey = dataSnapshot.getKey().toString();

                Firebase infoRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + friendKey + "/Information");

                infoRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2)
                    {

                        if(dataSnapshot2.child("Latitude").exists()) {
                            final String friendLat = dataSnapshot2.child("Latitude").getValue().toString();

                            final String friendLong = dataSnapshot2.child("Longitude").getValue().toString();

                            Firebase userRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Information");

                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot3) {

                                    if(dataSnapshot3.child("Latitude").exists()) {
                                        String userLat = dataSnapshot3.child("Latitude").getValue().toString();

                                        String userLong = dataSnapshot3.child("Longitude").getValue().toString();

                                        boolean myTest = friendsAdapter.isAlreadyInList(friendKey);

                                        if (myTest) {
                                            friendsAdapter.replaceList(friendKey, friendLat, friendLong, userLat, userLong);
                                            //Toast.makeText(TrackFriendsActivity.this, friendKey + " in list", Toast.LENGTH_SHORT).show();
                                        } else {
                                            friendsAdapter.add(friendName, friendKey, friendLat, friendLong, userLat, userLong);
                                            //Toast.makeText(TrackFriendsActivity.this, friendKey + " not in list", Toast.LENGTH_SHORT).show();
                                        }

                                        friendsAdapter.notifyDataSetChanged();
                                    }
                                    else
                                    {
                                        Toast.makeText(TrackFriendsActivity.this, "Enable Tracking atleast once to track friends ", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                        else
                            Toast.makeText(TrackFriendsActivity.this, friendName + " hasn't yet enabled tracking", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

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

    //void goToTrackFriendsActivity()
    //{
       //Intent intent = new Intent(TrackFriendsActivity.this, TrackFriendsActivity.class);
       //startActivity(intent);
    //}


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


    //manage what happens when options on the toolbar are clicked
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.manage:
                goToManageActivity();
                return true;
            case R.id.info:
                goToInformationActivity();
                return true;
            case R.id.signout:
                userSignOut();
                startActivity(new Intent(TrackFriendsActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
}