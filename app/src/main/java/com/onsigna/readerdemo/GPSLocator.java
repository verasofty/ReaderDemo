package com.onsigna.readerdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class GPSLocator implements Runnable {
    private final static String TAG = GPSLocator.class.getSimpleName();

    private final static String DEFAULT_LATITUD = "32.7185";
    private final static String DEFAULT_LONGITUD = "99.203823";
    public static final String MESSAGE_ERROR_GPS = "Señal de GPS no encontrada. Compruebe el estado del GPS";

    private Activity m_this;
    private static String m_latitud = DEFAULT_LATITUD;
    private static String m_longitud = DEFAULT_LONGITUD;

    private static MyLocationListener m_locationListener;
    private static LocationManager m_locationManager;
    private static Location currentLocation = null;
    static Activity activity;

    public GPSLocator(Activity activity) {
        this.activity = activity;
    }

    public void writeSignalGPS(Activity thisActivity) {
        Log.d(TAG, "== writingSignalGPS() ==");

        if (currentLocation != null) return;

        m_this = thisActivity;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        Log.d(TAG, "== Runnable.run() ==");

        try {
            m_locationManager = (LocationManager) m_this.getSystemService(Context.LOCATION_SERVICE);
            Looper.prepare();
            m_locationListener = new MyLocationListener();

            if (m_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.d(TAG, "--> Network provider enabled");
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, m_locationListener);
                Looper.loop();
                Looper.myLooper().quit();
            } else if (m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "--> GPS provider enabled");
                m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, m_locationListener);
                Looper.loop();
                Looper.myLooper().quit();
            } else if (m_locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                Log.d(TAG, "--> Passive provider enabled");
                m_locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1, 1, m_locationListener);
                Looper.loop();
                Looper.myLooper().quit();
            } else {
                Log.d(TAG, "--> None provider enabled");
                Toast.makeText(m_this.getBaseContext(), MESSAGE_ERROR_GPS, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private static Handler handlerGPS = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "== Handler.handleMessage()");

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            m_locationManager.removeUpdates(m_locationListener);
            if (currentLocation != null) {
                m_latitud = String.valueOf(currentLocation.getLatitude());
                m_longitud = String.valueOf(currentLocation.getLongitude());

                Log.d(TAG, "--> Latitud : " + m_latitud);
                Log.d(TAG, "--> longitud: " + m_longitud);

            }
        }
    };

    private void setCurrentLocation(Location loc) {
        currentLocation = loc;
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            Log.d(TAG, "== MyLocationListener.onLocationChanged() ==");
            Log.d(TAG, "--> loc = " + loc);


            if (loc != null) {
                setCurrentLocation(loc);
                m_latitud = String.valueOf(loc.getLatitude());
                m_longitud = String.valueOf(loc.getLongitude());
                handlerGPS.sendEmptyMessage(0);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "== onStatusChanged() ==");
            Log.d(TAG, "<-- provider : " + provider);
            Log.d(TAG, "<-- status : " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "== onProviderEnabled() ==");
            Log.d(TAG, "<-- provider : " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {

            Log.d(TAG, "== onProviderDisabled() ==");
            Log.d(TAG, "<-- provider : " + provider);

        }

    }


    public String getLatitud() {
        return m_latitud;
    }

    public String getLongitud() {
        return m_longitud;
    }
}
