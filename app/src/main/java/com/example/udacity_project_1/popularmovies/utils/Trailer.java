package com.example.udacity_project_1.popularmovies.utils;

import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by hit on 17/02/17.
 */


public class Trailer {

    private String name;
    private java.net.URL url;

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


