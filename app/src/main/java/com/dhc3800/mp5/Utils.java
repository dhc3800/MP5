package com.dhc3800.mp5;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class Utils {

    private static int ringerMode;
    private static AudioManager AUDIOMANAGER;

    public static void Silence(Context context) {
        AUDIOMANAGER = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ringerMode = AUDIOMANAGER.getRingerMode();
        AUDIOMANAGER.setRingerMode(AUDIOMANAGER.RINGER_MODE_VIBRATE);
    }

    public static void Ring(Context context) {
        AUDIOMANAGER = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AUDIOMANAGER.setRingerMode(AUDIOMANAGER.RINGER_MODE_NORMAL);
    }

    public static void failedLocationRetrieve(Context context) {
        Toast.makeText(context,"Failed to retrieve set location", Toast.LENGTH_LONG).show();
    }

    public static void failedGeoFenceRetrieve(Context context) {
        Toast.makeText(context, "Failed to set GeoFence", Toast.LENGTH_LONG).show();
    }




}
