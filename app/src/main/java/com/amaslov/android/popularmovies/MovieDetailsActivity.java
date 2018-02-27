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
import android.widget.Toast;

import com.amaslov.android.popularmovies.asynctasks.MovieDetailsTask;
import com.amaslov.android.popularmovies.asynctasks.MovieInfoTask;
import com.amaslov.android.popularmovies.asynctasks.OnEventListener;
import com.amaslov.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.parcelables.MovieInfo;
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
        MovieDetailsTask movieDetailsTask = new MovieDetailsTask(this, new OnEventListener<MovieDetails>() {
            @Override
            public void onSuccess(MovieDetails movieDetails) {
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

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        movieDetailsTask.execute(idAndUrl, null, null);
    }
}
