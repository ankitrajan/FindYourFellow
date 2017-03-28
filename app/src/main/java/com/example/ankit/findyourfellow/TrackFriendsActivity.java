package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Track Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mAuth = FirebaseAuth.getInstance();

        listView = (ListView) findViewById(R.id.myFriends);

        //final ArrayAdapter<String> friendsAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allFriend);

        final TrackAdapter friendsAdapter = new TrackAdapter(getApplicationContext(), R.layout.track_item);

        listView.setAdapter(friendsAdapter);

        final String thisUser = mAuth.getCurrentUser().getUid().toString();

        //allFriends.add(thisUser);

        Firebase friendRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Friends");

        friendRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //String friendName = dataSnapshot.getValue(String.class);
                final String friendName = dataSnapshot.getValue().toString();
                final String friendKey = dataSnapshot.getKey().toString();

                Firebase infoRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + friendKey + "/Information");

                infoRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2)
                    {

                        final String friendLat = dataSnapshot2.child("Latitude").getValue().toString();

                        final String friendLong = dataSnapshot2.child("Longitude").getValue().toString();

                        Firebase userRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Information");

                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot3) {

                                String userLat = dataSnapshot3.child("Latitude").getValue().toString();

                                String userLong = dataSnapshot3.child("Longitude").getValue().toString();

                                boolean myTest = friendsAdapter.isAlreadyInList(friendKey);

                                if (myTest)
                                {
                                    friendsAdapter.replaceList(friendKey, friendLat, friendLong, userLat, userLong);
                                    //Toast.makeText(TrackFriendsActivity.this, friendKey + " in list", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    friendsAdapter.add(friendName, friendKey, friendLat, friendLong, userLat, userLong);
                                    //Toast.makeText(TrackFriendsActivity.this, friendKey + " not in list", Toast.LENGTH_SHORT).show();
                                }

                                friendsAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
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
        mAuth.signOut();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //manage what happens when options on the toolbar are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.signout:
                userSignOut();
                startActivity(new Intent(TrackFriendsActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Action", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
