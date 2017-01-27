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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class DataFetcher {

    private static final String tmdbHost = "api.themoviedb.org";
    private static final String tmdbImageHost = "image.tmdb.org";
    private static final String tmdbImageSize = "w342";
    private static final String popularMoviesPath = "popular";
    private static final String topRatedMoviesPath = "top_rated";


    public static ArrayList<Movie> getPopularMovies() throws IOException{
        return queryTmdbAPIandClean(popularMoviesPath);

    }

    public static ArrayList<Movie> getTopRatedMovies() throws IOException{
        return queryTmdbAPIandClean(topRatedMoviesPath);

    }

    private static URL buildURL(String path) throws MalformedURLException{
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority(tmdbHost);

        builder.appendPath("3");
        builder.appendPath("movie");
        builder.appendPath(path);

        builder.appendQueryParameter("api_key", Constants.TMDB_API_KEY);
        URL output = new URL(builder.build().toString());
        Log.v("DataFetcher", "URL generated: " + output);
        return output;
    }

    private static URL buildImageURL (String path) throws MalformedURLException{

        String cleanedPath = path.replace("/", "");

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority(tmdbImageHost);
        builder.appendPath("t");
        builder.appendPath("p");
        builder.appendPath(tmdbImageSize);
        builder.appendPath(cleanedPath);
        URL output = new URL(builder.build().toString());
        Log.v("DataFetcher", "Image URL generated: " + output);
        return output;
    }

    private static JSONObject queryTmdbAPI(String query) throws IOException {
        try {
            URL url = buildURL(query);
            return NetworkUtils.getUrlAsJSON(url);
        }
        catch (JSONException e) {
            Log.e("DataFetcher", "exception", e);
        }
        return null;
    }

    private static ArrayList<Movie> queryTmdbAPIandClean(String query) throws IOException{
        JSONObject response = queryTmdbAPI(query);
        if (response == null) {
            return null;
        }
        else {
            return parseResponse(queryTmdbAPI(query));
        }
    }

    private static ArrayList<Movie> parseResponse(JSONObject response) {
        try {
            ArrayList<Movie> output = new ArrayList<>();
            JSONArray movies = response.getJSONArray("results");
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);


                String title = movie.get("original_title").toString();
                String date = prettyfyDate(movie.get("release_date").toString());
                String rating = movie.get("vote_average").toString();
                String synopsis = movie.get("overview").toString();
                String poster = buildImageURL(movie.get("poster_path").toString()).toString();

                Movie cleaned_movie = new Movie(title, date, rating, synopsis, poster);

                output.add(cleaned_movie);
            }
            return output;
        }
        catch (JSONException | MalformedURLException e){
            Log.e("DataFetcher", "exception", e);
        }
        return null;
    }

    private static String prettyfyDate(String date) {
        try {
            Calendar cal = Calendar.getInstance();
            String[] parts = date.split("-");
            cal.set(Calendar.MONTH, Integer.parseInt(parts[1]));
            SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.getDefault());
            String month_name = month_date.format(cal.getTime());

            return month_name + " " + parts[0];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            Log.d("DataFetcher", "exception", e);
            return date;
        }
    }

}
