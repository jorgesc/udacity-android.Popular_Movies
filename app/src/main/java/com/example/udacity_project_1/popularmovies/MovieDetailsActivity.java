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

import com.example.udacity_project_1.popularmovies.utils.Adapters.ReviewsAdapter;
import com.example.udacity_project_1.popularmovies.utils.Adapters.TrailersAdapter;
import com.example.udacity_project_1.popularmovies.utils.DataFetcher;
import com.example.udacity_project_1.popularmovies.utils.DatabaseUtils.FavoriteContentProviderContract;
import com.example.udacity_project_1.popularmovies.utils.DatabaseUtils.FavoriteMoviesDbContract;
import com.example.udacity_project_1.popularmovies.utils.EmptyRecyclerView;
import com.example.udacity_project_1.popularmovies.utils.Objects.Movie;
import com.example.udacity_project_1.popularmovies.utils.Objects.MovieExtra;
import com.example.udacity_project_1.popularmovies.utils.Objects.Review;
import com.example.udacity_project_1.popularmovies.utils.Objects.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

    }

}
