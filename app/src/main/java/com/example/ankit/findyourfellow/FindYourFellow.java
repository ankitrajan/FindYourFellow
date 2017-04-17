package com.example.ankit.findyourfellow;

import android.app.Application;

import com.firebase.client.Firebase;

public class FindYourFellow extends Application{

    @Override
    public void onCreate()
    {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
