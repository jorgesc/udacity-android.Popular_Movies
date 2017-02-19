package com.example.udacity_project_1.popularmovies.utils.Objects;

import com.example.udacity_project_1.popularmovies.utils.Objects.Review;
import com.example.udacity_project_1.popularmovies.utils.Objects.Trailer;

import java.util.ArrayList;


public class MovieExtra {
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;

    public MovieExtra(ArrayList<Trailer> t, ArrayList<Review> r) {
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

