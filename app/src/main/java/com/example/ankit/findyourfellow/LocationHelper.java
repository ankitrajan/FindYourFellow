package com.example.ankit.findyourfellow;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import android.location.LocationListener;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Date;

public class LocationHelper extends Service
{

    private LocationListener listener;
    private LocationManager locationManager;
    //private String lastUpdateTime;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        mAuth = FirebaseAuth.getInstance();

        String thisUser = mAuth.getCurrentUser().getUid().toString();

        final Firebase informationRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + thisUser + "/Information");

        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location)
            {
                Intent intent = new Intent("location_update");

                intent.putExtra("coordinates", location.getLongitude()+ " " + location.getLatitude());
                sendBroadcast(intent);

                //String lastUpdateTime = String.valueOf(location.getTime());

                String lastUpdateTime = (DateFormat.getDateTimeInstance().format(new Date())).toString();

                informationRef.child("Latitude").setValue(location.getLatitude());

                informationRef.child("Longitude").setValue(location.getLongitude());

                informationRef.child("LastUpdate").setValue(lastUpdateTime);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //Toast.makeText(this, "Attempting to get location", Toast.LENGTH_SHORT).show();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0, listener);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(locationManager != null)
        {
            locationManager.removeUpdates(listener);
        }
    }
}