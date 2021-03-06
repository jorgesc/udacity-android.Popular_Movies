package com.example.udacity_project_1.popularmovies.utils.databaseUtils;

import android.net.Uri;

public final class FavoriteContentProviderContract {
    private FavoriteContentProviderContract() {}
    public final static String AUTHORITY = "com.example.udacity_project_1.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


}
