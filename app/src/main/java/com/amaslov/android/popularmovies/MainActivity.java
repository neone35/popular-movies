package com.amaslov.android.popularmovies;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amaslov.android.popularmovies.asynctasks.MovieInfoTask;
import com.amaslov.android.popularmovies.asynctasks.OnEventListener;
import com.amaslov.android.popularmovies.databinding.ActivityMainBinding;
import com.amaslov.android.popularmovies.parcelables.MovieInfo;
import com.amaslov.android.popularmovies.utilities.MovieDBJsonUtils;
import com.amaslov.android.popularmovies.utilities.MovieDBUrlUtils;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.ListItemClickListener {

    public static final String EXTRA_MOVIE_ID = "movie_id";
    public static final String EXTRA_MOVIE_FULL_URL = "movie_poster_full_url";
    private static final int GRID_SPAN_COUNT_PORTRAIT = 3;
    private static final int GRID_SPAN_COUNT_LANDSCAPE = 5;
    ActivityMainBinding mainBinding;
    private RecyclerView posterRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        actionBarSetup();
        Toast noInternetToast = Toast.makeText(
                this,
                "You have no internet connection",
                Toast.LENGTH_LONG);
        radioGroupListenerSetup(noInternetToast);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void actionBarSetup() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


    private void radioGroupListenerSetup(final Toast noInternetToast) {
        if (isOnline()) {
            displayMoviePosters(MovieDBUrlUtils.MOVIE_DB_PATH_POPULAR); //default
        } else {
            noInternetToast.show();
        }
        mainBinding.popularTopRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_popular:
                        if (!isOnline()) {
                            noInternetToast.show();
                            break;
                        }
                        displayMoviePosters(MovieDBUrlUtils.MOVIE_DB_PATH_POPULAR);
                        break;
                    case R.id.rb_top_rated:
                        if (!isOnline()) {
                            noInternetToast.show();
                            break;
                        }
                        displayMoviePosters(MovieDBUrlUtils.MOVIE_DB_PATH_TOP_RATED);
                        break;
                    case R.id.rb_upcoming:
                        if (!isOnline()) {
                            noInternetToast.show();
                            break;
                        }
                        displayMoviePosters(MovieDBUrlUtils.MOVIE_DB_PATH_UPCOMING);
                        break;
                }
            }
        });
    }

    private void recyclerViewSetup() {
        posterRecyclerView = mainBinding.rvPosters;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns());
        posterRecyclerView.setLayoutManager(gridLayoutManager);
        posterRecyclerView.setHasFixedSize(true);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 300;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void displayMoviePosters(String sortBy) {
        recyclerViewSetup();

        String configUrl = MovieDBUrlUtils.getConfigUrl();
        String moviesUrl = MovieDBUrlUtils.getMoviesUrl(sortBy);
        String[] configAndUrls = {configUrl, moviesUrl};

        MovieInfoTask movieInfoTask = new MovieInfoTask(this, new OnEventListener<MovieInfo>() {
            @Override
            public void onSuccess(MovieInfo moviesInfo) {
                MoviePosterAdapter moviePosterAdapter = new MoviePosterAdapter(MainActivity.this, moviesInfo);
                posterRecyclerView.setAdapter(moviePosterAdapter);
                moviePosterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        movieInfoTask.execute(configAndUrls, null, null);
    }

    @Override
    public void onListItemClick(String movieId, String movieFullUrl) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        Log.d(getLocalClassName(), "onListItemClick: " + movieId);
        intent.putExtra(EXTRA_MOVIE_ID, movieId);
        intent.putExtra(EXTRA_MOVIE_FULL_URL, movieFullUrl);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
//                displayPopularMoviePosters();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
