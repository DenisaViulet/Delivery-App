package com.example.licenta2.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class GeocodingCoordinates implements Parcelable {

    private double latitude;
    private double longitude;

    public GeocodingCoordinates() {
    }

    public GeocodingCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected GeocodingCoordinates(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<GeocodingCoordinates> CREATOR = new Creator<GeocodingCoordinates>() {
        @Override
        public GeocodingCoordinates createFromParcel(Parcel in) {
            return new GeocodingCoordinates(in);
        }

        @Override
        public GeocodingCoordinates[] newArray(int size) {
            return new GeocodingCoordinates[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
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
}
