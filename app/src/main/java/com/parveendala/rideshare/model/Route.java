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
public class Route implements Parcelable {

    private int rideId;
    private int distance;
    private int newDistance;


    @SerializedName("bounds")
    private Bound bound;
    @SerializedName("legs")
    private List<Leg> legList;
    @SerializedName("overview_polyline")
    private RoutePolyline overviewPolyline;
    @SerializedName("waypoint_order")
    private List<Integer> waypointOrderList;

    public Route() {
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public List<Leg> getLegList() {
        return legList;
    }

    public void setLegList(List<Leg> legList) {
        this.legList = legList;
    }

    public RoutePolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(RoutePolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public List<Integer> getWaypointOrderList() {
        return waypointOrderList;
    }

    public void setWaypointOrderList(List<Integer> waypointOrderList) {
        this.waypointOrderList = waypointOrderList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.bound, flags);
        dest.writeList(this.legList);
        dest.writeParcelable(this.overviewPolyline, flags);
        dest.writeList(this.waypointOrderList);
    }

    protected Route(Parcel in) {
        this.bound = in.readParcelable(Bound.class.getClassLoader());
        this.legList = new ArrayList<Leg>();
        in.readList(this.legList, Leg.class.getClassLoader());
        this.overviewPolyline = in.readParcelable(RoutePolyline.class.getClassLoader());
        this.waypointOrderList = new ArrayList<Integer>();
        in.readList(this.waypointOrderList, Integer.class.getClassLoader());
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel source) {
            return new Route(source);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
