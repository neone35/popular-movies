package com.amaslov.android.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.utilities.MovieDBUrlUtils;
import com.squareup.picasso.Picasso;


public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.ViewHolder> {

    private static final String TAG = "MovieTrailersAdapter";
    private static String[] mTrailerKeys;
    final private MovieTrailersAdapter.ListItemClickListener mOnClickListener;

    public MovieTrailersAdapter(MovieTrailersAdapter.ListItemClickListener listener, String[] trailerKeys) {
        mOnClickListener = listener;
        mTrailerKeys = trailerKeys;
    }

    @Override
    public int getItemCount() {
        if (mTrailerKeys == null) {
            return 0;
        }
        return mTrailerKeys.length;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.linear_trailer_item, viewGroup, false);
        return new MovieTrailersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ImageView ivContext = viewHolder.getImageView();
        String[] youtubeThumbnailUrls = MovieDBUrlUtils.getYoutubeThumbnailUrls(mTrailerKeys);
        Picasso.with(viewHolder.trailerThumbHolder.getContext())
                .load(youtubeThumbnailUrls[position])
                .into(ivContext);
    }

    public interface ListItemClickListener {
        void onListItemClick(String trailerKey, View clickedView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView trailerThumbHolder;

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            trailerThumbHolder = view.findViewById(R.id.iv_trailer_thumb_holder);
        }

        private ImageView getImageView() {
            return trailerThumbHolder;
        }

        @Override
        public void onClick(View view) {
            int p = getAdapterPosition();
            mOnClickListener.onListItemClick(mTrailerKeys[p], view);
        }
    }
}
