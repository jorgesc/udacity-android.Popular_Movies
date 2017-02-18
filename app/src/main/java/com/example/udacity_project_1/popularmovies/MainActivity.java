package com.example.udacity_project_1.popularmovies;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.udacity_project_1.popularmovies.utils.DataFetcher;
import com.example.udacity_project_1.popularmovies.utils.FavoriteMoviesDbContract;
import com.example.udacity_project_1.popularmovies.utils.FavoriteMoviesDbHelper;
import com.example.udacity_project_1.popularmovies.utils.Movie;
import com.example.udacity_project_1.popularmovies.utils.MoviesAdapter;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener{

    private MoviesAdapter moviesAdapter;
    private final String MOVIE_LIST_SAVE_KEY = "movies";

    private SQLiteDatabase db;


    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;

    @BindView(R.id.pb_loading_bar) ProgressBar loadingBar;
    @BindView(R.id.tv_error_message) TextView errorMessage;

    private int calculateNumberOfColumns() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 90);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("MainActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNumberOfColumns());
        moviesAdapter = new MoviesAdapter(this);

        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.setHasFixedSize(true);
        moviesRecyclerView.setAdapter(moviesAdapter);
        moviesRecyclerView.setVisibility(View.VISIBLE);

        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(this);
        db = dbHelper.getReadableDatabase();

        if ((savedInstanceState != null) && savedInstanceState.containsKey(MOVIE_LIST_SAVE_KEY)) {
            Log.v("MainActivity", "Restoring");
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIE_LIST_SAVE_KEY);
            moviesAdapter.updateData(movies);
        }

        else {
            refresh("popular");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST_SAVE_KEY,  moviesAdapter.getDataset());
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selection = item.getItemId();
        if (selection == R.id.action_popular){
            refresh("popular");
            setTitle(getResources().getString(R.string.title_popular_movies));
        }
        else if (selection == R.id.action_top_rated){
            refresh("top_rated");
            setTitle(getResources().getString(R.string.title_top_rated_movies));
        }
        else if (selection == R.id.action_favorites){
            setTitle(getResources().getString(R.string.title_favorite_movies));
            show_favorites();
        }
        return super.onOptionsItemSelected(item);
    }

    private void show_favorites() {
        Cursor c = db.query(FavoriteMoviesDbContract.FavoriteTable.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Movie> movies = new ArrayList<>();

        while (c.moveToNext()) {
            Movie movie = new Movie(
                    c.getString(c.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_TITLE)),
                    c.getString(c.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_DATE)),
                    c.getString(c.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_RATING)),
                    c.getString(c.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_SYNOPSIS)),
                    c.getString(c.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_POSTER)),
                    c.getInt(c.getColumnIndex(FavoriteMoviesDbContract.FavoriteTable.COLUMN_MOVIE_ID))
                    );
            movies.add(movie);
        }
        moviesAdapter.updateData(movies);
        showMoviesView();
    }

    private void refresh(String query)
    {
        new DownloadMoviesDataAsync().execute(query);
    }

    private void showLoadingScreen(){
        moviesRecyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void showNetworkNotAvailableMessage() {
        moviesRecyclerView.setVisibility(View.INVISIBLE);
        loadingBar.setVisibility(View.INVISIBLE);
        errorMessage.setText(this.getResources().getString(R.string.network_not_available_message));
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void showMoviesView() {
        loadingBar.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        moviesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGridItemClick(int index) {
        Movie movie= moviesAdapter.getDatasetElement(index);
        Log.d("MainActivity", "Clicked on movie " + movie.title);
        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
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
