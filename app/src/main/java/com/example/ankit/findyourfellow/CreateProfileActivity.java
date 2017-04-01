package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateProfileActivity extends AppCompatActivity {


    private EditText mFirstName;
    private EditText mLastName;
    private EditText mAge;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mNumber;
    private EditText mEmergency;

    private Button mCreate;


    private Firebase mRootRef;
    private FirebaseAuth mAuth;


    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Your Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mFirstName = (EditText) findViewById(R.id.createFirstName);
        mLastName = (EditText) findViewById(R.id.createLastName);
        mAge = (EditText) findViewById(R.id.createAge);
        mEmail = (EditText) findViewById(R.id.createEmail);
        mPassword = (EditText) findViewById(R.id.createPassword);
        mNumber = (EditText) findViewById(R.id.createNumber);
        mEmergency = (EditText) findViewById(R.id.createEmergency);

        mCreate = (Button) findViewById(R.id.newProfileButton);

        mAuth = FirebaseAuth.getInstance();

        mRootRef = new Firebase("https://findyourfellow.firebaseio.com/Users");

        mCreate.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                createAccount();
            }
        });

    }

    private void createAccount()
    {
        final String firstName = mFirstName.getText().toString();
        final String lastName = mLastName.getText().toString();
        final String age = mAge.getText().toString();
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String phoneNumber = mNumber.getText().toString();
        final String emergencyNumber = mEmergency.getText().toString();

        if(!(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(age) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||  TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(emergencyNumber)))
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (!(task.isSuccessful()))
                            {
                                Toast.makeText(CreateProfileActivity.this, "Account not created", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                FirebaseUser user = mAuth.getCurrentUser();

                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // email sent

                                                    Toast.makeText(CreateProfileActivity.this, "Verification email was sent", Toast.LENGTH_LONG).show();

                                                    String newUser = mAuth.getCurrentUser().getUid().toString();

                                                    Firebase childRef = mRootRef.child(newUser);

                                                    Firebase infoRef = mRootRef.child("Information");
                                                    Firebase reqRef = mRootRef.child("FriendRequest");
                                                    Firebase friendRef = mRootRef.child("Information");


                                                    Firebase newRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + newUser +"/Information/");

                                                    Firebase firstRef = newRef.child("FirstName");

                                                    firstRef.setValue(firstName);

                                                    Firebase lastRef = newRef.child("LastName");

                                                    lastRef.setValue(lastName);

                                                    Firebase ageRef = newRef.child("Age");

                                                    ageRef.setValue(age);

                                                    Firebase emailRef = newRef.child("Email");

                                                    emailRef.setValue(email);

                                                    Firebase passwordRef = newRef.child("Password");

                                                    passwordRef.setValue(password);

                                                    Firebase phoneRef = newRef.child("PhoneNumber");

                                                    phoneRef.setValue(phoneNumber);

                                                    Firebase emergencyRef = newRef.child("EmergencyNumber1");

                                                    emergencyRef.setValue(emergencyNumber);

                                                    Firebase trackRef = newRef.child("Tracking");

                                                    trackRef.setValue("no");

                                                    // after email is sent just logout the user and finish this activity
                                                    mAuth.signOut();

                                                    startActivity(new Intent(CreateProfileActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                                else
                                                {
                                                    // email not sent, so display message and restart the activity or do whatever you wish to do

                                                    //restart this activity
                                                    Toast.makeText(CreateProfileActivity.this, "Email could not be verified", Toast.LENGTH_LONG).show();
                                                    finish();

                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(CreateProfileActivity.this, "Field(s) cannot be left empty", Toast.LENGTH_LONG).show();
        }


    }

}
