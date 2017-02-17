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
import java.util.List;
import java.util.Locale;

class Trailer {

    private String name;
    private URL url;

    Trailer(String nName, URL nUrl) {
        name = nName;
        url = nUrl;
    }

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }
}

class Review {
    private String author;
    private String content;

    Review(String nAuthor, String nContent) {
        author = nAuthor;
        content = nContent;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

}

public class DataFetcher {

    private static final String tmdbHost = "api.themoviedb.org";
    private static final String tmdbImageHost = "image.tmdb.org";
    private static final String tmdbImageSize = "w342";
    private static final String popularMoviesPath = "popular";
    private static final String topRatedMoviesPath = "top_rated";
    private static final String trailersPath = "videos";
    private static final String reviewsPath = "reviews";


    public static ArrayList<Movie> getPopularMovies() throws IOException{
        return queryTmdbAPIandClean(popularMoviesPath);

    }

    public static ArrayList<Movie> getTopRatedMovies() throws IOException{
        return queryTmdbAPIandClean(topRatedMoviesPath);

    }

    private static URL buildURL(ArrayList<String> path) throws MalformedURLException{
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority(tmdbHost);

        builder.appendPath("3");
        builder.appendPath("movie");
        for (String p : path) {
            builder.appendPath(p);
        }

        builder.appendQueryParameter("api_key", Constants.TMDB_API_KEY);
        URL output = new URL(builder.build().toString());
        Log.v("DataFetcher", "URL generated: " + output);
        return output;
    }

    private static URL buildURL(String path) throws MalformedURLException{
        ArrayList<String> paths = new ArrayList<>();
        paths.add(path);
        return buildURL(paths);
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

    private static URL buildDetailsURL (int movieId, String detail) throws MalformedURLException{
        ArrayList<String> paths = new ArrayList<>();
        paths.add(String.valueOf(movieId));
        paths.add(detail);
        return buildURL(paths);

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
                int movieId = Integer.valueOf(movie.get("id").toString());

                Movie cleaned_movie = new Movie(title, date, rating, synopsis, poster, movieId);

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


    private static URL generateYoutubeUrl(String key) throws MalformedURLException{
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("www.youtube.com");
        builder.path("watch");
        builder.appendQueryParameter("v", key);
        URL output = new URL(builder.build().toString());
        return output;

    }

    public static ArrayList<Trailer> getMovieTrailers(int movieId) throws IOException, JSONException{

        // TODO clean all this

        URL url = buildDetailsURL(movieId, trailersPath);
        JSONObject response = NetworkUtils.getUrlAsJSON(url);
        JSONArray results = response.getJSONArray("results");

        ArrayList<Trailer> output = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = (JSONObject) results.get(i);
            String name = result.getString("name");
            URL ytUrl = generateYoutubeUrl(result.getString("key"));
            output.add(new Trailer(name, ytUrl));
        }
        return output;
    }


    public static ArrayList<Review> getMovieReviews(int movieId)  throws IOException, JSONException{
        URL url = buildDetailsURL(movieId, reviewsPath);
        JSONObject response = NetworkUtils.getUrlAsJSON(url);
        JSONArray results = response.getJSONArray("results");

        ArrayList<Review> output = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = (JSONObject) results.get(i);
            String author = result.getString("author");
            String content = result.getString("content");
            output.add(new Review(author, content));
        }
        return output;
    }

}
