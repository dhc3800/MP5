package com.dhc3800.mp5;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.FragmentTransaction;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private GeofencingClient geofencingClient;

    private RecyclerView recyclerView;
    private SetLocationAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SetLocation> locationList;
    private AutocompleteSupportFragment autocompleteFragment;
    private ArrayList<Geofence> geofenceList;

    private MapFragment mapFragment;
    private Helper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recylerView);
        locationList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        helper = new Helper(this);

        locationList = helper.getAll();
        mAdapter = new SetLocationAdapter(locationList);
        geofenceList = new ArrayList<>();
        addToGeoList(locationList);
        recyclerView.setAdapter(mAdapter);
        geofencingClient = LocationServices.getGeofencingClient(this);
        ((Button) findViewById(R.id.quietHours)).setText("Start Quiet Hours");
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.apikey));
        }
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autoComplete);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setHint("Search for a Location");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                SetLocation l = new SetLocation(place.getLatLng().latitude, place.getLatLng().longitude, place.getId(), place.getAddress(), place.getName());
                locationList.add(0, l);
                helper.insert(l);
                addToGeoList(l);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(Status status) {
                Toast.makeText(MainActivity.this, "An error occurred.", Toast.LENGTH_LONG).show();
            }
        });
//

    }






    private void addToGeoList(ArrayList<SetLocation> locationList) {
        for (SetLocation location: locationList) {
            geofenceList.add(new Geofence.Builder()
                .setRequestId(location.id)
                .setCircularRegion(location.Latitude, location.Longitude, 50)
                .setExpirationDuration(-1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(1000 * 60 * 5)
                .build());
        }
    }

    private void addToGeoList(SetLocation location) {
        geofenceList.add(new Geofence.Builder()
                .setRequestId(location.id)
                .setCircularRegion(location.Latitude, location.Longitude, 50)
                .setExpirationDuration(-1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(1000 * 60 * 5)
                .build());
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofenceList);
        return builder.build();
    }



    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeoFencing.class);
        PendingIntent geofencePendingIntent = PendingIntent.getService(this, 0 ,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void addGeoFence() {
        String[] permissions = new String[2];

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.VIBRATE}, 1);
            return;
        }




        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //do fragment stuff

                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Failed to add geofences
                        // ...
                    }
                });
    }




    private void removeGeoFence() {
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addGeoFence();
            } else {
                Toast.makeText(MainActivity.this, "Can't activiate geofences without permission", Toast.LENGTH_LONG).show();
                ((Button) findViewById(R.id.quietHours)).setText("Start Quiet Hours");
            }
        }
        if (requestCode == 2) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                (findViewById(R.id.quietHours)).callOnClick();
            } else {
                Toast.makeText(MainActivity.this, "Need permission to change phone notification policy", Toast.LENGTH_LONG).show();
            }
        }

    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.quietHours:
//                if ((NotificationManager) getSystemService(NOTIFICATION_SERVICE).isNoti)
                NotificationManager notificationManager =
                        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !notificationManager.isNotificationPolicyAccessGranted()) {

                    Intent intent = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                    startActivity(intent);
                    return;
                }
//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.VIBRATE}, 2);
//                    return;
//                }

                if (((Button)v).getText().toString() == "Start Quiet Hours") {



                    if (geofenceList.size() == 0) {
                        Toast.makeText(MainActivity.this, "Add geofences", Toast.LENGTH_LONG).show();
                        return;
                    }
                    addGeoFence();
                    AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);



                    Utils.Silence(this);
                    ((Button) v).setText("End Quiet Hours");
                    startActivity(new Intent(MainActivity.this, MapsWithPicker.class));


                } else {
                    ((Button) v).setText("Start Quiet Hours");
                    removeGeoFence();

                }

        }
    }



}
