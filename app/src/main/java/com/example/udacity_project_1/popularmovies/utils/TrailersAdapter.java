package com.example.udacity_project_1.popularmovies.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.udacity_project_1.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by hit on 17/02/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> trailers;

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView name;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_trailer_name);
        }

        public void bind(int position) {
            name.setText(trailers.get(position).getName());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.v("TrailersAdapter", "Clicked on Trailer: " + trailers.get(position).getName());
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        }
        return trailers.size();
    }

    public void updateDataSet(ArrayList<Trailer> nTrailers) {
        trailers = nTrailers;
        notifyDataSetChanged();
    }
}
