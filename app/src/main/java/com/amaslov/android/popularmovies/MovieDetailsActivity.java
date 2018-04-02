package com.amaslov.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amaslov.android.popularmovies.adapters.MovieReviewsAdapter;
import com.amaslov.android.popularmovies.adapters.MovieTrailersAdapter;
import com.amaslov.android.popularmovies.asynctasks.MovieDetailsTask;
import com.amaslov.android.popularmovies.asynctasks.MovieReviewsTask;
import com.amaslov.android.popularmovies.asynctasks.MovieTrailersTask;
import com.amaslov.android.popularmovies.asynctasks.OnEventListener;
import com.amaslov.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.amaslov.android.popularmovies.fragments.YtPlayerFragment;
import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.parcelables.MovieReviewInfo;
import com.amaslov.android.popularmovies.parcelables.MovieTrailerInfo;
import com.amaslov.android.popularmovies.sqlitedb.FavoritesContract;
import com.amaslov.android.popularmovies.sqlitedb.FavoritesDbHelper;
import com.amaslov.android.popularmovies.utilities.UrlUtils;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class MovieDetailsActivity extends AppCompatActivity implements MovieTrailersAdapter.ListItemClickListener {

    private static final String TAG = MovieDetailsActivity.class.getName();
    final private static String YOUTUBE_FRAGMENT_TAG = "youtube_player_fragment";
    private static String movieId = "";
    private static String movieFullUrl = "";
    private MovieDetails mMovieDetails;
    private RecyclerView trailerRecyclerView;
    private RecyclerView reviewsRecyclerView;
    private ActivityMovieDetailsBinding activityMovieDetailsBinding;
    private SQLiteDatabase mFavoritesDB;

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
                // load new trailers thumbnail list
                trailerRecyclerViewSetup();
                loadTrailerThumbnailsTask(movieId);
            }
        });
        activityMovieDetailsBinding.ibReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOneButtonActive((ImageButton) v);
                setThisIncludeActive(activityMovieDetailsBinding.incMovieReviews);
                reviewsRecyclerViewSetup();
                loadReviewsTask(movieId);
            }
        });
        activityMovieDetailsBinding.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.button_details_press);
                if (addToFavorites(mMovieDetails)) {
                    showToast("'" + mMovieDetails.getTitle() + "' successfully added to favorites");
                    Log.d(TAG, "onClick: " + getDetailsTable().getCount());
                }
            }
        });
    }

    private Cursor getDetailsTable() {
        String favoritesTableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
        String columnTitle = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE;
        FavoritesDbHelper favoritesDbHelper = new FavoritesDbHelper(this);
        mFavoritesDB = favoritesDbHelper.getReadableDatabase();
        return mFavoritesDB.query(favoritesTableName, null, null, null, null, null, columnTitle);
    }

    public void showToast(String message) {
        Toast.makeText(MovieDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public boolean addToFavorites(MovieDetails movieDetails) {
        // Check if movie details fields are not empty
        String FAVORITES_ERROR = "Can't add to favorites.";
        String[] detailsFields = movieDetails.getAllDetails();
        String[] fieldErrors = {"Poster URL is empty. ", "Movie title is empty. ", "Movie release date is empty. ",
                "Movie vote average is empty. ", "Movie vote count is empty. ", "Movie overview is empty. "};
        for (int i = 0; i < detailsFields.length; i++) {
            if (detailsFields[i].isEmpty()) {
                showToast(fieldErrors[i] + FAVORITES_ERROR);
                return false;
            }
        }

        // Insert into favorites table otherwise
        String favoritesTableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
        ContentValues detailsValues = new ContentValues();
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_FULL_URL, movieDetails.getMoviePosterUrl());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE, movieDetails.getTitle());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE, movieDetails.getMovieReleaseDate());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE, movieDetails.getVoteAverage());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_COUNT, movieDetails.getVoteCount());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_OVERVIEW, movieDetails.getOverview());
        FavoritesDbHelper favoritesDbHelper = new FavoritesDbHelper(this);
        mFavoritesDB = favoritesDbHelper.getWritableDatabase();
        mFavoritesDB.insert(favoritesTableName, null, detailsValues);
        return true;
    }

    @Override
    public void onBackPressed() {
        // If YT player exists, load thumbnails again
        Fragment ytFrag = getSupportFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG);
        if (ytFrag != null) {
            activityMovieDetailsBinding.ibTrailers.performClick();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onListItemClick(String clickedTrailerKey) {
        // hide thumbnails recylerview on trailer choose
        RecyclerView rvTrailers = activityMovieDetailsBinding.incMovieTrailers.findViewById(R.id.rv_trailers);
        rvTrailers.setVisibility(View.GONE);
        // load clicked thumbnail into framelayout as youtube player
        FrameLayout flTrailerFragment = activityMovieDetailsBinding.incMovieTrailers.findViewById(R.id.fl_trailer_fragment_holder);
        flTrailerFragment.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_trailer_fragment_holder, YtPlayerFragment.newInstance(clickedTrailerKey), YOUTUBE_FRAGMENT_TAG);
        ft.commit();
    }

    private void loadReviewsTask(String movieId) {
        MovieReviewsTask movieReviewsTask =
                new MovieReviewsTask(MovieDetailsActivity.this, new OnEventListener<MovieReviewInfo>() {
                    @Override
                    public void onSuccess(final MovieReviewInfo movieReviewInfo) {
                        if (movieReviewInfo.getReviewInfoLength() != 0) {
                            // Set new reviews list
                            MovieReviewsAdapter movieReviewsAdapter = new MovieReviewsAdapter(movieReviewInfo);
                            reviewsRecyclerView.setAdapter(movieReviewsAdapter);
                            movieReviewsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MovieDetailsActivity.this, getString(R.string.no_reviews_found), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MovieDetailsActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        movieReviewsTask.execute(movieId, null, null);
    }

    private void loadTrailerThumbnailsTask(String movieId) {
        MovieTrailersTask movieTrailersTask =
                new MovieTrailersTask(MovieDetailsActivity.this, new OnEventListener<MovieTrailerInfo>() {
                    @Override
                    public void onSuccess(final MovieTrailerInfo movieTrailerInfo) {
                        if (movieTrailerInfo.getTrailerInfoLength() != 0) {
                            // remove last loaded trailer player fragment
                            Fragment ytFragment = getSupportFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG);
                            if (ytFragment != null)
                                getSupportFragmentManager().beginTransaction().remove(ytFragment).commit();
                            // Set new trailer thumbnail list
                            MovieTrailersAdapter movieTrailersAdapter = new MovieTrailersAdapter(MovieDetailsActivity.this, movieTrailerInfo);
                            trailerRecyclerView.setAdapter(movieTrailersAdapter);
                            movieTrailersAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MovieDetailsActivity.this, getString(R.string.no_trailers_found), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MovieDetailsActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        movieTrailersTask.execute(movieId, null, null);
    }

    private void trailerRecyclerViewSetup() {
        // load trailers thumbnail recyclerview
        int rvID = R.id.rv_trailers;
        View incView = activityMovieDetailsBinding.incMovieTrailers;
        RecyclerView rv = incView.findViewById(rvID);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        trailerRecyclerView = incView.findViewById(rvID);
        // hide trailer player frameLayout
        trailerRecyclerView.setVisibility(View.VISIBLE);
        FrameLayout flTrailerFragment = activityMovieDetailsBinding.incMovieTrailers.findViewById(R.id.fl_trailer_fragment_holder);
        flTrailerFragment.setVisibility(View.GONE);
    }

    private void reviewsRecyclerViewSetup() {
        int rvID = R.id.rv_reviews;
        View incView = activityMovieDetailsBinding.incMovieReviews;
        RecyclerView rv = incView.findViewById(rvID);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(false);
        reviewsRecyclerView = incView.findViewById(rvID);
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
        activityMovieDetailsBinding.incMovieOverview.setVisibility(View.GONE);
        activityMovieDetailsBinding.incMovieTrailers.setVisibility(View.GONE);
        activityMovieDetailsBinding.incMovieReviews.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
    }

    private void receiveMainActivityIntent() {
        Intent intent = getIntent();
        movieId = intent.getStringExtra(MainActivity.EXTRA_MOVIE_ID);
        movieFullUrl = intent.getStringExtra(MainActivity.EXTRA_MOVIE_FULL_URL);
        displayMovieDetailsTask(movieId, movieFullUrl);
    }

    private void displayMovieDetailsTask(String movieId, String movieFullUrl) {
        MovieDetailsTask movieDetailsTask =
                new MovieDetailsTask(this, new OnEventListener<MovieDetails>() {
                    @Override
                    public void onSuccess(MovieDetails movieDetails) {
                        // Get data from returned Parcelable
                        mMovieDetails = movieDetails;
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
        movieDetailsTask.execute(movieId, movieFullUrl, null);
    }
}
