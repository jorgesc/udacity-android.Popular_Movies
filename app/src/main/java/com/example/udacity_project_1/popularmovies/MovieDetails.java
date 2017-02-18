package com.example.udacity_project_1.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.udacity_project_1.popularmovies.utils.DataFetcher;
import com.example.udacity_project_1.popularmovies.utils.Movie;
import com.example.udacity_project_1.popularmovies.utils.MovieExtra;
import com.example.udacity_project_1.popularmovies.utils.Review;
import com.example.udacity_project_1.popularmovies.utils.ReviewsAdapter;
import com.example.udacity_project_1.popularmovies.utils.TrailersAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieExtra>{

    @BindView(R.id.tv_movie_details_title) TextView movieTitle;
    @BindView(R.id.tv_movie_details_synopsis) TextView movieSynopsis;
    @BindView(R.id.tv_movie_details_release_date) TextView movieDate;
    @BindView(R.id.iv_movie_details_poster) ImageView moviePoster;
    @BindView(R.id.rb_movie_details_rating) RatingBar movieRating;

    @BindView(R.id.rv_movie_details_trailers) RecyclerView movieTrailers;
    @BindView(R.id.tv_movie_details_trailers_error) TextView noTrailersError;

    @BindView(R.id.rv_movie_details_reviews) RecyclerView movieReviews;
    @BindView(R.id.tv_movie_details_reviews_error) TextView noReviewsError;

    @BindView(R.id.pb_movie_details_extra) ProgressBar loadingBar;
    @BindView(R.id.ll_movie_details_extra_container) LinearLayout extraContainer;

    @BindView(R.id.tv_movie_details_no_internet) TextView noInternetError;


    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;

    private final int LOADER_CODE = 238923;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        Intent builderIntent = getIntent();

        // INIT STUFF

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


            movieReviews.setLayoutManager(new LinearLayoutManager(this));
            reviewsAdapter = new ReviewsAdapter();
            movieReviews.setAdapter(reviewsAdapter);
            movieReviews.setHasFixedSize(false);

            movieTrailers.setLayoutManager(new LinearLayoutManager(this));
            trailersAdapter = new TrailersAdapter();
            movieTrailers.setAdapter(trailersAdapter);
            movieTrailers.setHasFixedSize(false);



            // LOADER STUFF

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader loader = loaderManager.getLoader(LOADER_CODE);
            if (loader != null) {
                loader.forceLoad();
            }
            else {
                Bundle args = new Bundle();
                args.putInt("movieId", movie.movieId);
                loaderManager.initLoader(LOADER_CODE, args, this);
                loader = loaderManager.getLoader(LOADER_CODE);
                loader.forceLoad();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie, menu);
        return true;
    }

    @Override
    public Loader<MovieExtra> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieExtra>(this) {

            @Override
            protected void onStartLoading() {
                noInternetError.setVisibility(View.GONE);
                extraContainer.setVisibility(View.GONE);
                loadingBar.setVisibility(View.VISIBLE);
                super.onStartLoading();
            }

            @Override
            public MovieExtra loadInBackground() {
                try {
                    return DataFetcher.getMovieExtra(args.getInt("movieId"));
                }
                catch (IOException |JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieExtra> loader, MovieExtra data) {
        loadingBar.setVisibility(View.GONE);
        if (data == null){
            noInternetError.setVisibility(View.VISIBLE);

        }
        else {
            Log.v("MovieDetails", "Got " + data.getTrailers().size() + " trailers");
            Log.v("MovieDetails", "Got " + data.getReviews().size() + " reviews");
            reviewsAdapter.updateDataSet(data.getReviews());
            trailersAdapter.updateDataSet(data.getTrailers());


            extraContainer.setVisibility(View.VISIBLE);

            if (data.getReviews().size() > 0) {
                noReviewsError.setVisibility(View.GONE);
                movieReviews.setVisibility(View.VISIBLE);
                movieReviews.requestLayout();
                movieReviews.invalidate();
            } else {
                movieReviews.setVisibility(View.GONE);
                noReviewsError.setVisibility(View.VISIBLE);
            }

            if (data.getTrailers().size() > 0) {
                noTrailersError.setVisibility(View.GONE);
                movieTrailers.setVisibility(View.VISIBLE);
            } else {
                movieTrailers.setVisibility(View.GONE);
                noTrailersError.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieExtra> loader) {

    }
}
