package com.parveendala.rideshare.utils;

import android.content.Context;
import android.content.SharedPreferences;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class PrefUtils {

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
    }

    public static int getRideCounter(Context context) {
        return getSharedPref(context).getInt(Constants.RIDE_COUNTER, 1);
    }

    public static void setRideCounter(Context context, int value) {
        getSharedPref(context).edit().putInt(Constants.RIDE_COUNTER, value).commit();
    }

    public static String getRide(Context context, int counter) {
        return getSharedPref(context).getString(Constants.RIDE + context, null);
    }

    public static void saveRide(Context context, String ride) {
        int rideCount = getRideCounter(context);
        getSharedPref(context).edit().putString(Constants.RIDE + (rideCount + 1), ride).commit();
        setRideCounter(context, rideCount + 1);
    }

}
