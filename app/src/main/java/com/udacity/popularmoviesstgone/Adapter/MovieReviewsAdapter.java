package com.udacity.popularmoviesstgone.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmoviesstgone.Model.MovieReviews;
import com.udacity.popularmoviesstgone.R;

import java.util.List;

/**
 * PopuralMoviesStage2 Created by aidan on 01/03/2018.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    private List<MovieReviews> movieReviews;

    public MovieReviewsAdapter(List<MovieReviews> movieReviews) {
        this.movieReviews = movieReviews;
    }

    @NonNull
    @Override
    public MovieReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_reviews_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsAdapter.ViewHolder holder, int position) {
        MovieReviews movieR = movieReviews.get(position);
        holder.author.setText(movieR.getReviewAuthor());
        holder.source.setText(movieR.getReviewUrl());
        holder.comments.setText(movieR.getReviewComments());
    }

    @Override
    public int getItemCount() {
        return movieReviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView author, source, comments;

        ViewHolder(View view) {
            super(view);
            author = view.findViewById(R.id.tv_author);
            source = view.findViewById(R.id.tv_source);
            comments = view.findViewById(R.id.tv_comments);
        }
    }

}
