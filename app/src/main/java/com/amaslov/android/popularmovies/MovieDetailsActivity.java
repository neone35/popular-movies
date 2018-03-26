package com.amaslov.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amaslov.android.popularmovies.asynctasks.MovieDetailsTask;
import com.amaslov.android.popularmovies.asynctasks.MovieTrailersTask;
import com.amaslov.android.popularmovies.asynctasks.OnEventListener;
import com.amaslov.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.amaslov.android.popularmovies.fragments.YtPlayerFragment;
import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.utilities.MovieDBUrlUtils;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY;
    private static final String TAG = MovieDetailsActivity.class.getName();
    public static String movieId = "";
    ActivityMovieDetailsBinding activityMovieDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMovieDetailsBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        activityMovieDetailsBinding.ibDescription.setBackgroundResource(R.drawable.button_details_press); // initial option
        receiveMainActivityIntent();


        activityMovieDetailsBinding.ibDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOneButtonActive((ImageButton) v);
                setThisIncludeActive(activityMovieDetailsBinding.incMovieOverview);
            }
        });
        activityMovieDetailsBinding.ibTrailers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOneButtonActive((ImageButton) v);
                setThisIncludeActive(activityMovieDetailsBinding.incMovieTrailers);
                String movieTrailersUrl = MovieDBUrlUtils.getTrailersUrl(movieId);

                MovieTrailersTask movieTrailersTask =
                        new MovieTrailersTask(MovieDetailsActivity.this, new OnEventListener<String[]>() {
                            @Override
                            public void onSuccess(final String[] movieTrailerKeys) {
                                final LinearLayout llYoutubePlayerHolder = findViewById(R.id.yt_fragment_holder);
                                String[] youtubeThumbnailUrls = MovieDBUrlUtils.getYoutubeThumbnailUrls(movieTrailerKeys);
                                for (int i = 0; i < movieTrailerKeys.length; i++) {
                                    ImageView ivVideoThumb = new ImageView(MovieDetailsActivity.this);
                                    llYoutubePlayerHolder.addView(ivVideoThumb);
                                    ivVideoThumb.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                                    ivVideoThumb.getLayoutParams().height = (int) getResources().getDimension(R.dimen.details_main_height);
                                    Picasso.with(getApplicationContext())
                                            .load(youtubeThumbnailUrls[i])
                                            .into(ivVideoThumb);
                                    final int final_i = i;
                                    ivVideoThumb.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            llYoutubePlayerHolder.removeView(v);
                                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                            ft.replace(R.id.yt_fragment_holder,
                                                    YtPlayerFragment.newInstance(movieTrailerKeys[final_i]), String.valueOf(movieTrailerKeys[final_i]));
                                            ft.commit();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(MovieDetailsActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                movieTrailersTask.execute(movieTrailersUrl, null, null);


//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.add(R.id.yt_fragment_holder, YtPlayerFragment.newInstance("gNXQQbgK_cc"), "firstYTVideo");
//                ft.add(R.id.yt_fragment_holder, YtPlayerFragment.newInstance("4Eo84jDIMKI"), "secondYTVideo");
//                ft.commit();
            }
        });
    }

    private void setOneButtonActive(ImageButton thisButton) {
        // set all buttons inactive
        ConstraintLayout parentConstraint = activityMovieDetailsBinding.constraintLayout;
        int allChildCount = parentConstraint.getChildCount();
        for (int i = 0; i < allChildCount; i++) {
            if (parentConstraint.getChildAt(i) instanceof ImageButton) {
                parentConstraint.getChildAt(i).setBackgroundResource(R.drawable.button_details_inactive);
            }
        }
        // set selected button active
        thisButton.setBackgroundResource(R.drawable.button_details_press);
    }

    private void setThisIncludeActive(View v) {
        activityMovieDetailsBinding.incMovieOverview.setVisibility(View.INVISIBLE);
        activityMovieDetailsBinding.incMovieTrailers.setVisibility(View.INVISIBLE);
        v.setVisibility(View.VISIBLE);
    }

    private void receiveMainActivityIntent() {
        Intent intent = getIntent();
        movieId = intent.getStringExtra(MainActivity.EXTRA_MOVIE_ID);
        String movieFullUrl = intent.getStringExtra(MainActivity.EXTRA_MOVIE_FULL_URL);
        displayMovieDetails(movieId, movieFullUrl);
    }

    private void displayMovieDetails(String movieId, String movieFullUrl) {
        String[] idAndUrl = {movieId, movieFullUrl};
        MovieDetailsTask movieDetailsTask =
                new MovieDetailsTask(this, new OnEventListener<MovieDetails>() {
                    @Override
                    public void onSuccess(MovieDetails movieDetails) {
                        // Get data from returned Parcelable
                        String posterUrl = movieDetails.getMoviePosterUrl();
                        String title = movieDetails.getTitle();
                        String date = movieDetails.getMovieReleaseDate();
                        String voteAverage = movieDetails.getVoteAverage() + " " + getString(R.string.vote_average_out_of_ten);
                        String voteCount = movieDetails.getVoteCount();
                        String overview = movieDetails.getOverview();
                        // Calculate poster dimensions according to screen dpi programatically
                        int detailsPosterWidth =
                                (int) (getResources().getDimension(R.dimen.details_poster_width) /
                                        getResources().getDisplayMetrics().density);
                        int detailsPosterHeight =
                                (int) (getResources().getDimension(R.dimen.details_poster_height) /
                                        getResources().getDisplayMetrics().density);
                        Log.d(getLocalClassName(), "onSuccess: " + detailsPosterWidth + " " + detailsPosterHeight);
                        // Bind data to views
                        Picasso.with(MovieDetailsActivity.this)
                                .load(posterUrl)
                                .placeholder(R.drawable.poster_placeholder)
                                .centerCrop()
                                .resize(detailsPosterWidth, detailsPosterHeight)
                                .into(activityMovieDetailsBinding.ivPoster);
                        TextView movieTitle = activityMovieDetailsBinding.incMovieOverview.findViewById(R.id.tv_title);
                        movieTitle.setText(title);
                        TextView movieOverview = activityMovieDetailsBinding.incMovieOverview.findViewById(R.id.tv_overview);
                        movieOverview.setText(overview);
                        activityMovieDetailsBinding.tvDate.setText(date);
                        activityMovieDetailsBinding.tvAverageVoteAppend.setText(voteAverage);
                        activityMovieDetailsBinding.tvVoteCount.setText(voteCount);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MovieDetailsActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        movieDetailsTask.execute(idAndUrl, null, null);
    }
}
