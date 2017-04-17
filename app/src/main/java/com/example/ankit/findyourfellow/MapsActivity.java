package com.example.ankit.findyourfellow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friend Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /*
    Manipulates the map once available.
    This callback is triggered when the map is ready to be used.
    This is where we can add markers or lines, add listeners or move the camera.
    If Google Play services is not installed on the device, the user will be prompted to install
    it inside the SupportMapFragment. This method will only be triggered once the user has
    installed Google Play services and returned to the app.
    */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Get information
        Bundle extras = getIntent().getExtras();
        String friendKey = extras.getString("FRIENDKEY");
        String userKey = extras.getString("USERKEY");

        Firebase friendInfoRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + getIntent().getStringExtra("FRIENDKEY") + "/Information");

        friendInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String friendLat = dataSnapshot.child("Latitude").getValue().toString();
                final String friendLong = dataSnapshot.child("Longitude").getValue().toString();

                //Get correct format for map display
                double lat=Double.parseDouble(friendLat);
                double lng=Double.parseDouble(friendLong);

                mMap.clear();

                //Go to correct map part
                LatLng coordinates = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(coordinates).title("Friend's Location"));
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getCode());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    //manage what happens when options on the toolbar are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
