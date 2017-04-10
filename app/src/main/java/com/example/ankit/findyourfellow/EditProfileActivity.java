package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

public class EditProfileActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText age;
    private EditText number;
    private EditText emergency1;
    private EditText emergency2;
    private EditText emergency3;
    private Button saveNewProfile;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firstName = (EditText) findViewById(R.id.editFirstName);
        lastName = (EditText) findViewById(R.id.editLastName);
        age = (EditText) findViewById(R.id.editAge);
        number = (EditText) findViewById(R.id.editPhone);
        emergency1 = (EditText) findViewById(R.id.editEmergency1);
        emergency2 = (EditText) findViewById(R.id.editEmergency2);
        emergency3 = (EditText) findViewById(R.id.editEmergency3);

        saveNewProfile = (Button) findViewById(R.id.profileSave);

        mAuth = FirebaseAuth.getInstance();

        populateView();

        saveNewProfile.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                editAccount();
            }
        });


    }

    private void populateView()
    {
        String thisUser = mAuth.getCurrentUser().getUid().toString();

        Firebase userInfoRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser +"/Information/");

        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstName.setText(dataSnapshot.child("FirstName").getValue().toString());
                lastName.setText(dataSnapshot.child("LastName").getValue().toString());
                age.setText(dataSnapshot.child("Age").getValue().toString());
                number.setText(dataSnapshot.child("PhoneNumber").getValue().toString());
                emergency1.setText(dataSnapshot.child("EmergencyNumber1").getValue().toString());

                if(dataSnapshot.child("EmergencyNumber2").exists())
                {
                    emergency2.setText(dataSnapshot.child("EmergencyNumber2").getValue().toString());
                }
                else
                {
                    emergency2.setHint("No 2nd contact");
                }

                if(dataSnapshot.child("EmergencyNumber3").exists())
                {
                    emergency3.setText(dataSnapshot.child("EmergencyNumber3").getValue().toString());
                }
                else
                {
                    emergency3.setHint("No 3rd contact");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void editAccount()
    {
        final String fName = firstName.getText().toString();
        final String lName = lastName.getText().toString();
        final String newAge = age.getText().toString();
        final String phoneNumber = number.getText().toString();
        final String emergencyNumber1 = emergency1.getText().toString();
        final String emergencyNumber2 = emergency2.getText().toString();
        final String emergencyNumber3 = emergency3.getText().toString();

        if(!(TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName) || TextUtils.isEmpty(newAge) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(emergencyNumber1)))
        {

            String thisUser = mAuth.getCurrentUser().getUid().toString();

            Firebase userInfoRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser +"/Information/");

            userInfoRef.child("FirstName").setValue(fName);

            userInfoRef.child("LastName").setValue(lName);


            if(Double.parseDouble(newAge) < 123)
            {
                userInfoRef.child("Age").setValue(newAge);
            }
            else
            {
                Toast.makeText(this, "Are you really " + newAge + " years old?", Toast.LENGTH_SHORT).show();
            }

            if(phoneNumber.length()<10)
            {
                Toast.makeText(this, "Phone number length incorrect", Toast.LENGTH_SHORT).show();
            }
            else
            {
                userInfoRef.child("PhoneNumber").setValue(phoneNumber);
            }

            if(emergencyNumber1.length()<10)
            {
                Toast.makeText(this, "Emergency Number 1 incorrect", Toast.LENGTH_SHORT).show();
            }
            else
            {
                userInfoRef.child("EmergencyNumber1").setValue(phoneNumber);
            }

            if(!(TextUtils.isEmpty(emergencyNumber2)))
            {
                if(emergencyNumber2.length()<10)
                {
                    Toast.makeText(this, "Emergency Number 2 length incorrect", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    userInfoRef.child("EmergencyNumber2").setValue(emergencyNumber2);
                }
            }
            else
            {
                userInfoRef.child("EmergencyNumber2").removeValue();
            }

            if(!(TextUtils.isEmpty(emergencyNumber3)))
            {
                if(emergencyNumber3.length()<10)
                {
                    Toast.makeText(this, "Emergency Number 3 length incorrect", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    userInfoRef.child("EmergencyNumber3").setValue(emergencyNumber3);
                }
            }
            else
            {
                userInfoRef.child("EmergencyNumber3").removeValue();
            }

            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();

            goToInformationActivity();
        }
        else
        {
            Toast.makeText(EditProfileActivity.this, "Field(s) cannot be left empty", Toast.LENGTH_LONG).show();
        }
    }

    void goToInformationActivity()
    {
        Intent intent = new Intent(EditProfileActivity.this, InformationActivity.class);
        startActivity(intent);
    }

}
