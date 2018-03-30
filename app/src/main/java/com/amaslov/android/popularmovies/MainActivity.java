package com.amaslov.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.amaslov.android.popularmovies.adapters.MoviePosterAdapter;
import com.amaslov.android.popularmovies.asynctasks.MovieInfoTask;
import com.amaslov.android.popularmovies.asynctasks.OnEventListener;
import com.amaslov.android.popularmovies.databinding.ActivityMainBinding;
import com.amaslov.android.popularmovies.parcelables.MovieInfo;
import com.amaslov.android.popularmovies.utilities.UrlUtils;


public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.ListItemClickListener {

    public static final String EXTRA_MOVIE_ID = "movie_id";
    public static final String EXTRA_MOVIE_FULL_URL = "movie_poster_full_url";
    private static final String TAG = MainActivity.class.getName();
    ActivityMainBinding mainBinding;
    private RecyclerView posterRecyclerView;
    private Snackbar noInternetSnack = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        actionBarSetup();
        recyclerViewSetup();
        displayMoviePosters(UrlUtils.MOVIE_DB_PATH_POPULAR); //default

        noInternetSnack = Snackbar.make(mainBinding.mainConstrainLayout,
                getString(R.string.no_internet_msg), Snackbar.LENGTH_LONG);
        noInternetSnack.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetSnack.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isOnline()) {
            noInternetSnack.dismiss();
            radioGroupListenerSetup();
        } else {
            noInternetSnack.show();
            if (posterRecyclerView.getAdapter() != null)
                posterRecyclerView.setAdapter(null);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void actionBarSetup() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
    }

    private void radioGroupListenerSetup() {
        mainBinding.popularTopRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_popular:
                        displayMoviePosters(UrlUtils.MOVIE_DB_PATH_POPULAR);
                        break;
                    case R.id.rb_top_rated:
                        displayMoviePosters(UrlUtils.MOVIE_DB_PATH_TOP_RATED);
                        break;
                    case R.id.rb_upcoming:
                        displayMoviePosters(UrlUtils.MOVIE_DB_PATH_UPCOMING);
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
        String configUrl = UrlUtils.getConfigUrl();
        String moviesUrl = UrlUtils.getMoviesUrl(sortBy);

        MovieInfoTask movieInfoTask = new MovieInfoTask(this, new OnEventListener<MovieInfo>() {
            @Override
            public void onSuccess(MovieInfo moviesInfo) {
                MoviePosterAdapter moviePosterAdapter = new MoviePosterAdapter(MainActivity.this, moviesInfo);
                posterRecyclerView.setAdapter(moviePosterAdapter);
                moviePosterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                noInternetSnack.show();
            }
        });
        movieInfoTask.execute(configUrl, moviesUrl, null);
    }

    @Override
    public void onListItemClick(String movieId, String movieFullUrl) {
        Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        Log.d(getLocalClassName(), "onListItemClick: " + movieId);
        movieDetailsIntent.putExtra(EXTRA_MOVIE_ID, movieId);
        movieDetailsIntent.putExtra(EXTRA_MOVIE_FULL_URL, movieFullUrl);
        startActivity(movieDetailsIntent);
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
