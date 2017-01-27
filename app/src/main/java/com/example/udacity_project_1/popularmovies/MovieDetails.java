package com.example.udacity_project_1.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.udacity_project_1.popularmovies.utils.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetails extends AppCompatActivity {

    @BindView(R.id.tv_movie_details_title) TextView movieTitle;
    @BindView(R.id.tv_movie_details_synopsis) TextView movieSynopsis;
    @BindView(R.id.tv_movie_details_release_date) TextView movieDate;
    @BindView(R.id.iv_movie_details_poster) ImageView moviePoster;
    @BindView(R.id.rb_movie_details_rating) RatingBar movieRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        Intent builderIntent = getIntent();

        if (builderIntent.hasExtra("movie")){
            Movie movie = builderIntent.getParcelableExtra("movie");

            movieTitle.setText(movie.title);
            movieSynopsis.setText(movie.synopsis);
            movieDate.setText(movie.date);
            movieRating.setRating(Float.valueOf(movie.rating) / 2);
            Picasso.with(this)
                    .load(movie.poster)
                    .error(R.mipmap.img_movie_poster_placeholder)
                    .into(moviePoster);
        }

    }
}
