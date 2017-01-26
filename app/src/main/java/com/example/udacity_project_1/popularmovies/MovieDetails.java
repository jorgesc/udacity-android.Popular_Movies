package com.example.udacity_project_1.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MovieDetails extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        TextView movieTitle = (TextView) findViewById(R.id.tv_movie_details_title);
        TextView movieSynopsis = (TextView) findViewById(R.id.tv_movie_details_synopsis);
        TextView movieDate = (TextView) findViewById(R.id.tv_movie_details_release_date);
        ImageView moviePoster = (ImageView) findViewById(R.id.iv_movie_details_poster);
        RatingBar movieRating = (RatingBar) findViewById(R.id.rb_movie_details_rating);

        Intent builderIntent = getIntent();

        if (builderIntent.hasExtra("movie_title")) {
            movieTitle.setText(builderIntent.getStringExtra("movie_title"));
        }

        if (builderIntent.hasExtra("movie_synopsis")) {
            movieSynopsis.setText(builderIntent.getStringExtra("movie_synopsis"));
        }

        if (builderIntent.hasExtra("movie_date")) {
            movieDate.setText(builderIntent.getStringExtra("movie_date"));
        }

        if (builderIntent.hasExtra("movie_poster")) {
            Picasso.with(this).load(builderIntent.getStringExtra("movie_poster")).into(moviePoster);
        }

        if (builderIntent.hasExtra("movie_rating")) {
            movieRating.setRating(builderIntent.getFloatExtra("movie_rating", 0.0f));
        }

    }
}
