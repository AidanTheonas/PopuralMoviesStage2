package com.udacity.popularmoviesstgone.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * PopuralMoviesStage2 Created by aidan on 16/03/2018.
 */

public class Helpers {
    static final String PREFERENCE_NAME = "movies_app";
    static final String MOVIE_CATEGORY_PREFERENCE = "movie_category";
    public static class PreferenceHelpers{
        public static void setMovieCategory(Context context,int categoryResource){
            SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit();
            editor.putInt(MOVIE_CATEGORY_PREFERENCE, categoryResource);
            editor.apply();
        }

        public static int getMovieCategory(Context context,int defaultCategory){
            SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
           return prefs.getInt(MOVIE_CATEGORY_PREFERENCE, defaultCategory);
        }

    }
}
