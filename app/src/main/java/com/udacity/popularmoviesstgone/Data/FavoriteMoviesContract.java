package com.udacity.popularmoviesstgone.Data;

import android.provider.BaseColumns;

public class FavoriteMoviesContract {

    private FavoriteMoviesContract(){}

    public static final class FavoriteMoviesEntry implements BaseColumns{
        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";
    }
}
