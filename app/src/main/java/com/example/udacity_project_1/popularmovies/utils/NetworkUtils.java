package com.example.udacity_project_1.popularmovies.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hit on 26/01/17.
 */

public class NetworkUtils {

    public static String getUrl(URL url) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String output = response.body().string();
        return output;
    }

    public static JSONObject getUrlAsJSON(URL url) throws IOException, JSONException{
        String response = getUrl(url);
        return new JSONObject(response);
    }
}
