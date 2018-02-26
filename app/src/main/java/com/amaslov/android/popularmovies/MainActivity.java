package com.amaslov.android.popularmovies;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

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
    private static final int GRID_SPAN_COUNT = 3;
    ActivityMainBinding mainBinding;
    private RecyclerView posterRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        actionBarSetup();
        recyclerViewSetup();
        radioGroupListenerSetup();
    }

    private void actionBarSetup() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    private void radioGroupListenerSetup() {
        displayMoviePosters(MovieDBUrlUtils.MOVIE_DB_PATH_POPULAR); //default
        mainBinding.popularTopRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Log.d(getLocalClassName(), "onCheckedChanged: " + checkedId);
                switch (checkedId) {
                    case R.id.rb_popular:
                        displayMoviePosters(MovieDBUrlUtils.MOVIE_DB_PATH_POPULAR);
                        break;
                    case R.id.rb_top_rated:
                        displayMoviePosters(MovieDBUrlUtils.MOVIE_DB_PATH_TOP_RATED);
                        break;
                    case R.id.rb_upcoming:
                        displayMoviePosters(MovieDBUrlUtils.MOVIE_DB_PATH_UPCOMING);
                        break;
                }
            }
        });
    }

    private void recyclerViewSetup() {
        posterRecyclerView = mainBinding.rvPosters;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        posterRecyclerView.setLayoutManager(gridLayoutManager);
        posterRecyclerView.setHasFixedSize(true);
    }

    private void displayMoviePosters(String sortBy) {
        String configUrl = MovieDBUrlUtils.getConfigUrl();
        String moviesUrl = MovieDBUrlUtils.getMoviesUrl(sortBy);
        String[] configAndUrls = {configUrl, moviesUrl};
        new movieInfoAsyncTask().execute(configAndUrls, null, null);
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

    @SuppressLint("StaticFieldLeak")
    private class movieInfoAsyncTask extends AsyncTask<String[], Void, MovieInfo> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Getting movies...");
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();
        }

        protected MovieInfo doInBackground(String[]... strings) {
            String[] urls = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request reqImageUrlConfig = new Request.Builder()
                    .url(urls[0])
                    .get()
                    .build();
            Request reqMoviePostersJPG = new Request.Builder()
                    .url(urls[1])
                    .get()
                    .build();
            try {
                Response resImageUrlConfig = client.newCall(reqImageUrlConfig).execute();
                Response resMoviePostersJPG = client.newCall(reqMoviePostersJPG).execute();

                String resConfigJSON = resImageUrlConfig.body().string();
                String resMoviesJSON = resMoviePostersJPG.body().string();
                String imageUrlConfig = MovieDBJsonUtils
                        .getImageUrl(resConfigJSON);
                String[] moviePosterJPGs = MovieDBJsonUtils
                        .getMovieValues(resMoviesJSON, MovieDBJsonUtils.MOVIE_DB_POSTER_PATH);
                String[] movieIDs = MovieDBJsonUtils
                        .getMovieValues(resMoviesJSON, MovieDBJsonUtils.MOVIE_DB_ID);
                String[] fullImageUrls = new String[moviePosterJPGs.length];
                for (int i = 0; i < fullImageUrls.length; i++) {
                    fullImageUrls[i] = imageUrlConfig + moviePosterJPGs[i];
                }
                return new MovieInfo(fullImageUrls, movieIDs);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(MovieInfo moviesInfo) {
            dialog.dismiss();
            MoviePosterAdapter moviePosterAdapter = new MoviePosterAdapter(MainActivity.this, moviesInfo);
            posterRecyclerView.setAdapter(moviePosterAdapter);
            moviePosterAdapter.notifyDataSetChanged();
        }
    }
}
