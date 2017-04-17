package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class FriendInfoActivity extends AppCompatActivity {

    protected Button map;

    protected TextView email;
    protected TextView phone;
    protected TextView emergency;
    protected TextView emergency2;
    protected TextView emergency3;
    protected TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friend Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Lock into portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        map = (Button) findViewById(R.id.mapButton);

        email = (TextView) findViewById(R.id.emailView);
        phone = (TextView) findViewById(R.id.phoneView);
        emergency = (TextView) findViewById(R.id.emergencyView);
        emergency2 = (TextView) findViewById(R.id.emergency2View);
        emergency3 = (TextView) findViewById(R.id.emergency3View);
        time = (TextView) findViewById(R.id.lastView);

        Firebase friendInfoRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + getIntent().getStringExtra("FRIENDKEY") + "/Information");

        friendInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Display friend information
                email.setText(dataSnapshot.child("FirstName").getValue().toString() + " " + dataSnapshot.child("LastName").getValue().toString());
                phone.setText(dataSnapshot.child("PhoneNumber").getValue().toString());
                emergency.setText(dataSnapshot.child("EmergencyNumber1").getValue().toString());
                time.setText(dataSnapshot.child("LastUpdate").getValue().toString());

                if(dataSnapshot.child("EmergencyNumber2").exists())
                    emergency2.setText(dataSnapshot.child("EmergencyNumber2").getValue().toString());

                if(dataSnapshot.child("EmergencyNumber3").exists())
                    emergency3.setText(dataSnapshot.child("EmergencyNumber3").getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getCode());
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapActivity();
            }
        });
    }

    void goToMapActivity()
    {
        //Send information to map
        Intent intent= new Intent(FriendInfoActivity.this, MapsActivity.class);
        intent.putExtra("FRIENDKEY", getIntent().getStringExtra("FRIENDKEY"));
        intent.putExtra("USERKEY", getIntent().getStringExtra("USERKEY"));
        intent.putExtra("FRIENDNAME", getIntent().getStringExtra("FRIENDNAME"));
        startActivity(intent);
    }
}
