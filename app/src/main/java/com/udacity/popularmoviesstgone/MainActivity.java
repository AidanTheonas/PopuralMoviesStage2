package com.udacity.popularmoviesstgone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import com.udacity.popularmoviesstgone.Loader.PopularMoviesLoader;
import com.udacity.popularmoviesstgone.Model.Movies;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.popularmoviesstgone.Utils.JSONUtils.parseMovieJson;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    List<Movies> postersList;
    public static final String POPURAL_MOVIES_URL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    public static final String TOP_RATED_MOVIES_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=";
    public static final String POSTER_PATH_BASE_URL = "http://image.tmdb.org/t/p/w780/";
    public static final int POPULAR_MOVIES_LOADER = 1;
    public static final String MOVIE_URL_EXTRA = "movie_url";
    public static final String RESULTS_KEY = "results";
    public static final String MOVIE_DETAILS_EXTRA = "movie_details";
    String currentURL = "";
    GridView gridview;
    MoviePostersAdapter moviePostersAdapter;
    ProgressBar progressBar;
    TextView tvErrorMessage;
    Button btnRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridview = findViewById(R.id.gv_movies);
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
        moviePostersAdapter = new MoviePostersAdapter(this,postersList);
        gridview.setAdapter(moviePostersAdapter);

        progressBar = findViewById(R.id.pb_loading);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        tvErrorMessage.setText(getResources().getString(R.string.error_occured));
        btnRefresh = findViewById(R.id.btn_refresh);
        loadMovies(POPURAL_MOVIES_URL.concat(getResources().getString(R.string.movies_db_key)));
    }

    public void reloadMovieList(View view){
        loadMovies(currentURL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_pupular:
                loadMovies(POPURAL_MOVIES_URL.concat(getResources().getString(R.string.movies_db_key)));
                return true;
            case R.id.highest_rated:
                loadMovies(TOP_RATED_MOVIES_URL.concat(getResources().getString(R.string.movies_db_key)));
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new PopularMoviesLoader.LoadMovies(getBaseContext(),args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        progressBar.setVisibility(View.GONE);
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
    public void onLoaderReset(Loader<String> loader) {}
}
