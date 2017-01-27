package com.example.udacity_project_1.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.udacity_project_1.popularmovies.utils.DataFetcher;
import com.example.udacity_project_1.popularmovies.utils.Movie;
import com.example.udacity_project_1.popularmovies.utils.MoviesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener{

    private MoviesAdapter moviesAdapter;
    private RecyclerView moviesRecyclerView;

    private ProgressBar loadingBar;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingBar = (ProgressBar) findViewById(R.id.pb_loading_bar);
        errorMessage = (TextView) findViewById(R.id.tv_error_message);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        moviesAdapter = new MoviesAdapter(this);

        moviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
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
