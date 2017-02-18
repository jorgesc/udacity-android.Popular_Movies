package com.example.udacity_project_1.popularmovies.utils;

import android.provider.BaseColumns;

/**
 * Created by hit on 18/02/17.
 */

public class FavoriteMoviesDbContract {
    private FavoriteMoviesDbContract() {}

    public static class FavoriteTable implements BaseColumns {
        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_RATING = "movieRating";
        public static final String COLUMN_MOVIE_DATE = "movieDate";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";

    }

}
