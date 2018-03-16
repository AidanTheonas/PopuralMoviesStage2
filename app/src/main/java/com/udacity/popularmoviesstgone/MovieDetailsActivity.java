package com.udacity.popularmoviesstgone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmoviesstgone.Adapter.MovieReviewsAdapter;
import com.udacity.popularmoviesstgone.Adapter.MovieTrailersAdapter;
import com.udacity.popularmoviesstgone.Loader.MovieReviewsLoader;
import com.udacity.popularmoviesstgone.Loader.MovieTrailerLoader;
import com.udacity.popularmoviesstgone.Model.MovieReviews;
import com.udacity.popularmoviesstgone.Model.MovieTrailers;
import com.udacity.popularmoviesstgone.Model.Movies;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.popularmoviesstgone.MainActivity.MOVIE_DETAILS_EXTRA;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    public static final int MOVIE_TRAILERS_LOADER = 2;
    public static final int MOVIE_REVIEWS_LOADER = 3;
    public static final String TRAILERS_URL_EXTRA = "trailers_url";
    public static final String REVIEWS_URL_EXTRA = "reviews_url";
    public static final String RESULTS_KEY = "results";

    private static final String TRAILER_ID = "key";
    private static final String TRAILER_DESCRIPTION = "name";

    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_COMMENTS = "content";
    private static final String REVIEW_URL = "url";
    private static final String MOVIE_REVIEWS_STATE = "movie_reviews_state";
    private static final String MOVIE_TRAILERS_STATE = "movie_trailers_state";
    public List<MovieTrailers> movieTrailers;
    public List<MovieReviews> movieReviews;
    @BindView(R.id.iv_poster)
    ImageView ivMoviePoster;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.tv_vote_average)
    TextView tvVoteAverage;
    @BindView(R.id.tv_synopsis)
    TextView tvSynopsis;
    @BindView(R.id.tv_trailers_title)
    TextView tvTrailersTitle;
    @BindView(R.id.tv_reviews_title)
    TextView tvReviewsTitle;
    @BindView(R.id.rv_movies)
    RecyclerView moviesTrailerRecyclerView;
    MovieTrailersAdapter movieTrailersAdapter;
    @BindView(R.id.rv_reviews)
    RecyclerView moviesReviewRecyclerView;
    MovieReviewsAdapter movieReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.getExtras() == null) {
            Toast.makeText(this,"Sorry! An Error occurred",Toast.LENGTH_LONG).show();
        } else {
            Movies movies = intent.getParcelableExtra(MOVIE_DETAILS_EXTRA);
            Picasso.with(this)
                    .load(movies.getMoviePoster())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.image_error)
                    .into(ivMoviePoster);
            tvTitle.setText(movies.getMovieTitle());
            tvReleaseDate.setText(movies.getReleaseDate());
            tvVoteAverage.setText(movies.getVoteAverage().concat("/10"));
            tvSynopsis.setText(movies.getPlotSynopsis());

            if(savedInstanceState != null){
                movieTrailersAdapter = savedInstanceState.getParcelable(MOVIE_TRAILERS_STATE);
                movieReviewsAdapter = savedInstanceState.getParcelable(MOVIE_REVIEWS_STATE);
            }else{
                movieTrailers = new ArrayList<>();
                movieTrailersAdapter = new MovieTrailersAdapter(movieTrailers, this);
                movieReviews = new ArrayList<>();
                movieReviewsAdapter = new MovieReviewsAdapter(movieReviews);
            }

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            moviesTrailerRecyclerView.setLayoutManager(mLayoutManager);
            moviesTrailerRecyclerView.setItemAnimator(new DefaultItemAnimator());
            moviesTrailerRecyclerView.setAdapter(movieTrailersAdapter);
            moviesTrailerRecyclerView.setNestedScrollingEnabled(false);

            RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(getApplicationContext());
            moviesReviewRecyclerView.setLayoutManager(rLayoutManager);
            moviesReviewRecyclerView.setItemAnimator(new DefaultItemAnimator());
            moviesReviewRecyclerView.setAdapter(movieReviewsAdapter);
            moviesReviewRecyclerView.setNestedScrollingEnabled(false);

            if(savedInstanceState == null){
                loadMovieTrailers(movies.getMovieID());
                loadReviews(movies.getMovieID());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_REVIEWS_STATE,movieReviewsAdapter);
        outState.putParcelable(MOVIE_TRAILERS_STATE,movieTrailersAdapter);
    }

    public String movieTrailerURL(String id) {
        String key = getResources().getString(R.string.movies_db_key);
        return "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=".concat(key);
    }

    public String movieReviewsURL(String id) {
        String key = getResources().getString(R.string.movies_db_key);
        return "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=".concat(key);
    }

    public void loadMovieTrailers(String id) {
        String url = movieTrailerURL(id);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> moviesLoader = loaderManager.getLoader(MOVIE_TRAILERS_LOADER);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(TRAILERS_URL_EXTRA, url);
        if (moviesLoader == null) {
            loaderManager.initLoader(MOVIE_TRAILERS_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_TRAILERS_LOADER, queryBundle, this);
        }
    }

    public void loadReviews(String id) {
        String url = movieReviewsURL(id);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> moviesLoader = loaderManager.getLoader(MOVIE_REVIEWS_LOADER);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(REVIEWS_URL_EXTRA, url);
        if (moviesLoader == null) {
            loaderManager.initLoader(MOVIE_REVIEWS_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_REVIEWS_LOADER, queryBundle, this);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        if (id == MOVIE_TRAILERS_LOADER) {
            return new MovieTrailerLoader.LoadTrailers(this, args);
        } else {
            return new MovieReviewsLoader.LoadReviews(this, args);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (!(null == data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (jsonObject.has(RESULTS_KEY)) {
                    switch (loader.getId()) {
                        case MOVIE_REVIEWS_LOADER:
                            parseReviewsJson(jsonObject.optString(RESULTS_KEY));
                            break;

                        case MOVIE_TRAILERS_LOADER:
                            parseTrailerJson(jsonObject.optString(RESULTS_KEY));
                            break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }

    public void parseTrailerJson(String json) {
        Log.e("JSON:", json);
        try {
            if (json == null || json.trim().equals("")) {
                return;
            }
            JSONArray jsonMoviesArray = new JSONArray(json);
            for (int i = 0; i < jsonMoviesArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonMoviesArray.get(i).toString());
                MovieTrailers trailer = new MovieTrailers(
                        jsonObject.optString(TRAILER_ID),
                        jsonObject.optString(TRAILER_DESCRIPTION)
                );
                movieTrailers.add(trailer);
            }
            if (movieTrailers.size() == 0) tvTrailersTitle.setVisibility(View.GONE);
            movieTrailersAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseReviewsJson(String json) {
        try {
            if (json == null || json.trim().equals("")) {
                return;
            }
            JSONArray jsonMoviesArray = new JSONArray(json);
            for (int i = 0; i < jsonMoviesArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonMoviesArray.get(i).toString());
                MovieReviews reviews = new MovieReviews(
                        jsonObject.optString(REVIEW_AUTHOR),
                        jsonObject.optString(REVIEW_COMMENTS),
                        jsonObject.optString(REVIEW_URL)

                );
                movieReviews.add(reviews);
            }
            if (movieReviews.size() == 0) tvReviewsTitle.setVisibility(View.GONE);
            movieReviewsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
