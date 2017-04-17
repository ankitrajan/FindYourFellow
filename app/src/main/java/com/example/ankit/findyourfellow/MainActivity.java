package com.example.ankit.findyourfellow;

import android.*;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected Button createProfileButton;
    protected Button loginToProfileButton;
    protected Button reset;

    private EditText EmailField;
    private EditText PassworField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //Lock into portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        createProfileButton = (Button) findViewById(R.id.newCreateButton);
        loginToProfileButton = (Button) findViewById(R.id.loginButton);
        reset = (Button) findViewById(R.id.resetButton);

        EmailField = (EditText) findViewById(R.id.emailField);
        PassworField = (EditText) findViewById(R.id.passwordField);

        //If authentication state changes, login if authenticated
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null)
                    if (firebaseAuth.getCurrentUser().isEmailVerified())
                        goToTrackActivity();
            }
        };

        //Allow login if permissions are accepted
        if(!runtime_permissions())
            enable_buttons();
    }

    private void enable_buttons()
    {
        createProfileButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                goToCreateActivity();
            }
        });
        loginToProfileButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startLogin();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private boolean runtime_permissions()
    {
        //Get GPS location permission
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100)
        {
            //Get internet permission
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                enable_buttons();
            else
                runtime_permissions();
        }
    }


    void goToCreateActivity()
    {
        Intent intent = new Intent(MainActivity.this, CreateProfileActivity.class);
        startActivity(intent);
    }

    void goToTrackActivity()
    {
        Intent intent = new Intent(MainActivity.this, TrackFriendsActivity.class);
        startActivity(intent);
    }

    protected void onStart()
    {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    private void resetPassword()
    {
        String email = EmailField.getText().toString();
        if(!(TextUtils.isEmpty(email)))
        {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!(task.isSuccessful()))
                        Toast.makeText(MainActivity.this, "Email not linked to any account", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            Toast.makeText(this, "Email field(s) is empty", Toast.LENGTH_SHORT).show();
    }

    private void startLogin()
    {
        String email = EmailField.getText().toString();
        String password = PassworField.getText().toString();

        if(!(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)))
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (!(task.isSuccessful()))
                        Toast.makeText(MainActivity.this, "Email/Password incorrect", Toast.LENGTH_LONG).show();
                    else
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user.isEmailVerified())
                        {
                            // user is verified, so logged in, which will be detected by the AuthStateListener
                            finish();
                            Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // email is not verified,
                            FirebaseAuth.getInstance().signOut();

                            Toast.makeText(MainActivity.this, "Email not verified", Toast.LENGTH_SHORT).show();

                            //restart this activity
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
        else
            Toast.makeText(MainActivity.this, "Field(s) is/are empty", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        InternetAvailability();
    }

    private void InternetAvailability() {

        //If internet is disconnected, prompt user to reconnect to internet
        if (!isNetworkAvailable())
        {
            vibratePhone();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("No Internet Connection, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            goToMobileDataSettings();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "Internet Connection Not Avalailable", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
    }

    private boolean isNetworkAvailable() {
        //Check if network is available
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void goToMobileDataSettings()
    {
        Intent mobileIntent = new Intent();
        mobileIntent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity")); //for data enabled
        startActivity(mobileIntent);
    }

    public void vibratePhone(){

        addNotification();
        long[] pattern = {50,100,1000};
        Vibrator vibe=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(pattern,0);
        countDown();

        //when phone is sleeping
        BroadcastReceiver vibrateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && !isNetworkAvailable()) {
                    long[] pattern = {50,100,1000};
                    Vibrator vibe=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(pattern,0);
                    countDown();
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(vibrateReceiver, filter);
    }

    public void countDown(){

        new CountDownTimer(5000, 1000) { //5 sec countdown
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Vibrator v=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.cancel();
            }
        }.start();
    }

    private void addNotification() {

        //Send notification if there is no network
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logomain2) //logo
                        .setContentTitle("Alert! No Internet") //large text
                        .setContentText("You have no internet, connect to use app");// small text

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
