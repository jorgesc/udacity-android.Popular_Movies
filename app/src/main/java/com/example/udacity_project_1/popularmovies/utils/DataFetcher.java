package com.example.udacity_project_1.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hit on 26/01/17.
 */

public class DataFetcher {

    private final String tmdbHost = "api.themoviedb.org";
    private final String tmdbImageHost = "image.tmdb.org";
    private final String tmdbImageSize = "w185";
    private final String popularMoviesQuery = "popularity.desc";
    private final String topRatedMoviesQuery = "vote_average.desc";


    public JSONArray getPopularMovies() {
        return queryTmdbAPIandClean(popularMoviesQuery);

    }

    public JSONArray getTopRatedMovies() {
        return queryTmdbAPIandClean(topRatedMoviesQuery);

    }

    private URL buildURL(String query) throws MalformedURLException{
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority(tmdbHost);
        builder.path("/3/discover/movie");
        builder.appendQueryParameter("api_key", Constants.TMDB_API_KEY);
        builder.appendQueryParameter("sort_by", query);
        return new URL(builder.build().toString());
    }

    private URL buildImageURL (String path) throws MalformedURLException{
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority(tmdbImageHost);
        builder.appendPath("/t/p/");
        builder.appendPath(tmdbImageSize);
        builder.appendPath("/");
        builder.appendPath(path);
        return new URL(builder.build().toString());
    }

    private JSONObject queryTmdbAPI(String query) {
        try {
            URL url = buildURL(query);
            return NetworkUtils.getUrlAsJSON(url);
        } catch (IOException | JSONException e) {
            Log.e("DataFetcher", "exception", e);
        }
        return null;
    }

    private JSONArray queryTmdbAPIandClean(String query) {
        return parseResponse(queryTmdbAPI(query));
    }

    private JSONArray parseResponse(JSONObject response) {
        try {
            JSONArray output = new JSONArray();
            JSONArray movies = response.getJSONArray("results");
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);
                JSONObject cleaned_movie = new JSONObject();

                cleaned_movie.put("movie_title", movie.get("original_title").toString());
                cleaned_movie.put("movie_synopsis", movie.get("overview").toString());
                cleaned_movie.put("movie_date", prettyfyDate(movie.get("release_date").toString()));
                cleaned_movie.put("movie_poster", buildImageURL(movie.get("backdrop_path").toString()));
                cleaned_movie.put("movie_rating", movie.get("vote_average").toString());

                output.put(cleaned_movie);
            }
            return output;
        }
        catch (JSONException | MalformedURLException e){
            Log.e("DataFetcher", "exception", e);
        }
        return null;
    }

    private String prettyfyDate(String date) {
        Calendar cal = Calendar.getInstance();
        String[] parts = date.split("-");
        cal.set(Calendar.MONTH, Integer.parseInt(parts[1]));
        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.getDefault());
        String month_name = month_date.format(cal.getTime());

        return month_name + " " + parts[2];
    }

}