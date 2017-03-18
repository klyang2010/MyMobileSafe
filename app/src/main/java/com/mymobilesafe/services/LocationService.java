package com.mymobilesafe.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.SpTools;

/**
 * 位置服务
 */

public class LocationService extends Service {

    private LocationManager lm;
    private LocationListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();
                double altitude = location.getAltitude();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                float speed = location.getSpeed();

                StringBuffer mess = new StringBuffer();
                mess.append(accuracy);
                mess.append(altitude);
                mess.append(latitude);
                mess.append(longitude);
                mess.append(speed);

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(SpTools.getString(getApplicationContext(), MyConstants.SAFENUMBER, ""), "", mess.toString(), null, null);
                stopSelf();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        lm.requestLocationUpdates("gps", 0, 0, listener);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(listener);
        lm = null;
        super.onDestroy();
    }
}
