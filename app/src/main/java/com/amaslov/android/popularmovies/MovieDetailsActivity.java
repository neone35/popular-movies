package com.amaslov.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amaslov.android.popularmovies.databinding.ActivityMovieDetailsBinding;

public class MovieDetailsActivity extends AppCompatActivity {

    ActivityMovieDetailsBinding activityMovieDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMovieDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        receiveMainActivityIntent();
    }

    private void receiveMainActivityIntent() {
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_ITEM_INDEX);
        activityMovieDetailsBinding.textView.setText(message);
    }
}
