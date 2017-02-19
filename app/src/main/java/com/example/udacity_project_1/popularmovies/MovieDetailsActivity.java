package com.example.udacity_project_1.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.udacity_project_1.popularmovies.utils.DataFetcher;
import com.example.udacity_project_1.popularmovies.utils.EmptyRecyclerView;
import com.example.udacity_project_1.popularmovies.utils.FavoriteContentProviderContract;
import com.example.udacity_project_1.popularmovies.utils.FavoriteMoviesDbContract;
import com.example.udacity_project_1.popularmovies.utils.Movie;
import com.example.udacity_project_1.popularmovies.utils.MovieExtra;
import com.example.udacity_project_1.popularmovies.utils.Review;
import com.example.udacity_project_1.popularmovies.utils.ReviewsAdapter;
import com.example.udacity_project_1.popularmovies.utils.Trailer;
import com.example.udacity_project_1.popularmovies.utils.TrailersAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieExtra>{

    @BindView(R.id.tv_movie_details_title) TextView movieTitle;
    @BindView(R.id.tv_movie_details_synopsis) TextView movieSynopsis;
    @BindView(R.id.tv_movie_details_release_date) TextView movieDate;
    @BindView(R.id.iv_movie_details_poster) ImageView moviePoster;
    @BindView(R.id.rb_movie_details_rating) RatingBar movieRating;

    @BindView(R.id.rv_movie_details_trailers) EmptyRecyclerView movieTrailers;
    @BindView(R.id.tv_movie_details_trailers_error) TextView noTrailersError;

    @BindView(R.id.rv_movie_details_reviews) EmptyRecyclerView movieReviews;
    @BindView(R.id.tv_movie_details_reviews_error) TextView noReviewsError;

    @BindView(R.id.pb_movie_details_extra) ProgressBar loadingBar;
    @BindView(R.id.ll_movie_details_extra_container) LinearLayout extraContainer;

    @BindView(R.id.tv_movie_details_no_internet) TextView noInternetError;

    private Movie movie;

    private ContentResolver contentResolver;

    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;

    private final int LOADER_CODE = 238923;

    private final String SAVE_STATE_REVIEWS_KEY = "reviews";
    private final String SAVE_STATE_TRAILERS_KEY = "trailers";
    private final String SAVE_STATE_MOVIE_KEY = "movie";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        contentResolver = getContentResolver();

        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        movieReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewsAdapter = new ReviewsAdapter();
        movieReviews.setAdapter(reviewsAdapter);
        movieReviews.setHasFixedSize(false);
        movieReviews.setEmptyView(noReviewsError);

        movieTrailers.setLayoutManager(new LinearLayoutManager(this));
        trailersAdapter = new TrailersAdapter(this);
        movieTrailers.setAdapter(trailersAdapter);
        movieTrailers.setHasFixedSize(false);
        movieTrailers.setEmptyView(noTrailersError);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(SAVE_STATE_MOVIE_KEY)) {
            createFromSavedState(savedInstanceState);
        }
        else {
            createFromIntent();
        }
    }


    private void fillMovieDetails() {
        movieTitle.setText(movie.title);
        movieSynopsis.setText(movie.synopsis);
        movieDate.setText(movie.date);
        movieRating.setRating(Float.valueOf(movie.rating) / 2);
        Picasso.with(this)
                .load(movie.poster)
                .error(R.mipmap.img_movie_poster_placeholder)
                .into(moviePoster);

    }

    private void fillMovieExtra(ArrayList<Trailer> t, ArrayList<Review> r) {
        reviewsAdapter.updateDataSet(r);
        trailersAdapter.updateDataSet(t);

        extraContainer.setVisibility(View.VISIBLE);

    }

    private void createFromIntent(){

        // INIT STUFF
        Intent builderIntent = getIntent();
        if (builderIntent.hasExtra("movie")){
            movie = builderIntent.getParcelableExtra("movie");
            fillMovieDetails();

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

    private void createFromSavedState(Bundle savedInstanceState) {
        movie = savedInstanceState.getParcelable(SAVE_STATE_MOVIE_KEY);
        ArrayList<Trailer> trailers = savedInstanceState.getParcelableArrayList(SAVE_STATE_TRAILERS_KEY);
        ArrayList<Review> reviews = savedInstanceState.getParcelableArrayList(SAVE_STATE_REVIEWS_KEY);
        fillMovieDetails();
        fillMovieExtra(trailers, reviews);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVE_STATE_MOVIE_KEY, movie);
        outState.putParcelableArrayList(SAVE_STATE_REVIEWS_KEY, reviewsAdapter.getDataset());
        outState.putParcelableArrayList(SAVE_STATE_TRAILERS_KEY, trailersAdapter.getDataset());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie, menu);
        if (isCurrentMovieOnDb()) {
            MenuItem item = menu.getItem(0);
            item.setIcon(getResources().getDrawable(R.drawable.ic_grade_black_24dp));
        }
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
            Log.d("MovieDetailsActivity", "No internet");
        }
        else {
            Log.v("MovieDetailsActivity", "Got " + data.getTrailers().size() + " trailers");
            Log.v("MovieDetailsActivity", "Got " + data.getReviews().size() + " reviews");
            fillMovieExtra(data.getTrailers(), data.getReviews());
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieExtra> loader) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_as_fav) {
            Log.v("MovieDetailsActivity", "Click fav button");
            onFavButtonClick(item);
        }
        return super.onOptionsItemSelected(item);
    }


    private void onFavButtonClick(MenuItem item) {
        if (isCurrentMovieOnDb()) {
            Log.v("MovieDetailsActivity", "Movie is currently on db, removing...");
            removeCurrentMovieFromDb();
            item.setIcon(getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
            showToast(getResources().getString(R.string.movie_removed_from_favorites));
        }
        else {
            Log.v("MovieDetailsActivity", "Movie is NOT currently on db, adding...");
            addCurrentMovieToDb();
            item.setIcon(getResources().getDrawable(R.drawable.ic_grade_black_24dp));
            showToast(getResources().getString(R.string.movie_added_to_favorites));
        }

    }

    private void showToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean isCurrentMovieOnDb () {
        Uri uri = FavoriteContentProviderContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.movieId)).build();
        Cursor c = contentResolver.query(uri, null, null, null, null);
        int count = c.getCount();
        c.close();
        return (count > 0);
    }

    private void addCurrentMovieToDb () {
        ContentValues values = new ContentValues();
        values.put(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_ID, movie.movieId);
        values.put(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_TITLE, movie.title);
        values.put(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_RATING, Float.valueOf(movie.rating));
        values.put(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_DATE, movie.date);
        values.put(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_SYNOPSIS, movie.synopsis);
        values.put(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_POSTER, movie.poster);

        contentResolver.insert(FavoriteContentProviderContract.CONTENT_URI, values);

    }

    private void removeCurrentMovieFromDb () {
        Uri uri = FavoriteContentProviderContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.movieId)).build();
        contentResolver.delete(uri, null, null);

    }
}
