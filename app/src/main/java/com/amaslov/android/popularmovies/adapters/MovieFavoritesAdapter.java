package com.amaslov.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amaslov.android.popularmovies.MainActivity;
import com.amaslov.android.popularmovies.MovieDetailsActivity;
import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.sqlitedb.FavoritesContract;
import com.squareup.picasso.Picasso;

public class MovieFavoritesAdapter extends RecyclerView.Adapter<MovieFavoritesAdapter.ViewHolder> {

    private static final String TAG = "MovieFavoritesAdapter";
    private Cursor mDbCursor;
    private Context mContext;

    public MovieFavoritesAdapter(Cursor cursor, Context context) {
        mDbCursor = cursor;
        mContext = context;
    }

    public void swapCursor(Cursor cursor) {
        if (mDbCursor == null)
            return;
        mDbCursor = cursor;
        if (cursor != null)
            this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDbCursor.getCount();
    }

    @NonNull
    @Override
    public MovieFavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_poster_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieFavoritesAdapter.ViewHolder viewHolder, int position) {
        if (!mDbCursor.moveToPosition(position))
            return;

        ImageView ivContext = viewHolder.getImageView();
        int mainPosterWidth =
                (int) (ivContext.getResources().getDimension(R.dimen.main_poster_width) /
                        ivContext.getResources().getDisplayMetrics().density);
        int mainPosterHeight =
                (int) (ivContext.getResources().getDimension(R.dimen.main_poster_height) /
                        ivContext.getResources().getDisplayMetrics().density);
        String columnPosterUrl = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_FULL_URL;
        String moviePosterUrl = mDbCursor.getString(mDbCursor.getColumnIndex(columnPosterUrl));
        Picasso.with(viewHolder.posterImageView.getContext())
                .load(moviePosterUrl)
                .placeholder(R.drawable.poster_placeholder)
                .centerCrop()
                .resize(mainPosterWidth, mainPosterHeight)
                .into(viewHolder.getImageView());
    }

    public interface ListItemClickListener {
        void onListItemClick(String movieId, String movieFullUrl);
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView posterImageView;

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            posterImageView = view.findViewById(R.id.posterImageView);
        }

        private ImageView getImageView() {
            return posterImageView;
        }

        @Override
        public void onClick(View view) {
            int p = getAdapterPosition();
            mDbCursor.moveToPosition(p);

            String columnMovieID = FavoritesContract.FavoritesEntry._ID;
            String movieID = mDbCursor.getString(mDbCursor.getColumnIndex(columnMovieID));
            String columnFullUrl = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_FULL_URL;
            String moviePosterUrl = mDbCursor.getString(mDbCursor.getColumnIndex(columnFullUrl));
            String columnMovieTitle = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE;
            String movieTitle = mDbCursor.getString(mDbCursor.getColumnIndex(columnMovieTitle));
            String columnMovieReleaseDate = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE;
            String movieReleaseDate = mDbCursor.getString(mDbCursor.getColumnIndex(columnMovieReleaseDate));
            String columnMovieVoteAverage = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE;
            String movieVoteAverage = mDbCursor.getString(mDbCursor.getColumnIndex(columnMovieVoteAverage));
            String columnMovieVoteCount = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_COUNT;
            String movieVoteCount = mDbCursor.getString(mDbCursor.getColumnIndex(columnMovieVoteCount));
            String columnMovieOverview = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_OVERVIEW;
            String movieOverview = mDbCursor.getString(mDbCursor.getColumnIndex(columnMovieOverview));

            Intent movieDetailsIntent = new Intent(mContext, MovieDetailsActivity.class);
            movieDetailsIntent.putExtra(MainActivity.EXTRA_MOVIE_ID, movieID); // 18028
            movieDetailsIntent.putExtra(MainActivity.EXTRA_MOVIE_FULL_URL, moviePosterUrl); // poster image url
            movieDetailsIntent.putExtra(MovieDetailsActivity.EXTRA_FAVORITE_TITLE, movieTitle);
            movieDetailsIntent.putExtra(MovieDetailsActivity.EXTRA_FAVORITE_RELEASE_DATE, movieReleaseDate);
            movieDetailsIntent.putExtra(MovieDetailsActivity.EXTRA_FAVORITE_VOTE_AVERAGE, movieVoteAverage);
            movieDetailsIntent.putExtra(MovieDetailsActivity.EXTRA_FAVORITE_VOTE_COUNT, movieVoteCount);
            movieDetailsIntent.putExtra(MovieDetailsActivity.EXTRA_FAVORITE_OVERVIEW, movieOverview);
            mContext.startActivity(movieDetailsIntent);
        }
    }
}
