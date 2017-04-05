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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

public class AddFriendActivity extends AppCompatActivity {

    protected EditText searchEdit;
    protected Button requestButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        searchEdit = (EditText) findViewById(R.id.editSearch);

        requestButton = (Button) findViewById(R.id.sendRequest);

        mAuth = FirebaseAuth.getInstance();

        requestButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                final String searchRequest = searchEdit.getText().toString();

                String userID = mAuth.getCurrentUser().getUid().toString();
                //Toast.makeText(AddFriendActivity.this, "userID is " + mAuth.getCurrentUser().getUid().toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(AddFriendActivity.this, "search request is " + searchRequest, Toast.LENGTH_LONG).show();


                if (searchRequest.isEmpty())
                    Toast.makeText(AddFriendActivity.this, "Field cannot be left empty", Toast.LENGTH_LONG).show();
                else if (searchRequest.equals(mAuth.getCurrentUser().getUid().toString()))
                {
                    Toast.makeText(AddFriendActivity.this, "You cannot add yourself", Toast.LENGTH_LONG).show();
                }
                else
                {
                    final Firebase newFriendRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + searchRequest + "/Information/Email");

                    newFriendRef.addValueEventListener(new ValueEventListener()
                    {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            String  friendEmail = dataSnapshot.getValue(String.class);

                            if (friendEmail == null)
                            {
                                Toast.makeText(AddFriendActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                FirebaseUser user = mAuth.getCurrentUser();

                                Firebase friendListRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + searchRequest+ "/FriendRequest/");

                                Firebase addRequestRef = friendListRef.child(user.getUid().toString());

                                addRequestRef.setValue(user.getDisplayName().toString());

                                Toast.makeText(AddFriendActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                        {
                            System.out.println("The read failed: " + firebaseError.getCode());
                        }
                    });
                }

            }
        });
    }

    private void userSignOut()
    {
        mAuth.signOut();
    }

    void goToManageActivity()
    {
        Intent intent = new Intent(AddFriendActivity.this, ManageFriendsActivity.class);
        startActivity(intent);
    }

    void goToTrackActivity()
    {
        Intent intent = new Intent(AddFriendActivity.this, TrackFriendsActivity.class);
        //Toast.makeText(getApplicationContext(), "Friend Managed", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    void goToInformationActivity()
    {
        Intent intent = new Intent(AddFriendActivity.this, InformationActivity.class);
        startActivity(intent);
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
            case R.id.sign_out:
                userSignOut();
                startActivity(new Intent(AddFriendActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
