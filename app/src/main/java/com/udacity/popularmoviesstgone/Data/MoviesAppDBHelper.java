package com.udacity.popularmoviesstgone.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesAppDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popular_movies.db";
    private static final int DATABASE_VERSION = 3;

    MoviesAppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE =
                "CREATE TABLE " +
                        FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME + " ("
                        + FavoriteMoviesContract.FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL," +
                        FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                        FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                        FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL," +
                        FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE + " FLOAT NOT NULL," +
                        FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                        ");";
        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
