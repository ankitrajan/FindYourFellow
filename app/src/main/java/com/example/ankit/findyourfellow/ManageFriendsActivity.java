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
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ManageFriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ArrayList<String> allFriend = new ArrayList<>();
    private ListView listView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Manage Friends");

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();

        menu.getItem(1).setChecked(false);


        /*
        Menu menu = bottomNavigationView.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {

            if(i == 1)
                menu.getItem(1).setChecked(true);
            else
                menu.getItem(i).setChecked(false);
        }
        */

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    //case R.id.manage_friends:
                        //goToManageActivity();
                        //return true;
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

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //focusableFalse();

        mAuth = FirebaseAuth.getInstance();

        listView = (ListView) findViewById(R.id.friendsList);

        final ManageAdapter adapter = new ManageAdapter(getApplicationContext(), R.layout.delete_item);


        /*
        final ArrayAdapter<String> requestAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allRequests);
        */
        listView.setAdapter(adapter);


        String thisUser = mAuth.getCurrentUser().getUid().toString();

        Firebase friendRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Friends");

        //allRequests.add(thisUser);

        friendRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String friend = dataSnapshot.getValue().toString();
                String id = dataSnapshot.getKey();

                //allKeys.add(id);
                adapter.add(friend, id);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                goToManageActivity();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ManageFriendsActivity.this, "position clicked: " + position, Toast.LENGTH_SHORT).show();
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

    //void goToUserIDActivity()
   // {
     //   Intent intent = new Intent(ManageFriendsActivity.this, UserIDActivity.class);
    //    startActivity(intent);
  //  }

    void goToTrackActivity()
    {
        Intent intent = new Intent(ManageFriendsActivity.this, TrackFriendsActivity.class);
        //Toast.makeText(getApplicationContext(), "Friend Managed", Toast.LENGTH_LONG).show();
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
        //Toast.makeText(getApplicationContext(), "Friend Managed", Toast.LENGTH_LONG).show();
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

        //focusableFalse();

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
            /*case R.id.track_activity:
                goToTrackActivity();
                return true;
            case R.id.information:
                goToInformationActivity();
                return true;
            case R.id.sign_out:
                userSignOut();
                startActivity(new Intent(ManageFriendsActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                return true; */
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
