package com.udacity.popularmoviesstgone;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.popularmoviesstgone.Adapter.MoviePostersAdapter;
import com.udacity.popularmoviesstgone.Data.FavoriteMoviesContentProvider;
import com.udacity.popularmoviesstgone.Data.FavoriteMoviesContract;
import com.udacity.popularmoviesstgone.Loader.PopularMoviesLoader;
import com.udacity.popularmoviesstgone.Model.Movies;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.popularmoviesstgone.Utils.Helpers.PreferenceHelpers.getMovieCategory;
import static com.udacity.popularmoviesstgone.Utils.Helpers.PreferenceHelpers.setMovieCategory;
import static com.udacity.popularmoviesstgone.Utils.JSONUtils.parseMovieJson;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    public static final String POPURAL_MOVIES_URL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    public static final String TOP_RATED_MOVIES_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=";
    public static final String POSTER_PATH_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    public static final int POPULAR_MOVIES_LOADER = 1;
    public static final String MOVIE_URL_EXTRA = "movie_url";
    public static final String RESULTS_KEY = "results";
    public static final String MOVIE_DETAILS_EXTRA = "movie_details";
    public static final String MOVIE_POSTER_ADAPTER_STATE = "movie_poster_adapter";
    List<Movies> postersList;
    String currentURL = "";

    @BindView(R.id.gv_movies)
    GridView gridview;
    MoviePostersAdapter moviePostersAdapter;
    @BindView(R.id.pb_loading)
    ProgressBar progressBar;
    @BindView(R.id.tv_error_message)
    TextView tvErrorMessage;
    @BindView(R.id.btn_refresh)
    Button btnRefresh;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshMovieList;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        refreshMovieList.setDistanceToTriggerSync(100);
        refreshMovieList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLastUserMovieCategoryChoice();
            }
        });

        refreshMovieList.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorDarkAccent,
                R.color.colorPrimaryDark,
                R.color.colorAccent);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movies movies = postersList.get(position);
                if (movies != null){
                    Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                    intent.putExtra(MOVIE_DETAILS_EXTRA, movies);
                    startActivity(intent);
                }
            }
        });
        postersList = new ArrayList<>();
        if(savedInstanceState != null) {
            moviePostersAdapter = savedInstanceState.getParcelable(MOVIE_POSTER_ADAPTER_STATE);
        }else{
            moviePostersAdapter = new MoviePostersAdapter(this, postersList);
        }
        gridview.setAdapter(moviePostersAdapter);

        tvErrorMessage.setText(getResources().getString(R.string.error_occured));
    }

    public void reloadMovieList(View view){
        loadMovies(currentURL);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_POSTER_ADAPTER_STATE,moviePostersAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() != R.id.refresh_list){
            item.setChecked(true);
            setMovieCategory(this,item.getItemId());
        }
        switch (item.getItemId()) {
            case R.id.favorites:
                FavoriteMoviesFetchTask favoriteMoviesFetchTask = new FavoriteMoviesFetchTask();
                favoriteMoviesFetchTask.execute();
                updateTitle(getResources().getString(R.string.favorite_movies));
                return true;

            case R.id.most_popular:
                loadMovies(POPURAL_MOVIES_URL.concat(getResources().getString(R.string.movies_db_key)));
                updateTitle(getResources().getString(R.string.most_popular_movies));
                return true;
            case R.id.highest_rated:
                loadMovies(TOP_RATED_MOVIES_URL.concat(getResources().getString(R.string.movies_db_key)));
                updateTitle(getResources().getString(R.string.highest_rated_movies));
                return true;

            case R.id.refresh_list:
                refreshMovieList.setRefreshing(true);
                loadLastUserMovieCategoryChoice();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    public void updateTitle(String title){
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        loadLastUserMovieCategoryChoice();
        return true;
    }

    public void loadLastUserMovieCategoryChoice(){
        MenuItem menuItem = menu.findItem(getMovieCategory(this,R.id.most_popular));
        if (menuItem != null) {
            menuItem.setChecked(true);
            onOptionsItemSelected(menuItem);
        } else {
            loadMovies(POPURAL_MOVIES_URL.concat(getResources().getString(R.string.movies_db_key)));
            menuItem = menu.findItem(R.id.most_popular);
            menuItem.setChecked(true);
        }
    }

    public void loadMovies(String url){
        progressBar.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.GONE);
        btnRefresh.setVisibility(View.GONE);
        currentURL = url;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> moviesLoader = loaderManager.getLoader(POPULAR_MOVIES_LOADER);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_URL_EXTRA, url);
        if (moviesLoader == null) {
            loaderManager.initLoader(POPULAR_MOVIES_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(POPULAR_MOVIES_LOADER, queryBundle, this);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new PopularMoviesLoader.LoadMovies(getBaseContext(),args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        progressBar.setVisibility(View.GONE);
        tvErrorMessage.setText(R.string.error_occured);
        if(refreshMovieList.isRefreshing()){
            refreshMovieList.setRefreshing(false);
        }
        if (!(null == data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(jsonObject.has(RESULTS_KEY)){
                    postersList = parseMovieJson(jsonObject.optString(RESULTS_KEY));
                    if(postersList == null) {
                        tvErrorMessage.setVisibility(View.VISIBLE);
                        btnRefresh.setVisibility(View.VISIBLE);
                    }else{
                        moviePostersAdapter = new MoviePostersAdapter(this,postersList);
                        gridview.setAdapter(moviePostersAdapter);
                    }
                }else {
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    btnRefresh.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                tvErrorMessage.setVisibility(View.VISIBLE);
                btnRefresh.setVisibility(View.VISIBLE);
            }
        }else{
            tvErrorMessage.setVisibility(View.VISIBLE);
            btnRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {}

    @SuppressLint("StaticFieldLeak")
    public class FavoriteMoviesFetchTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            ContentResolver contentResolver = getContentResolver();
            return contentResolver.query(FavoriteMoviesContentProvider.CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if (refreshMovieList.isRefreshing()) {
                refreshMovieList.setRefreshing(false);
            }
            progressBar.setVisibility(View.GONE);
            List<Movies> moviesList = new ArrayList<>();
            while (cursor.moveToNext()) {
                Movies movies = new Movies(
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE)),
                        POSTER_PATH_BASE_URL.concat(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER))),
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE)),
                        cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_PLOT_SYNOPSIS))
                );
                moviesList.add(movies);
            }

            if (!moviesList.isEmpty()) {
                postersList = moviesList;
                moviePostersAdapter = new MoviePostersAdapter(getBaseContext(), postersList);
                gridview.setAdapter(moviePostersAdapter);
            } else {
                gridview.setAdapter(null);
                tvErrorMessage.setText(R.string.no_favorite_movies);
                tvErrorMessage.setVisibility(View.VISIBLE);
            }
            cursor.close();
        }
    }
}
