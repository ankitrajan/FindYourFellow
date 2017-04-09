package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class FriendInfoActivity extends AppCompatActivity {

    protected TextView email;
    protected TextView phone;
    protected TextView emergency;
    protected TextView emergency2;
    protected TextView emergency3;
    protected TextView time;

    protected Button map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friend Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        email = (TextView) findViewById(R.id.emailView);
        phone = (TextView) findViewById(R.id.phoneView);
        emergency = (TextView) findViewById(R.id.emergencyView);
        emergency2 = (TextView) findViewById(R.id.emergency2View);
        emergency3 = (TextView) findViewById(R.id.emergency3View);
        time = (TextView) findViewById(R.id.lastView);


        map = (Button) findViewById(R.id.mapButton);

        Firebase friendInfoRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + getIntent().getStringExtra("FRIENDKEY") + "/Information");

        friendInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                email.setText(dataSnapshot.child("FirstName").getValue().toString() + " " + dataSnapshot.child("LastName").getValue().toString());
                phone.setText(dataSnapshot.child("PhoneNumber").getValue().toString());
                emergency.setText(dataSnapshot.child("EmergencyNumber1").getValue().toString());
                time.setText(dataSnapshot.child("LastUpdate").getValue().toString());

                if(dataSnapshot.child("EmergencyNumber2").exists())
                {
                    emergency2.setText(dataSnapshot.child("EmergencyNumber2").getValue().toString());
                }

                if(dataSnapshot.child("EmergencyNumber3").exists())
                {
                    emergency3.setText(dataSnapshot.child("EmergencyNumber3").getValue().toString());
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapActivity();
            }
        });

        /*
        String fLat = getIntent().getStringExtra("FRIENDLATITUDE");
        String fLong = getIntent().getStringExtra("FRIENDLONGITUDE");
        String uLat = getIntent().getStringExtra("USERLATITUDE");
        String uLong = getIntent().getStringExtra("USERLONGITUDE");


        Toast.makeText(this, "Friend Latitude: " + fLat, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Friend Longitude: " + fLong, Toast.LENGTH_SHORT).show();
        */
        //Toast.makeText(this, "User Latitude: " + uLat, Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "User Longitude: " + uLong, Toast.LENGTH_LONG).show();
    }

    void goToMapActivity()
    {
        Intent intent= new Intent(FriendInfoActivity.this, MapsActivity.class);
        intent.putExtra("FRIENDKEY", getIntent().getStringExtra("FRIENDKEY"));
        intent.putExtra("USERKEY", getIntent().getStringExtra("USERKEY"));
        startActivity(intent);
    }

}
