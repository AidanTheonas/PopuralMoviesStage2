package com.udacity.popularmoviesstgone.Utils;

import com.udacity.popularmoviesstgone.Model.Movies;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.popularmoviesstgone.MainActivity.POSTER_PATH_BASE_URL;

/*
 * Created by Aidan on 2/26/2018.
 */

public class JSONUtils {
    private static final String MOVIE_TITLE = "title";
    private static final String RELEASE_DATE = "release_date";
    private static final String MOVIE_POSTER = "poster_path";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String PLOT_SYNOPSIS = "overview";
    public static List<Movies> parseMovieJson(String json){
        List<Movies> moviesList = new ArrayList<>();
        try {
            if (json == null || json.trim().equals("")){
                return null;
            }
            JSONArray jsonMoviesArray = new JSONArray(json);
            for(int i=0;i<jsonMoviesArray.length();i++){
                JSONObject jsonObject = new JSONObject(jsonMoviesArray.get(i).toString());
                Movies movies = new Movies(
                        jsonObject.optString(MOVIE_TITLE),
                        jsonObject.optString(RELEASE_DATE),
                        POSTER_PATH_BASE_URL.concat(jsonObject.optString(MOVIE_POSTER)),
                        jsonObject.optString(VOTE_AVERAGE),
                        jsonObject.optString(PLOT_SYNOPSIS)
                );
                moviesList.add(movies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moviesList;
    }
}
