package com.amaslov.android.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.parcelables.MovieTrailerInfo;
import com.amaslov.android.popularmovies.utilities.UrlUtils;
import com.squareup.picasso.Picasso;


public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.ViewHolder> {

    private static final String TAG = "MovieTrailersAdapter";
    private static MovieTrailerInfo mMovieTrailerInfo;
    final private MovieTrailersAdapter.ListItemClickListener mOnClickListener;

    public MovieTrailersAdapter(MovieTrailersAdapter.ListItemClickListener listener, MovieTrailerInfo movieTrailerInfo) {
        mOnClickListener = listener;
        mMovieTrailerInfo = movieTrailerInfo;
    }

    @Override
    public int getItemCount() {
        if (mMovieTrailerInfo.getTrailerInfoLength() == 0) {
            return 0;
        }
        return mMovieTrailerInfo.getTrailerInfoLength();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.trailer_item, viewGroup, false);
        return new MovieTrailersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // load trailer thumbnails into imageView
        ImageView ivTrailerThumb = viewHolder.getTrailerThumbnailView();
        String[] youtubeThumbnailUrls = UrlUtils.getYoutubeThumbnailUrls(mMovieTrailerInfo.getTrailerKeys());
        Picasso.with(viewHolder.trailerThumbHolder.getContext())
                .load(youtubeThumbnailUrls[position])
                .into(ivTrailerThumb);
        // load trailer names into textView
        TextView tvTrailerName = viewHolder.getTrailerNameView();
        tvTrailerName.setText(mMovieTrailerInfo.getTrailerNames()[position]);
    }

    public interface ListItemClickListener {
        void onListItemClick(String trailerKey);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView trailerThumbHolder;
        private final TextView trailerNameHolder;

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            trailerThumbHolder = view.findViewById(R.id.iv_trailer_thumb_holder);
            trailerNameHolder = view.findViewById(R.id.tv_trailer_name);
        }

        private ImageView getTrailerThumbnailView() {
            return trailerThumbHolder;
        }

        private TextView getTrailerNameView() {
            return trailerNameHolder;
        }

        @Override
        public void onClick(View view) {
            int p = getAdapterPosition();
            mOnClickListener.onListItemClick(mMovieTrailerInfo.getTrailerKeys()[p]);
        }
    }
}
