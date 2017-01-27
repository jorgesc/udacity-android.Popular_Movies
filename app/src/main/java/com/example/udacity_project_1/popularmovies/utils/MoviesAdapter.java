package com.example.udacity_project_1.popularmovies.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.udacity_project_1.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private ArrayList<com.example.udacity_project_1.popularmovies.utils.Movie> dataset;
    private GridItemClickListener clickListener;

    public MoviesAdapter(GridItemClickListener listener) {
        clickListener = listener;
    }

    public interface GridItemClickListener {
        void onGridItemClick(int index);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        Context context;

        private MovieViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_movie_item);
            context = itemView.getContext();
            itemView.setOnClickListener(this);

        }

        private void bind(String imgSource) {
            Picasso.with(context)
                    .load(imgSource)
                    .error(R.mipmap.img_movie_poster_placeholder)
                    .into(imageView);
       }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onGridItemClick(position);
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
        com.example.udacity_project_1.popularmovies.utils.Movie movie = dataset.get(position);
        String imageSource = movie.poster;
        holder.bind(imageSource);
    }

    @Override
    public int getItemCount() {
        if (dataset == null) {return 0;}
        return dataset.size();
    }

    public void updateData(ArrayList<com.example.udacity_project_1.popularmovies.utils.Movie> data) {
        dataset = data;
        notifyDataSetChanged();
        Log.d("MoviesAdapter", "dataset changed, new length: " + dataset.size());
    }

    public com.example.udacity_project_1.popularmovies.utils.Movie getDatasetElement(int index) {
        return dataset.get(index);
    }
}
