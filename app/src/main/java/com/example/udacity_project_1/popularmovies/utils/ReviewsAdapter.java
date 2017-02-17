package com.example.udacity_project_1.popularmovies.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.udacity_project_1.popularmovies.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by hit on 17/02/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviews;

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView author;
        private TextView content;

        private ReviewViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_review_author);
            content = (TextView) itemView.findViewById(R.id.tv_review_content);
        }

        private void bind(int position) {
            Review review = reviews.get(position);
            author.setText(review.getAuthor());
            content.setText(review.getContent());
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
}
