package com.amaslov.android.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.parcelables.MovieInfo;
import com.squareup.picasso.Picasso;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {
    private static final String TAG = "MoviePosterAdapter";
    private static MovieInfo movie;
    final private ListItemClickListener mOnClickListener;

    public MoviePosterAdapter(ListItemClickListener listener, MovieInfo moviesInfo) {
        mOnClickListener = listener;
        movie = moviesInfo;
    }

    @Override
    public int getItemCount() {
        if (movie.getFullUrls() == null) {
            return 0;
        }
        return movie.getFullUrls().length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_poster_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");
        ImageView ivContext = viewHolder.getImageView();
        int mainPosterWidth =
                (int) (ivContext.getResources().getDimension(R.dimen.main_poster_width) /
                        ivContext.getResources().getDisplayMetrics().density);
        int mainPosterHeight =
                (int) (ivContext.getResources().getDimension(R.dimen.main_poster_height) /
                        ivContext.getResources().getDisplayMetrics().density);
        Picasso.with(viewHolder.posterImageView.getContext())
                .load(movie.getFullUrls()[position])
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
            mOnClickListener.onListItemClick(movie.getIds()[p], movie.getFullUrls()[p]);
        }
    }


}
