package com.example.udacity_project_1.popularmovies.objects;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {
    public int movieId;
    public String title;
    public String date;
    public String rating;
    public String synopsis;
    public String poster;


    public Movie(String nTitle, String nDate, String nRating, String nSynopsis, String nPoster, int nMovieId){
        title = nTitle;
        date = nDate;
        rating = nRating;
        synopsis= nSynopsis;
        poster = nPoster;
        movieId = nMovieId;
    }

    private Movie(Parcel source){
        title = source.readString();
        date = source.readString();
        rating = source.readString();
        synopsis= source.readString();
        poster = source.readString();
        movieId = source.readInt();
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
        dest.writeInt(movieId);
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
