package com.example.udacity_project_1.popularmovies.utils;

import java.util.ArrayList;

/**
 * Created by hit on 17/02/17.
 */

public class MovieExtra {
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;

    MovieExtra(ArrayList<Trailer> t, ArrayList<Review> r) {
        trailers = t;
        reviews = r;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }
}

