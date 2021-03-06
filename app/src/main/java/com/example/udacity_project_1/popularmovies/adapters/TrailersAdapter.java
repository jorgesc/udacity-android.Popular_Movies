package com.example.udacity_project_1.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.udacity_project_1.popularmovies.R;
import com.example.udacity_project_1.popularmovies.objects.Trailer;

import java.util.ArrayList;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> trailers;
    private Context context;

    public TrailersAdapter (Context nContext) {
        context = nContext;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView playIcon;
        private ImageView shareIcon;

        private Uri youtubeUrl;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            playIcon = (ImageView) itemView.findViewById(R.id.iv_play_trailer_icon);
            shareIcon = (ImageView) itemView.findViewById(R.id.iv_share_trailer_icon);

            playIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("TrailersAdapter", "Clicked on play icon");
                    if (youtubeUrl != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUrl);
                        context.startActivity(intent);
                    }
                }
            });

            shareIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("TrailersAdapter", "Clicked on share icon");
                    if (youtubeUrl != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, youtubeUrl.toString());
                        intent.setType("text/plain");
                        context.startActivity(Intent.createChooser(intent, "Share using: "));
                    }
                }
            });

            // itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Trailer trailer = trailers.get(position);
            name.setText(trailer.getName());
            youtubeUrl = Uri.parse(trailer.getUrl().toString());
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

    public ArrayList<Trailer> getDataset() {
        return trailers;
    }
}
