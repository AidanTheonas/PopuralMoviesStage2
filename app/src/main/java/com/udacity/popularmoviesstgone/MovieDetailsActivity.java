package com.udacity.popularmoviesstgone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmoviesstgone.Adapter.MovieReviewsAdapter;
import com.udacity.popularmoviesstgone.Adapter.MovieTrailersAdapter;
import com.udacity.popularmoviesstgone.Data.FavoriteMoviesContentProvider;
import com.udacity.popularmoviesstgone.Data.FavoriteMoviesContract;
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
import butterknife.OnClick;

import static com.udacity.popularmoviesstgone.MainActivity.MOVIE_DETAILS_EXTRA;
import static com.udacity.popularmoviesstgone.MainActivity.POSTER_PATH_BASE_URL;

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
    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @BindView(R.id.iv_poster)
    ImageView ivMoviePoster;
    @BindView(R.id.tv_movie_title)
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
    @BindView(R.id.btn_favorite)
    Button btnFavorite;

    Movies movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        }

        Intent intent = getIntent();
        if (intent.getExtras() == null) {
            Toast.makeText(this,"Sorry! An Error occurred",Toast.LENGTH_LONG).show();
        } else {
            movies = intent.getParcelableExtra(MOVIE_DETAILS_EXTRA);
            checkIfFavorite();
            String posterUrl = movies.getMoviePoster();
            if (!URLUtil.isValidUrl(movies.getMoviePoster())) {
                posterUrl = POSTER_PATH_BASE_URL.concat(posterUrl);
            }
            Picasso.with(this)
                    .load(posterUrl)
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
                if(movieTrailersAdapter.getItemCount() > 0) tvTrailersTitle.setVisibility(View.VISIBLE);
                if(movieReviewsAdapter.getItemCount() > 0) tvReviewsTitle.setVisibility(View.VISIBLE);
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

    private void checkIfFavorite() {
        Uri uri = FavoriteMoviesContentProvider.CONTENT_URI.buildUpon().appendPath(movies.getMovieID()).build();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) return;
        if (cursor.getCount() > 0) {
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_favorite, 0, 0, 0);
            btnFavorite.setTag(1);
        }
        cursor.close();
    }

    private String shareMovieString(String movieName,String movieID){
        String watchThisMovie = getResources().getString(R.string.watch_this_movie);
        String preInfo = watchThisMovie.concat("-").concat(movieName).concat("\n");
        String shareUrl = preInfo.concat("https://www.themoviedb.org/movie/").concat(movieID);
        if (movieTrailers.get(0) != null) {
            shareUrl = "http://www.youtube.com/watch?v=".concat(movieTrailers.get(0).getTrailerID().trim());
        }

        return shareUrl;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.btn_favorite)
    void toggleFavorite(){
        ContentResolver contentResolver = getContentResolver();

        switch (btnFavorite.getTag().toString().trim()){
            case "0":
                Uri result = contentResolver.insert(FavoriteMoviesContentProvider.CONTENT_URI, getContenValues());

                if (result != null) {
                    updateButtonStyle(R.drawable.ic_star_favorite, 1);

                } else {
                    Toast.makeText(this, R.string.error_fav_movie_not_added, Toast.LENGTH_SHORT).show();
                }
                break;

            case "1":
                Uri uri = FavoriteMoviesContentProvider.CONTENT_URI.buildUpon().appendPath(movies.getMovieID()).build();
                if (contentResolver.delete(uri, null, null) > 0) {
                    updateButtonStyle(R.drawable.ic_star_not_favorite, 0);
                } else {
                    Toast.makeText(this, R.string.error_fav_movie_not_removed, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public ContentValues getContenValues() {
        ContentValues values = new ContentValues();
        values.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movies.getMovieID());
        values.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, movies.getMovieTitle());
        values.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE, movies.getReleaseDate());
        values.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER, movies.getMoviePoster());
        values.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE, movies.getVoteAverage());
        values.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_PLOT_SYNOPSIS, movies.getPlotSynopsis());

        return values;
    }

    public void updateButtonStyle(int drawable, int tag) {
        btnFavorite.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        btnFavorite.setTag(tag);
        if (tag == 1) {
            Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_movie:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        shareMovieString(movies.getMovieTitle(),movies.getMovieID()));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
        }
        return (super.onOptionsItemSelected(item));
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
            if (movieTrailers.size() > 0) tvTrailersTitle.setVisibility(View.VISIBLE);
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
            if (movieReviews.size() > 0) tvReviewsTitle.setVisibility(View.VISIBLE);
            movieReviewsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
