package com.dhc3800.mp5;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.dhc3800.mp5.MainActivity;
import com.dhc3800.mp5.SetLocation;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GeoFencing extends IntentService {
    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList;


    public GeoFencing() {
        super("Geo");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Utils.Silence(this);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Utils.Ring(this);
        }

    }




    public void delete(Geofence fence, Context context) {
        ArrayList<String> remove = new ArrayList<>();
        remove.add(fence.getRequestId());
        LocationServices.getGeofencingClient(context).removeGeofences(remove);
    }





    public void updateList(SetLocation location, Context context) {
        geofencingClient = LocationServices.getGeofencingClient(context);
        geofenceList.add(new Geofence.Builder().setRequestId(location.id)
            .setCircularRegion(location.Latitude, location.Longitude, 100)
            .setExpirationDuration(-1)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
            .build());

    }
}
