package com.example.udacity_project_1.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.udacity_project_1.popularmovies.utils.Movie;
import com.example.udacity_project_1.popularmovies.utils.MoviesAdapter;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener{

    private MoviesAdapter moviesAdapter;

    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;

    @BindView(R.id.pb_loading_bar) ProgressBar loadingBar;
    @BindView(R.id.tv_error_message) TextView errorMessage;

    private int calculateNumberOfColumns() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNumberOfColumns());
        moviesAdapter = new MoviesAdapter(this);

        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.setHasFixedSize(true);
        moviesRecyclerView.setAdapter(moviesAdapter);
        moviesRecyclerView.setVisibility(View.VISIBLE);

        refresh("popular");
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
        }
        else if (selection == R.id.action_top_rated){
            refresh("top_rated");
        }
        return super.onOptionsItemSelected(item);
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
