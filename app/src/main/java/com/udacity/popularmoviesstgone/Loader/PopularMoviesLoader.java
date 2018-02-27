package com.udacity.popularmoviesstgone.Loader;

/*
 * Created by Aidan on 2/26/2018.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.udacity.popularmoviesstgone.Utils.NetworkUtils;

import java.net.URL;

import static com.udacity.popularmoviesstgone.MainActivity.MOVIE_URL_EXTRA;

public class PopularMoviesLoader {
    public static class LoadMovies extends AsyncTaskLoader<String> {
        String mMoviewJson;
        Bundle queryURL;

        public LoadMovies(Context context, Bundle queryURL) {
            super(context);
            this.queryURL = queryURL;
        }

        @Override
        protected void onStartLoading() {
            if (queryURL == null) {
                cancelLoad();
                return;
            }

            if (mMoviewJson != null) {
                deliverResult(mMoviewJson);
            } else {
                forceLoad();
            }
        }

        @Override
        public String loadInBackground() {
            try {
                String movieQueryUrlString = queryURL.getString(MOVIE_URL_EXTRA);
                if (movieQueryUrlString == null || TextUtils.isEmpty(movieQueryUrlString)) {
                    return null;
                }
                URL githubUrl = new URL(movieQueryUrlString);
                return NetworkUtils.getResponseFromHttpUrl(githubUrl);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void deliverResult(String orgJson) {
            mMoviewJson = orgJson;
            super.deliverResult(orgJson);
        }
    }
}
