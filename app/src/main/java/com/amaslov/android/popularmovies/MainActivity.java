package com.amaslov.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.RadioGroup;

import com.amaslov.android.popularmovies.adapters.MovieFavoritesAdapter;
import com.amaslov.android.popularmovies.adapters.MoviePosterAdapter;
import com.amaslov.android.popularmovies.asynctasks.MovieInfoTask;
import com.amaslov.android.popularmovies.asynctasks.OnEventListener;
import com.amaslov.android.popularmovies.databinding.ActivityMainBinding;
import com.amaslov.android.popularmovies.parcelables.MovieInfo;
import com.amaslov.android.popularmovies.utilities.SqlUtils;
import com.amaslov.android.popularmovies.utilities.UrlUtils;

import static com.amaslov.android.popularmovies.utilities.SqlUtils.getFavoritesTable;


public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.ListItemClickListener {

    public static final String EXTRA_MOVIE_ID = "movie_id";
    public static final String EXTRA_MOVIE_FULL_URL = "movie_poster_full_url";
    private static final String TAG = MainActivity.class.getName();
    private static final String KEY_RV_POSITION_STATE = "rv_position";
    private static final String KEY_BOTTOM_NAV_SELECTION = "bottom_nav_selection";
    private static final String KEY_SORT_BY = "favorites";
    private ActivityMainBinding mainBinding;
    private RecyclerView posterRecyclerView;
    private Snackbar noInternetSnack = null;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        actionBarSetup();
        recyclerViewSetup();
        // get last selected tab and set content & bottom nav item
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.getString(KEY_SORT_BY, "favorites").equals("favorites")) {
            displayFavorites();
        } else {
            displayMoviePosters(sharedPref.getString(KEY_SORT_BY, "favorites")); //default - favorites
        }
        mainBinding.bnvMain.setSelectedItemId(sharedPref.getInt(KEY_BOTTOM_NAV_SELECTION, -1));
        // create snack for use in further check
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
            bottomNavListenerSetup();
        } else {
            noInternetSnack.show();
            if (posterRecyclerView.getAdapter() != null)
                posterRecyclerView.setAdapter(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_RV_POSITION_STATE, mGridLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedGridLayoutManager = savedInstanceState.getParcelable(KEY_RV_POSITION_STATE);
            if (savedGridLayoutManager != null) {
                mGridLayoutManager.onRestoreInstanceState(savedGridLayoutManager);
            }
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

    private void bottomNavListenerSetup() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bnv_main);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        mainBinding.bnvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_favorites:
                        displayFavorites();
                        editor.putString(KEY_SORT_BY, getString(R.string.favorites_string));
                        editor.putInt(KEY_BOTTOM_NAV_SELECTION, R.id.action_favorites);
                        editor.apply();
                        return true;
                    case R.id.action_popular:
                        displayMoviePosters(UrlUtils.MOVIE_DB_PATH_POPULAR);
                        editor.putInt(KEY_BOTTOM_NAV_SELECTION, R.id.action_popular);
                        editor.apply();
                        return true;
                    case R.id.action_top_rated:
                        displayMoviePosters(UrlUtils.MOVIE_DB_PATH_TOP_RATED);
                        editor.putInt(KEY_BOTTOM_NAV_SELECTION, R.id.action_top_rated);
                        editor.apply();
                        return true;
                    case R.id.action_upcoming:
                        displayMoviePosters(UrlUtils.MOVIE_DB_PATH_UPCOMING);
                        editor.putInt(KEY_BOTTOM_NAV_SELECTION, R.id.action_upcoming);
                        editor.apply();
                        return true;
                }
                return false;
            }
        });
    }

    private void displayFavorites() {
        Cursor favoritesDbCursor = SqlUtils.getFavoritesTable(MainActivity.this);
        MovieFavoritesAdapter movieFavoritesAdapter = new MovieFavoritesAdapter(favoritesDbCursor, MainActivity.this);
        movieFavoritesAdapter.swapCursor(SqlUtils.getFavoritesTable(MainActivity.this));
        posterRecyclerView.setAdapter(movieFavoritesAdapter);
    }

    private void recyclerViewSetup() {
        posterRecyclerView = mainBinding.rvPosters;
        mGridLayoutManager = new GridLayoutManager(this, numberOfColumns());
        posterRecyclerView.setLayoutManager(mGridLayoutManager);
        posterRecyclerView.setHasFixedSize(true);
    }

    private int numberOfColumns() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 300;
        int width = metrics.widthPixels;
        Log.d(TAG, "numberOfColumns: " + width);
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void displayMoviePosters(String sortBy) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_SORT_BY, sortBy);
        editor.apply();

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
        movieDetailsIntent.putExtra(EXTRA_MOVIE_ID, movieId); // 18028
        movieDetailsIntent.putExtra(EXTRA_MOVIE_FULL_URL, movieFullUrl); // poster image url
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
