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
import android.support.v7.widget.Toolbar;

import com.amaslov.android.popularmovies.databinding.ActivityMainBinding;
import com.amaslov.android.popularmovies.utilities.MovieDBJsonUtils;
import com.amaslov.android.popularmovies.utilities.MovieDBUrlUtils;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements CustomAdapter.ListItemClickListener {

    public static final String EXTRA_ITEM_INDEX = "item_index";
    private static final int GRID_SPAN_COUNT = 3;
    ActivityMainBinding mainBinding;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        toolbarWidgetSetup();
        recyclerViewSetup();
        radioGroupListenerSetup();
    }

    private void toolbarWidgetSetup() {
        Toolbar moviesToolbar = (Toolbar) findViewById(R.id.moviesToolbar);
        setSupportActionBar(moviesToolbar);
    }

    private void radioGroupListenerSetup() {
        displayPopularMoviePosters();
        mainBinding.popularTopRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Log.d(getLocalClassName(), "onCheckedChanged: " + checkedId);
                switch (checkedId) {
                    case R.id.rb_popular:
                        displayPopularMoviePosters();
                        break;
                    case R.id.rb_top_rated:
                        displayTopRatedMoviePosters();
                        break;
                    case R.id.rb_upcoming:
                        displayUpcomingMoviePosters();
                        break;
                }
            }
        });
    }

    private void recyclerViewSetup() {
        mRecyclerView = mainBinding.rvPosters;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

    }

    private void displayPopularMoviePosters() {
        String configUrl = MovieDBUrlUtils.getConfigUrl();
        String moviesUrl = MovieDBUrlUtils.getMoviesUrl(MovieDBUrlUtils.MOVIE_DB_PATH_POPULAR);
        String[] configAndUrls = {configUrl, moviesUrl};
        new mAsyncTask().execute(configAndUrls, null, null);
    }

    private void displayTopRatedMoviePosters() {
        String configUrl = MovieDBUrlUtils.getConfigUrl();
        String moviesUrl = MovieDBUrlUtils.getMoviesUrl(MovieDBUrlUtils.MOVIE_DB_PATH_TOP_RATED);
        String[] configAndUrls = {configUrl, moviesUrl};
        new mAsyncTask().execute(configAndUrls, null, null);
    }

    private void displayUpcomingMoviePosters() {
        String configUrl = MovieDBUrlUtils.getConfigUrl();
        String moviesUrl = MovieDBUrlUtils.getMoviesUrl(MovieDBUrlUtils.MOVIE_DB_PATH_UPCOMING);
        String[] configAndUrls = {configUrl, moviesUrl};
        new mAsyncTask().execute(configAndUrls, null, null);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        String itemIndex = String.valueOf(clickedItemIndex);
        Log.d(getLocalClassName(), "onListItemClick: " + itemIndex);
        intent.putExtra(EXTRA_ITEM_INDEX, itemIndex);
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
    private class mAsyncTask extends AsyncTask<String[], Void, String[]> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Getting movies...");
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();
        }

        protected String[] doInBackground(String[]... strings) {
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
            Log.d("doInBackground", "doInBackground: " + urls[0] + " " + urls[1]);
            try {
                Response resImageUrlConfig = client.newCall(reqImageUrlConfig).execute();
                Response resMoviePostersJPG = client.newCall(reqMoviePostersJPG).execute();

                String resConfigJSON = resImageUrlConfig.body().string();
                String resMoviesJSON = resMoviePostersJPG.body().string();
                String imageUrlConfig = MovieDBJsonUtils
                        .getImageUrl(resConfigJSON);
                String[] moviePosterJPGs = MovieDBJsonUtils
                        .getMoviePosterJPGs(resMoviesJSON);
                String[] fullImageUrls = new String[moviePosterJPGs.length];
                for (int i = 0; i < fullImageUrls.length; i++) {
                    fullImageUrls[i] = imageUrlConfig + moviePosterJPGs[i];
                }
                return fullImageUrls;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String[] result) {
            dialog.dismiss();
//            for (int i=0; i<result.length; i++) {
//                Log.d(TAG, "onPostExecute: " + result[i]);
//            }
//            Picasso.with(MainActivity.this)
//                    .load(result[5])
//                    .into(mainBinding.ivTestPoster);

            CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, result);
            mRecyclerView.setAdapter(customAdapter);
            customAdapter.notifyDataSetChanged();
        }
    }
}
