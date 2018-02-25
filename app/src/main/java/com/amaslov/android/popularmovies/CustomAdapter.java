package com.amaslov.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by aarta on 2018-02-25.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private static String[] mPosterUrls;
    final private ListItemClickListener mOnClickListener;

    public CustomAdapter(ListItemClickListener listener, String[] posterUrls) {
        mOnClickListener = listener;
        mPosterUrls = posterUrls;
    }

    @Override
    public int getItemCount() {
        if (mPosterUrls == null) {
            return 0;
        }
        return mPosterUrls.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_poster_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");
        Picasso.with(viewHolder.posterImageView.getContext())
                .load(mPosterUrls[position])
                .placeholder(R.drawable.poster_placeholder)
                .centerCrop()
                .resize(150, 200)
                .into(viewHolder.getImageView());
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView posterImageView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            posterImageView = (ImageView) view.findViewById(R.id.posterImageView);
        }

        public ImageView getImageView() {
            return posterImageView;
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }


}
