package com.example.udacity_project_1.popularmovies.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.udacity_project_1.popularmovies.R;

import java.util.ArrayList;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviews;

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView author;
        private TextView content;

        private ReviewViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_review_author);
            content = (TextView) itemView.findViewById(R.id.tv_review_content);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            Review review = reviews.get(position);
            author.setText(review.getAuthor());
            content.setText(review.getContent());
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.v("ReviewsAdapter", "Clicked on Review: " + reviews.get(position).getAuthor());
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        }
        return reviews.size();
    }

    public void updateDataSet(ArrayList<Review> nReviews) {
        reviews = nReviews;
        notifyDataSetChanged();
    }

    public ArrayList<Review> getDataset() {
        return reviews;
    }
}
