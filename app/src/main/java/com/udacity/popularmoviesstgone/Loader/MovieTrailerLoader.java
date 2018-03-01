package com.udacity.popularmoviesstgone.Loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.udacity.popularmoviesstgone.Utils.NetworkUtils;

import java.net.URL;

import static com.udacity.popularmoviesstgone.MovieDetailsActivity.TRAILERS_URL_EXTRA;

/**
 * PopuralMoviesStage2 Created by aidan on 01/03/2018.
 */

public class MovieTrailerLoader {
    public static class LoadTrailers extends AsyncTaskLoader<String> {
        String mMoviewJson;
        Bundle queryURL;

        public LoadTrailers(Context context, Bundle queryURL) {
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
                String movieQueryUrlString = queryURL.getString(TRAILERS_URL_EXTRA);
                if (movieQueryUrlString == null || TextUtils.isEmpty(movieQueryUrlString)) {
                    return null;
                }
                URL url = new URL(movieQueryUrlString);
                return NetworkUtils.getResponseFromHttpUrl(url);
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
