package com.udacity.popularmoviesstgone.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

public class FavoriteMoviesContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.udacity.popularmoviesstgone.FavoriteMoviesProvider";
    static final String URL = "content://".concat(PROVIDER_NAME).concat("/favoriteMovies");
    public static final Uri CONTENT_URI = Uri.parse(URL);

    static final int MOVIES = 1;
    static final int MOVIE_ID = 2;
    static final UriMatcher uriMatcher;
    @SuppressWarnings("unused")
    private static HashMap<String, String> FAVORITE_MOVIES_PROJECTION_MAP;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "favoriteMovies", MOVIES);
        uriMatcher.addURI(PROVIDER_NAME, "favoriteMovies/#", MOVIE_ID);
    }

    SQLiteDatabase sqLiteDatabase;

    public FavoriteMoviesContentProvider() {
    }

    @Override
    public boolean onCreate() {
        MoviesAppDBHelper moviesAppDBHelper = new MoviesAppDBHelper(getContext());
        sqLiteDatabase = moviesAppDBHelper.getWritableDatabase();
        return sqLiteDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                queryBuilder.setProjectionMap(FAVORITE_MOVIES_PROJECTION_MAP);
                break;

            case MOVIE_ID:
                queryBuilder.appendWhere(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = " + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder.trim().equals("")) {
            sortOrder = FavoriteMoviesContract.FavoriteMoviesEntry._ID;
        }

        Cursor cursor = queryBuilder.query(sqLiteDatabase, projection, selection,
                selectionArgs, null, null, sortOrder);
        if (getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {

            case MOVIES:
                return "vnd.android.cursor.dir/vnd.example.favoritemovies";

            case MOVIE_ID:
                return "vnd.android.cursor.item/vnd.example.favoritemovies";
            default:
                throw new IllegalArgumentException("Unsupported URI: ".concat(uri.toString()));
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = sqLiteDatabase.insert(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            if (getContext() != null)
                getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add record".concat(uri.toString()));
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                count = sqLiteDatabase.delete(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                count = sqLiteDatabase.delete(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME, FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI ".concat(uri.toString()));
        }

        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int count;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                count = sqLiteDatabase.update(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case MOVIE_ID:
                count = sqLiteDatabase.update(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME, values,
                        FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI ".concat(uri.toString()));
        }

        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
