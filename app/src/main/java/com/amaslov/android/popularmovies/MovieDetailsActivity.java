package com.amaslov.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amaslov.android.popularmovies.asynctasks.MovieDetailsTask;
import com.amaslov.android.popularmovies.asynctasks.OnEventListener;
import com.amaslov.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    ActivityMovieDetailsBinding activityMovieDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMovieDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        receiveMainActivityIntent();
        setDescriptionButtonActive();
    }

    private void setDescriptionButtonActive() {
        activityMovieDetailsBinding.ibDescription.setBackgroundResource(R.drawable.button_details_press);
    }

    private void receiveMainActivityIntent() {
        Intent intent = getIntent();
        String movieId = intent.getStringExtra(MainActivity.EXTRA_MOVIE_ID);
        String movieFullUrl = intent.getStringExtra(MainActivity.EXTRA_MOVIE_FULL_URL);
        displayMovieDetails(movieId, movieFullUrl);
    }

    private void displayMovieDetails(String movieId, String movieFullUrl) {
        String[] idAndUrl = {movieId, movieFullUrl};
        MovieDetailsTask movieDetailsTask =
                new MovieDetailsTask(this, new OnEventListener<MovieDetails>() {
            @Override
            public void onSuccess(MovieDetails movieDetails) {
                String posterUrl = movieDetails.getMoviePosterUrl();
                String title = movieDetails.getTitle();
                String date = movieDetails.getMovieReleaseDate();
                String voteAverage = movieDetails.getVoteAverage() + " " + getString(R.string.vote_average_out_of_ten);
                String voteCount = movieDetails.getVoteCount();
                String overview = movieDetails.getOverview();
                int detailsPosterWidth =
                        (int) (getResources().getDimension(R.dimen.details_poster_width) /
                                getResources().getDisplayMetrics().density);
                int detailsPosterHeight =
                        (int) (getResources().getDimension(R.dimen.details_poster_height) /
                                getResources().getDisplayMetrics().density);
                Log.d(getLocalClassName(), "onSuccess: " + detailsPosterWidth + " " + detailsPosterHeight);
                Picasso.with(MovieDetailsActivity.this)
                        .load(posterUrl)
                        .placeholder(R.drawable.poster_placeholder)
                        .centerCrop()
                        .resize(detailsPosterWidth, detailsPosterHeight)
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
