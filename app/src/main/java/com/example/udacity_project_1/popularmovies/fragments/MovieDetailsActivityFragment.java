package com.example.udacity_project_1.popularmovies.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.udacity_project_1.popularmovies.R;
import com.example.udacity_project_1.popularmovies.adapters.ReviewsAdapter;
import com.example.udacity_project_1.popularmovies.adapters.TrailersAdapter;
import com.example.udacity_project_1.popularmovies.utils.DataFetcher;
import com.example.udacity_project_1.popularmovies.utils.databaseUtils.FavoriteContentProviderContract;
import com.example.udacity_project_1.popularmovies.utils.databaseUtils.FavoriteMoviesDbContract;
import com.example.udacity_project_1.popularmovies.utils.EmptyRecyclerView;
import com.example.udacity_project_1.popularmovies.objects.Movie;
import com.example.udacity_project_1.popularmovies.objects.MovieExtra;
import com.example.udacity_project_1.popularmovies.objects.Review;
import com.example.udacity_project_1.popularmovies.objects.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hit on 19/02/17.
 */

public class MovieDetailsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<MovieExtra>{
    TextView movieTitle;
    TextView movieSynopsis;
    TextView movieDate;
    ImageView moviePoster;
    RatingBar movieRating;
    EmptyRecyclerView movieTrailers;
    TextView noTrailersError;
    EmptyRecyclerView movieReviews;
    TextView noReviewsError;
    ProgressBar loadingBar;
    LinearLayout extraContainer;
    TextView noInternetError;

    private Movie movie;
    private View rootView;

    private ContentResolver contentResolver;

    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;

    private final int LOADER_CODE = 238923;

    private final String SAVE_STATE_REVIEWS_KEY = "reviews";
    private final String SAVE_STATE_TRAILERS_KEY = "trailers";
    private final String SAVE_STATE_MOVIE_KEY = "movie";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        contentResolver = getActivity().getContentResolver();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("MDActivityFrag", "onCreateView");

        rootView = inflater.inflate(R.layout.activity_movie_details_fragment, container, false);

        movieTitle = (TextView) rootView.findViewById(R.id.tv_movie_details_title);
        movieSynopsis = (TextView) rootView.findViewById(R.id.tv_movie_details_synopsis);
        movieDate = (TextView) rootView.findViewById(R.id.tv_movie_details_release_date);
        moviePoster = (ImageView) rootView.findViewById(R.id.iv_movie_details_poster);
        movieRating = (RatingBar) rootView.findViewById(R.id.rb_movie_details_rating);

        movieTrailers = (EmptyRecyclerView) rootView.findViewById(R.id.rv_movie_details_trailers);
        noTrailersError = (TextView) rootView.findViewById(R.id.tv_movie_details_trailers_error);

        movieReviews = (EmptyRecyclerView) rootView.findViewById(R.id.rv_movie_details_reviews);
        noReviewsError = (TextView) rootView.findViewById(R.id.tv_movie_details_reviews_error);

        loadingBar = (ProgressBar) rootView.findViewById(R.id.pb_movie_details_extra);
        extraContainer = (LinearLayout) rootView.findViewById(R.id.ll_movie_details_extra_container);

        noInternetError = (TextView) rootView.findViewById(R.id.tv_movie_details_no_internet);


        movieReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewsAdapter = new ReviewsAdapter();
        movieReviews.setAdapter(reviewsAdapter);
        movieReviews.setHasFixedSize(false);
        movieReviews.setEmptyView(noReviewsError);

        movieTrailers.setLayoutManager(new LinearLayoutManager(getActivity()));
        trailersAdapter = new TrailersAdapter(getActivity());
        movieTrailers.setAdapter(trailersAdapter);
        movieTrailers.setHasFixedSize(false);
        movieTrailers.setEmptyView(noTrailersError);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(SAVE_STATE_MOVIE_KEY)) {
            Log.d("MDActFrag", "From saved state");
            createFromSavedState(savedInstanceState);
        }
        else if(getArguments() != null) {
            Log.d("MDActFrag", "From fragment");
            Bundle args = getArguments();
            Movie m = args.getParcelable("movie");
            Log.v("MDActFrag", "Movie title: " + m.title);
            createFromFragment(m);
        }
        else {
            Log.d("MDActFrag", "From intent");
            createFromIntent();
        }

        return rootView;
    }

    private void fillMovieDetails() {
        Log.d("MDActFrag", "Filling movie details: " + movie.title);
        movieTitle.setText(movie.title);
        movieSynopsis.setText(movie.synopsis);
        movieDate.setText(movie.date);
        movieRating.setRating(Float.valueOf(movie.rating) / 2);
        Picasso.with(getActivity())
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
        Intent builderIntent = getActivity().getIntent();
        if (builderIntent.hasExtra("movie")){
            movie = builderIntent.getParcelableExtra("movie");
            createFromFragment(movie);
        }
    }

    private void createFromSavedState(Bundle savedInstanceState) {
        movie = savedInstanceState.getParcelable(SAVE_STATE_MOVIE_KEY);
        ArrayList<Trailer> trailers = savedInstanceState.getParcelableArrayList(SAVE_STATE_TRAILERS_KEY);
        ArrayList<Review> reviews = savedInstanceState.getParcelableArrayList(SAVE_STATE_REVIEWS_KEY);
        fillMovieDetails();
        fillMovieExtra(trailers, reviews);
    }

    private void createFromFragment(Movie nMovie){

        movie = nMovie;
        fillMovieDetails();
        // LOADER STUFF

        LoaderManager loaderManager = getActivity().getLoaderManager();
        Bundle args = new Bundle();
        args.putInt("movieId", movie.movieId);

        Loader loader = loaderManager.getLoader(LOADER_CODE);
        if (loader != null) {
            loaderManager.destroyLoader(LOADER_CODE);
        }

        loaderManager.initLoader(LOADER_CODE, args, this);
        loader = loaderManager.getLoader(LOADER_CODE);
        loader.forceLoad();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVE_STATE_MOVIE_KEY, movie);
        outState.putParcelableArrayList(SAVE_STATE_REVIEWS_KEY, reviewsAdapter.getDataset());
        outState.putParcelableArrayList(SAVE_STATE_TRAILERS_KEY, trailersAdapter.getDataset());
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie, menu);
        if (isCurrentMovieOnDb()) {
            MenuItem item;
            for (int i = 0; i < menu.size(); i++) {
                item = menu.getItem(i);
                if (item.getItemId() == R.id.action_add_as_fav){
                    item.setIcon(getResources().getDrawable(R.drawable.ic_grade_black_24dp));
                }
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_as_fav) {
            Log.v("MovieDetailsActivity", "Click fav button");
            onFavButtonClick(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<MovieExtra> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieExtra>(getActivity()) {

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
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
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
        Log.v("MovieDetailsActivity", "Uri generated:  " + uri);
        int deleted = contentResolver.delete(uri, null, null);
        Log.v("MovieDetailsActivity", "Deleted " + deleted + " movies.");
    }
}
