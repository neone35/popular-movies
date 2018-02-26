package com.amaslov.android.popularmovies;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amaslov.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.utilities.MovieDBJsonUtils;
import com.amaslov.android.popularmovies.utilities.MovieDBUrlUtils;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    ActivityMovieDetailsBinding activityMovieDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMovieDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        receiveMainActivityIntent();
    }

    private void receiveMainActivityIntent() {
        Intent intent = getIntent();
        String movieId = intent.getStringExtra(MainActivity.EXTRA_MOVIE_ID);
        String movieFullUrl = intent.getStringExtra(MainActivity.EXTRA_MOVIE_FULL_URL);
        displayMovieDetails(movieId, movieFullUrl);
    }

    private void displayMovieDetails(String movieId, String movieFullUrl) {
        String[] idAndUrl = {movieId, movieFullUrl};
        new movieDetailsAsyncTask().execute(idAndUrl, null, null);
    }

    @SuppressLint("StaticFieldLeak")
    private class movieDetailsAsyncTask extends AsyncTask<String[], Void, MovieDetails> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MovieDetailsActivity.this);
            dialog.setTitle("Getting movie details...");
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();
        }

        @Override
        protected MovieDetails doInBackground(String[]... strings) {
            String[] idAndUrl = strings[0];
            String movieDetailsUrl = MovieDBUrlUtils.getDetailsUrl(idAndUrl[0]);
            String moviePosterFullUrl = idAndUrl[1];
            OkHttpClient client = new OkHttpClient();
            Request reqMovieDetails = new Request.Builder()
                    .url(movieDetailsUrl)
                    .get()
                    .build();
            try {
                Response resMovieDetails = client.newCall(reqMovieDetails).execute();
                String resDetailsJSON = resMovieDetails.body().string();
                return MovieDBJsonUtils
                        .getMovieDetails(resDetailsJSON, moviePosterFullUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(MovieDetails movieDetails) {
            dialog.dismiss();
            String posterUrl = movieDetails.getMoviePosterUrl();
            String title = movieDetails.getTitle();
            String date = movieDetails.getMovieReleaseDate();
            String voteAverage = movieDetails.getVoteAverage() + " / 10";
            String voteCount = movieDetails.getVoteCount();
            String overview = movieDetails.getOverview();
            Picasso.with(MovieDetailsActivity.this)
                    .load(posterUrl)
                    .placeholder(R.drawable.poster_placeholder)
                    .centerCrop()
                    .resize(200, 300)
                    .into(activityMovieDetailsBinding.ivPoster);
            activityMovieDetailsBinding.tvTitle.setText(title);
            activityMovieDetailsBinding.tvDate.setText(date);
            activityMovieDetailsBinding.tvAverageVoteAppend.setText(voteAverage);
            activityMovieDetailsBinding.tvVoteCount.setText(voteCount);
            activityMovieDetailsBinding.tvOverview.setText(overview);
        }
    }
}
