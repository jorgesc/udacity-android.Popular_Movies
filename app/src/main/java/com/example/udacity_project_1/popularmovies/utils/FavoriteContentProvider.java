package com.example.udacity_project_1.popularmovies.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by hit on 18/02/17.
 */

public class FavoriteContentProvider extends ContentProvider {

    private SQLiteDatabase db;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(FavoriteContentProviderContract.AUTHORITY,
                FavoriteContentProviderContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(FavoriteContentProviderContract.AUTHORITY,
                FavoriteContentProviderContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return db.query(FavoriteMoviesDbContract.FavoriteTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            case FAVORITE_WITH_ID:
                return db.query(FavoriteMoviesDbContract.FavoriteTable.TABLE_NAME, projection,
                        FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_ID + "=?",
                        new String[]{uri.getPathSegments().get(1)}, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return "vnd.android.cursor.dir/" + FavoriteContentProviderContract.AUTHORITY + "/" + FavoriteContentProviderContract.PATH_FAVORITES;
            case FAVORITE_WITH_ID:
                return "vnd.android.cursor.dir/" + FavoriteContentProviderContract.AUTHORITY + "/" + FavoriteContentProviderContract.PATH_FAVORITES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                long id = db.insert(FavoriteMoviesDbContract.FavoriteTable.TABLE_NAME, null, values);
                if (id > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(FavoriteContentProviderContract.CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITE_WITH_ID:
                int deleted = db.delete(FavoriteMoviesDbContract.FavoriteTable.TABLE_NAME, selection, selectionArgs);
                if (deleted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                    return deleted;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITE_WITH_ID:
                int updated = db.update(FavoriteMoviesDbContract.FavoriteTable.TABLE_NAME, values, selection, selectionArgs);
                if (updated > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                    return updated;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return 0;
    }
}
