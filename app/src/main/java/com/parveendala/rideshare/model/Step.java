package com.parveendala.rideshare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class Step implements Parcelable {
    @SerializedName("distance")
    private Info distance;
    @SerializedName("duration")
    private Info duration;
    @SerializedName("end_location")
    private Coordination endLocation;
    @SerializedName("html_instructions")
    private String htmlInstruction;
    @SerializedName("maneuver")
    private String maneuver;
    @SerializedName("start_location")
    private Coordination startLocation;
    @SerializedName("steps")
    private List<Step> stepList;
    @SerializedName("polyline")
    private RoutePolyline polyline;
    @SerializedName("travel_mode")
    private String travelMode;

    public Step() {
    }


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

    public Coordination getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Coordination endLocation) {
        this.endLocation = endLocation;
    }

    public String getHtmlInstruction() {
        return htmlInstruction;
    }

    public void setHtmlInstruction(String htmlInstruction) {
        this.htmlInstruction = htmlInstruction;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
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

    public boolean isContainStepList() {
        return stepList != null && stepList.size() > 0;
    }

    public RoutePolyline getPolyline() {
        return polyline;
    }

    public void setPolyline(RoutePolyline polyline) {
        this.polyline = polyline;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.distance, flags);
        dest.writeParcelable(this.duration, flags);
        dest.writeParcelable(this.endLocation, flags);
        dest.writeString(this.htmlInstruction);
        dest.writeString(this.maneuver);
        dest.writeParcelable(this.startLocation, flags);
        dest.writeList(this.stepList);
        dest.writeParcelable(this.polyline, flags);
        dest.writeString(this.travelMode);
    }

    protected Step(Parcel in) {
        this.distance = in.readParcelable(Info.class.getClassLoader());
        this.duration = in.readParcelable(Info.class.getClassLoader());
        this.endLocation = in.readParcelable(Coordination.class.getClassLoader());
        this.htmlInstruction = in.readString();
        this.maneuver = in.readString();
        this.startLocation = in.readParcelable(Coordination.class.getClassLoader());
        this.stepList = new ArrayList<Step>();
        in.readList(this.stepList, Step.class.getClassLoader());
        this.polyline = in.readParcelable(RoutePolyline.class.getClassLoader());
        this.travelMode = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel source) {
            return new Step(source);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}
