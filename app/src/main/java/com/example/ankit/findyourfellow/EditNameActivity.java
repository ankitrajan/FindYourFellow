package com.example.ankit.findyourfellow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class EditNameActivity extends AppCompatActivity {

    Button saveButton;

    EditText newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Friend Name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Lock into portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Get current name
        Bundle extras = getIntent().getExtras();
        final String friendKey = extras.getString("FRIENDKEY");
        final String friendName = extras.getString("FRIENDNAME");
        final String thisUser = extras.getString("USERKEY");

        saveButton = (Button) findViewById(R.id.nameSave);

        newName = (EditText) findViewById(R.id.editName);

        newName.setHint(friendName);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Firebase newRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser +"/Friends/");

                String newText = newName.getText().toString();

                if(!(TextUtils.isEmpty(newText))) {
                    //Set new name in database and return to previous activity
                    newRef.child(friendKey).setValue(newName.getText().toString());
                    Toast.makeText(EditNameActivity.this, "Name changed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditNameActivity.this, ManageFriendsActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(EditNameActivity.this, "Field cannot be left empty", Toast.LENGTH_LONG).show();
            }
        });
    }
}
