package com.example.udacity_project_1.popularmovies.utils;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.udacity_project_1.popularmovies.MovieDetailsActivity;
import com.example.udacity_project_1.popularmovies.R;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivityFragment extends Fragment implements MoviesAdapter.GridItemClickListener{

    private MoviesAdapter moviesAdapter;
    private final String MOVIE_LIST_SAVE_KEY = "movies";
    private ContentResolver contentResolver;

    EmptyRecyclerView moviesRecyclerView;
    ProgressBar loadingBar;
    TextView errorMessage;
    TextView errorMessageNoContent;

    private View rootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentResolver = getActivity().getContentResolver();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        rootView =inflater.inflate(R.layout.activity_main_fragment, container, false);

        moviesRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.rv_movies);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.pb_loading_bar);
        errorMessage = (TextView) rootView.findViewById(R.id.tv_error_message);
        errorMessageNoContent = (TextView) rootView.findViewById(R.id.tv_error_message_no_content);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), calculateNumberOfColumns());
        moviesAdapter = new MoviesAdapter(this);
        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.setHasFixedSize(true);
        moviesRecyclerView.setAdapter(moviesAdapter);
        moviesRecyclerView.setVisibility(View.VISIBLE);
        moviesRecyclerView.setEmptyView(errorMessageNoContent);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(MOVIE_LIST_SAVE_KEY)) {
            Log.v("MainActivity", "Restoring");
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIE_LIST_SAVE_KEY);
            moviesAdapter.updateData(movies);
        }

        else {
            refresh("popular");
        }
        return rootView;

    }

    private int calculateNumberOfColumns() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 90);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST_SAVE_KEY,  moviesAdapter.getDataset());
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selection = item.getItemId();
        if (selection == R.id.action_popular){
            refresh("popular");
            getActivity().setTitle(getResources().getString(R.string.title_popular_movies));
        }
        else if (selection == R.id.action_top_rated){
            refresh("top_rated");
            getActivity().setTitle(getResources().getString(R.string.title_top_rated_movies));
        }
        else if (selection == R.id.action_favorites){
            getActivity().setTitle(getResources().getString(R.string.title_favorite_movies));
            new ShowFavoritesAsync().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGridItemClick(int index) {
        Movie movie= moviesAdapter.getDatasetElement(index);
        Log.d("MainActivity", "Clicked on movie " + movie.title);
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    class ShowFavoritesAsync extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            showLoadingScreen();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return contentResolver.query(FavoriteContentProviderContract.CONTENT_URI, null, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            ArrayList<Movie> movies = new ArrayList<>();

            while (cursor.moveToNext()) {
                Movie movie = new Movie(
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_DATE)),
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_RATING)),
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_SYNOPSIS)),
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_POSTER)),
                        cursor.getInt(cursor.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_ID))
                );
                movies.add(movie);
            }
            cursor.close();
            moviesAdapter.updateData(movies);
            showMoviesView();
        }
    }


    private void refresh(String query)
    {
        new DownloadMoviesDataAsync().execute(query);
    }

    private void showLoadingScreen(){
        moviesRecyclerView.setVisibility(View.INVISIBLE);
        errorMessageNoContent.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void showNetworkNotAvailableMessage() {
        moviesRecyclerView.setVisibility(View.INVISIBLE);
        loadingBar.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void showMoviesView() {
        loadingBar.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        moviesRecyclerView.setVisibility(View.VISIBLE);
    }


    private class DownloadMoviesDataAsync extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            Log.d("DownloadMoviesDataAsync", "Starting Async task");
            showLoadingScreen();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            ArrayList<Movie> output = null;

            try {

                switch (params[0]) {
                    case "popular":
                        output = DataFetcher.getPopularMovies();
                        break;
                    case "top_rated":
                        output = DataFetcher.getTopRatedMovies();
                        break;
                }
            }
            catch (IOException e) {
                Log.e("MainActivity", "No network", e);
            }
            return output;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> output) {
            if (output != null) {
                Log.v("MainActivity", "Got data: " + output.toString());
                moviesAdapter.updateData(output);
                showMoviesView();
            }
            else {
                showNetworkNotAvailableMessage();
            }
            Log.d("DownloadMoviesDataAsync", "Finished Async task");

        }
    }

}
