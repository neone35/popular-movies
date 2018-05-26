package com.amaslov.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amaslov.android.popularmovies.MainActivity;
import com.amaslov.android.popularmovies.MovieDetailsActivity;
import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.parcelables.MovieInfo;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {
    private static final String TAG = "MoviePosterAdapter";
    private static MovieInfo movie;
    private Context mContext;

    public MoviePosterAdapter(Context context, MovieInfo moviesInfo) {
        mContext = context;
        movie = moviesInfo;
    }

    @Override
    public int getItemCount() {
        if (movie.getFullUrls() == null) {
            return 0;
        }
        return movie.getFullUrls().length;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_poster_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
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
//        viewHolder.getExtraText().setText(movie.getAvgVotes()[position]);
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView posterImageView;
        private final TextView posterExtraText;

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            posterImageView = view.findViewById(R.id.posterImageView);
            posterExtraText = view.findViewById(R.id.posterExtraInfo);
        }

        private ImageView getImageView() {
            return posterImageView;
        }

        private TextView getExtraText() {
            return posterExtraText;
        }

        @Override
        public void onClick(View view) {
            int p = getAdapterPosition();
            Intent movieDetailsIntent = new Intent(mContext, MovieDetailsActivity.class);
            movieDetailsIntent.putExtra(MainActivity.EXTRA_MOVIE_ID, movie.getIds()[p]); // 18028
            movieDetailsIntent.putExtra(MainActivity.EXTRA_MOVIE_FULL_URL, movie.getFullUrls()[p]); // poster image url
            mContext.startActivity(movieDetailsIntent);
        }
    }


}
