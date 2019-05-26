package com.parveendala.rideshare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class Coordination implements Parcelable {
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;

    public Coordination() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getCoordination() {
        return new LatLng(latitude, longitude);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected Coordination(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Creator<Coordination> CREATOR = new Creator<Coordination>() {
        @Override
        public Coordination createFromParcel(Parcel source) {
            return new Coordination(source);
        }

        @Override
        public Coordination[] newArray(int size) {
            return new Coordination[size];
        }
    };
}
