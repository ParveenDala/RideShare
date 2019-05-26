package com.parveendala.rideshare.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.parveendala.rideshare.utils.DirectionUtils;

import java.util.ArrayList;
import java.util.List;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class Leg implements Parcelable {
    @SerializedName("distance")
    private Info distance;
    @SerializedName("duration")
    private Info duration;
    @SerializedName("duration_in_traffic")
    private Info durationInTraffic;
    @SerializedName("end_address")
    private String endAddress;
    @SerializedName("end_location")
    private Coordination endLocation;
    @SerializedName("start_address")
    private String startAddress;
    @SerializedName("start_location")
    private Coordination startLocation;
    @SerializedName("steps")
    private List<Step> stepList;
    @SerializedName("via_waypoint")
    private List<Waypoint> viaWaypointList;

    public Info getDistance() {
        return distance;
    }

    public void setDistance(Info distance) {
        this.distance = distance;
    }

    public Info getDuration() {
        return duration;
    }

    public void setDuration(Info duration) {
        this.duration = duration;
    }

    public Info getDurationInTraffic() {
        return durationInTraffic;
    }

    public void setDurationInTraffic(Info durationInTraffic) {
        this.durationInTraffic = durationInTraffic;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public Coordination getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Coordination endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public Coordination getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Coordination startLocation) {
        this.startLocation = startLocation;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public List<Waypoint> getViaWaypointList() {
        return viaWaypointList;
    }

    public void setViaWaypointList(List<Waypoint> viaWaypointList) {
        this.viaWaypointList = viaWaypointList;
    }

    public ArrayList<LatLng> getDirectionPoint() {
        return DirectionUtils.getDirectionPoint(stepList);
    }

    public ArrayList<LatLng> getSectionPoint() {
        return DirectionUtils.getSectionPoint(stepList);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.distance, flags);
        dest.writeParcelable(this.duration, flags);
        dest.writeParcelable(this.durationInTraffic, flags);
        dest.writeString(this.endAddress);
        dest.writeParcelable(this.endLocation, flags);
        dest.writeString(this.startAddress);
        dest.writeParcelable(this.startLocation, flags);
        dest.writeTypedList(this.stepList);
        dest.writeTypedList(this.viaWaypointList);
    }

    public Leg() {
    }

    protected Leg(Parcel in) {
        this.distance = in.readParcelable(Info.class.getClassLoader());
        this.duration = in.readParcelable(Info.class.getClassLoader());
        this.durationInTraffic = in.readParcelable(Info.class.getClassLoader());
        this.endAddress = in.readString();
        this.endLocation = in.readParcelable(Coordination.class.getClassLoader());
        this.startAddress = in.readString();
        this.startLocation = in.readParcelable(Coordination.class.getClassLoader());
        this.stepList = in.createTypedArrayList(Step.CREATOR);
        this.viaWaypointList = in.createTypedArrayList(Waypoint.CREATOR);
    }

    public static final Creator<Leg> CREATOR = new Creator<Leg>() {
        @Override
        public Leg createFromParcel(Parcel source) {
            return new Leg(source);
        }

        @Override
        public Leg[] newArray(int size) {
            return new Leg[size];
        }
    };
}
