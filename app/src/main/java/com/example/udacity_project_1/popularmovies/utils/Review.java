package com.example.udacity_project_1.popularmovies.utils;

/**
 * Created by hit on 17/02/17.
 */

public class Review {
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

