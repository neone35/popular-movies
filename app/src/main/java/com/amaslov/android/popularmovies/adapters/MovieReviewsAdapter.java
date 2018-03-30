package com.amaslov.android.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.parcelables.MovieReviewInfo;
import com.amaslov.android.popularmovies.utilities.UrlUtils;
import com.squareup.picasso.Picasso;

import at.blogc.android.views.ExpandableTextView;


public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    private static final String TAG = "MovieReviewsAdapter";
    private static MovieReviewInfo mMovieReviewInfo;

    public MovieReviewsAdapter(MovieReviewInfo movieReviewInfo) {
        mMovieReviewInfo = movieReviewInfo;
    }

    @Override
    public int getItemCount() {
        if (mMovieReviewInfo.getReviewInfoLength() == 0) {
            return 0;
        }
        return mMovieReviewInfo.getReviewInfoLength();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_item, viewGroup, false);
        return new MovieReviewsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        TextView tvReviewAuthor = viewHolder.getReviewAuthor();
        tvReviewAuthor.setText(mMovieReviewInfo.getReviewAuthors()[position]);
        final ExpandableTextView etvReviewContent = viewHolder.getReviewContent();
        etvReviewContent.setText(mMovieReviewInfo.getReviewContents()[position]);
        etvReviewContent.setInterpolator(new OvershootInterpolator());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView reviewAuthor;
        private final ExpandableTextView reviewContent;
        private final View expandCollapseChevron;

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            reviewAuthor = view.findViewById(R.id.tv_review_author);
            reviewContent = view.findViewById(R.id.etv_review_content);
            expandCollapseChevron = view.findViewById(R.id.ic_chevron);
        }

        private TextView getReviewAuthor() {
            return reviewAuthor;
        }

        private ExpandableTextView getReviewContent() {
            return reviewContent;
        }

        @Override
        public void onClick(View view) {
            int p = getAdapterPosition();
            expandCollapseChevron.setBackgroundResource(reviewContent.isExpanded() ? R.drawable.ic_chevron_down : R.drawable.ic_chevron_up);
            reviewContent.toggle();
        }
    }
}
