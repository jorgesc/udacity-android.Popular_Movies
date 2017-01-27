package com.example.udacity_project_1.popularmovies.utils;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {
    public String title;
    public String date;
    public String rating;
    public String synopsis;
    public String poster;


    Movie(String nTitle, String nDate, String nRating, String nSynopsis, String nPoster){
        title = nTitle;
        date = nDate;
        rating = nRating;
        synopsis= nSynopsis;
        poster = nPoster;
    }

    private Movie(Parcel source){
        title = source.readString();
        date = source.readString();
        rating = source.readString();
        synopsis= source.readString();
        poster = source.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(rating);
        dest.writeString(synopsis);
        dest.writeString(poster);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}