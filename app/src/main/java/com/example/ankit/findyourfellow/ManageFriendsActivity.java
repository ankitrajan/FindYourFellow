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
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

public class ManageFriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ListView listView;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage Friends");

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(1).setChecked(false);

        //Inflate bottom navigation items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.information:
                        goToInformationActivity();
                        return true;
                    case R.id.sign_out:
                        userSignOut();
                        startActivity(new Intent(ManageFriendsActivity.this, MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.track_activity:
                        goToTrackActivity();
                        return true;
                }
                return false;
            }
        });

        //Lock into portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listView = (ListView) findViewById(R.id.friendsList);

        //Setup custom adapter
        final ManageAdapter adapter = new ManageAdapter(getApplicationContext(), R.layout.delete_item);
        listView.setAdapter(adapter);

        String thisUser = mAuth.getCurrentUser().getUid().toString();

        Firebase friendRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Friends");

        friendRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Display all friends
                String friend = dataSnapshot.getValue().toString();
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
                goToManageActivity();
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

    void goToAddFriendActivity()
    {
        Intent intent = new Intent(ManageFriendsActivity.this, AddFriendActivity.class);
        startActivity(intent);
    }

    void goToRequestActivity()
    {
        Intent intent = new Intent(ManageFriendsActivity.this, RequestActivity.class);
        startActivity(intent);
    }

    void goToTrackActivity()
    {
        Intent intent = new Intent(ManageFriendsActivity.this, TrackFriendsActivity.class);
        startActivity(intent);
    }

   void goToManageActivity()
   {
        Intent intent = new Intent(ManageFriendsActivity.this, ManageFriendsActivity.class);
        startActivity(intent);
   }


    void goToInformationActivity()
    {
        Intent intent = new Intent(ManageFriendsActivity.this, InformationActivity.class);
        startActivity(intent);
    }

    private void userSignOut()
    {
        Intent intent = new Intent(getApplicationContext(), LocationHelper.class);
        stopService(intent);
        mAuth.signOut();
    }

    @Override
    protected void onResume() {
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(1).setChecked(false);

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_managerbutton, menu);
        return true;
    }

    //manage what happens when options on the toolbar are clicked
   @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.add_friend:
                //Toast.makeText(getApplicationContext(), "Add", Toast.LENGTH_LONG).show();
                goToAddFriendActivity();
                return true;
            case R.id.requests:
              //  Toast.makeText(getApplicationContext(), "Requests", Toast.LENGTH_LONG).show();
                goToRequestActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
