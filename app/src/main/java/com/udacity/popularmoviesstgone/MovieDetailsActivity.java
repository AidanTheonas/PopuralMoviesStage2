package com.udacity.popularmoviesstgone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmoviesstgone.Model.Movies;

import static com.udacity.popularmoviesstgone.MainActivity.MOVIE_DETAILS_EXTRA;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this,"Sorry! An Error occurred",Toast.LENGTH_LONG).show();
        } else {
            Movies movies = (Movies) intent.getSerializableExtra(MOVIE_DETAILS_EXTRA);
            ImageView ivMoviePoster = findViewById(R.id.iv_poster);
            Picasso.with(this)
                    .load(movies.getMoviePoster())
                    .into(ivMoviePoster);
            TextView tvTitle = findViewById(R.id.tv_title);
            tvTitle.setText(movies.getMovieTitle());
            setTitle(movies.getMovieTitle());
            TextView tvReleaseDate = findViewById(R.id.tv_release_date);
            tvReleaseDate.setText(movies.getReleaseDate());
            TextView tvVoteAverage = findViewById(R.id.tv_vote_average);
            tvVoteAverage.setText(movies.getVoteAverage());
            TextView tvSynopsis = findViewById(R.id.tv_synopsis);
            tvSynopsis.setText(movies.getPlotSynopsis());
        }
    }
}
