package com.example.udacity_project_1.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.udacity_project_1.popularmovies.utils.DataFetcher;
import com.example.udacity_project_1.popularmovies.utils.MoviesAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private MoviesAdapter moviesAdapter;
    private RecyclerView moviesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        moviesAdapter = new MoviesAdapter();

        moviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.setHasFixedSize(true);
        moviesRecyclerView.setAdapter(moviesAdapter);
        moviesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selection = item.getItemId();
        if (selection == R.id.action_refresh){
            new DownloadMoviesDataAsync().execute();

        }
        return super.onOptionsItemSelected(item);
    }




    private class DownloadMoviesDataAsync extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            Log.d("DownloadMoviesDataAsync", "Starting Async task");
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            return DataFetcher.getPopularMovies();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Log.v("MainActivity", "Got data: " + jsonArray.toString());
            moviesAdapter.updateData(jsonArray);
            Log.d("DownloadMoviesDataAsync", "Finished Async task");

        }
    }

}
