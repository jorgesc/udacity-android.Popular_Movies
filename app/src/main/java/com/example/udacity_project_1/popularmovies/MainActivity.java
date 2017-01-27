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




    private class DownloadMoviesDataAsync extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            Log.d("DownloadMoviesDataAsync", "Starting Async task");
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            JSONArray output = null;

            switch (params[0]){
                case "popular": output = DataFetcher.getPopularMovies();
                    break;
                case "top_rated": output = DataFetcher.getTopRatedMovies();
                    break;
            }
            return output;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Log.v("MainActivity", "Got data: " + jsonArray.toString());
            moviesAdapter.updateData(jsonArray);
            Log.d("DownloadMoviesDataAsync", "Finished Async task");

        }
    }

}
