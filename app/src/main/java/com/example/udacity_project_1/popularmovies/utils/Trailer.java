package com.example.udacity_project_1.popularmovies.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;



public class Trailer implements Parcelable {

    private String name;
    private java.net.URL url;

    Trailer(String nName, URL nUrl) {
        name = nName.trim();
        url = nUrl;
    }

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeSerializable(this.url);
    }

    protected Trailer(Parcel in) {
        this.name = in.readString();
        this.url = (java.net.URL) in.readSerializable();
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}


