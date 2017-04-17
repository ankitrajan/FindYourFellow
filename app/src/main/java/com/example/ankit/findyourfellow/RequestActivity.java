package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

public class RequestActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ListView requestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friend Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Lock into portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        requestView = (ListView) findViewById(R.id.requestList);

        //Setup custom adapter
        final RequestAdapter adapter = new RequestAdapter(getApplicationContext(), R.layout.list_item);
        requestView.setAdapter(adapter);

        String thisUser = mAuth.getCurrentUser().getUid().toString();

        Firebase requestRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/FriendRequest");

        requestRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Display all friends
                String friend = dataSnapshot.getValue(String.class);
                String id = dataSnapshot.getKey();

                adapter.add(friend, id);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                //Refresh activity if friend is deleted
                goToTrackActivity();
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

    void goToTrackActivity()
    {
        Intent intent = new Intent(RequestActivity.this, TrackFriendsActivity.class);
        startActivity(intent);
    }

    void goToManageActivity()
    {
        Intent intent = new Intent(RequestActivity.this, ManageFriendsActivity.class);
        startActivity(intent);
    }

    void goToInformationActivity()
    {
        Intent intent = new Intent(RequestActivity.this, InformationActivity.class);
        startActivity(intent);
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
            case R.id.track:
                goToTrackActivity();
                return true;
            case R.id.manage:
                goToManageActivity();
                return true;
            case R.id.info:
                goToInformationActivity();
                return true;
            case R.id.signout:
                userSignOut();
                startActivity(new Intent(RequestActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
