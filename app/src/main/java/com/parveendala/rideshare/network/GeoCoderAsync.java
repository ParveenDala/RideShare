package com.parveendala.rideshare.network;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class GeoCoderAsync extends AsyncTask<String, Void, String> {
    private double latitude, longitude;
    private WeakReference<Activity> activityWeakReference;

    public GeoCoderAsync(Activity activity, double latitude, double longitude) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Activity activity = activityWeakReference.get();
            if (activity != null || !activity.isFinishing() || !activity.isDestroyed()) {
                Geocoder geocoder = new Geocoder(activity, Locale.ENGLISH);
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StringBuilder str = new StringBuilder();
                if (Geocoder.isPresent()) {
                    if (null != addresses && addresses.size() > 0) {
                        return addresses.get(0).getAddressLine(0); //+ addresses.get(0).getAddressLine(1);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            Activity activity = activityWeakReference.get();
            if (activity != null || !activity.isFinishing() || !activity.isDestroyed() && activity instanceof OnGeoCoderCompleted) {
                ((OnGeoCoderCompleted) activity).onGeoCoderCompleted(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}