package com.udacity.popularmoviesstgone.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.udacity.popularmoviesstgone.Model.MovieTrailers;
import com.udacity.popularmoviesstgone.R;

import java.util.List;

/**
 * PopuralMoviesStage2 Created by aidan on 01/03/2018.
 */

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.ViewHolder> implements View.OnClickListener,Parcelable {

    private List<MovieTrailers> movieTrailers;
    private Context context;

    public MovieTrailersAdapter(List<MovieTrailers> movieTrailers, Context context) {
        this.movieTrailers = movieTrailers;
        this.context = context;
    }

    private MovieTrailersAdapter(Parcel in) {
        movieTrailers = in.createTypedArrayList(MovieTrailers.CREATOR);
    }

    public static final Creator<MovieTrailersAdapter> CREATOR = new Creator<MovieTrailersAdapter>() {
        @Override
        public MovieTrailersAdapter createFromParcel(Parcel in) {
            return new MovieTrailersAdapter(in);
        }

        @Override
        public MovieTrailersAdapter[] newArray(int size) {
            return new MovieTrailersAdapter[size];
        }
    };

    @Override
    public void onClick(View v) {
        String id = v.getTag().toString();
        playTrailer(id);
    }

    @NonNull
    @Override
    public MovieTrailersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_trailers_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailersAdapter.ViewHolder holder, int position) {
        MovieTrailers movieT = movieTrailers.get(position);
        holder.title.setText(movieT.getTrailerDescription());
        holder.title.setTag(movieT.getTrailerID());
        holder.playTrailer.setTag(movieT.getTrailerID());
        holder.playTrailer.setOnClickListener(this);
        holder.title.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return movieTrailers.size();
    }

    private void playTrailer(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(movieTrailers);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        ImageButton playTrailer;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_trailer_title);
            playTrailer = view.findViewById(R.id.btn_play);
        }
    }
}
