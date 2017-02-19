package com.example.udacity_project_1.popularmovies.utils.databaseUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoriteMovies.db";
    private static final int DATABASE_VERSION = 3;


    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " +
                FavoriteMoviesDbContract.FavoriteTable.TABLE_NAME + " (" +
                FavoriteMoviesDbContract.FavoriteTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_TITLE + " VARCHAR(256) NOT NULL, " +
                FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_RATING + " DECIMAL(3,1), " +
                FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_DATE + " VARCHAR(10), " +
                FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_SYNOPSIS + " TEXT, " +
                FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_POSTER + " VARCHAR(256)" +
                ");";
        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesDbContract.FavoriteTable.TABLE_NAME);
        onCreate(db);
    }
}


