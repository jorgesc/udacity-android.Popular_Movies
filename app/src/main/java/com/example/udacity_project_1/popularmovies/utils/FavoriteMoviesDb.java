package com.example.udacity_project_1.popularmovies.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by hit on 18/02/17.
 */

public class FavoriteMoviesDb {
    private FavoriteMoviesDb() {}

    public static class FavoriteTable implements BaseColumns {
        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_RATING = "movieRating";
        public static final String COLUMN_MOVIE_DATE = "movieDate";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";

    }

    public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "favoriteMovies.db";
        private static final int DATABASE_VERSION = 1;


        public FavoriteMoviesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " +
                    FavoriteTable.TABLE_NAME + " (" +
                    FavoriteTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoriteTable.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                    FavoriteTable.COLUMN_MOVIE_TITLE + " VARCHAR(256) NOT NULL, " +
                    FavoriteTable.COLUMN_MOVIE_RATING + " DECIMAL(3,1), " +
                    FavoriteTable.COLUMN_MOVIE_DATE + " VARCHAR(10), " +
                    FavoriteTable.COLUMN_MOVIE_SYNOPSIS + " TEXT, " +
                    FavoriteTable.COLUMN_MOVIE_POSTER + " VARCHAR(256)" +
                    ");";
            db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FavoriteTable.TABLE_NAME);
            onCreate(db);
        }
    }
}
