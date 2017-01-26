package com.example.udacity_project_1.popularmovies.utils;

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.udacity_project_1.popularmovies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.Inflater;

/**
 * Created by hit on 26/01/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private JSONArray dataset;

    class MovieViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        Context context;

        public MovieViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_movie_item);
            context = itemView.getContext();

        }

        public void bind(String imgSource) {
            Picasso.with(context).load(imgSource).into(imageView);
        }

    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position){
        try {
            JSONObject movie = (JSONObject) dataset.get(position);
            String imageSource = movie.getString("movie_poster");
            holder.bind(imageSource);
        }
        catch (JSONException e){
            Log.e("MoviesAdapter", "exception", e);
        }
    }

    @Override
    public int getItemCount() {
        if (dataset == null) {return 0;}
        return dataset.length();
    }

    public void updateData(JSONArray data) {
        dataset = data;
        notifyDataSetChanged();
        Log.d("MoviesAdapter", "dataset changed, new length: " + dataset.length());
    }
}
