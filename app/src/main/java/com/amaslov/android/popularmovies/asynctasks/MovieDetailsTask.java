package com.amaslov.android.popularmovies.asynctasks;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.utilities.MovieDBJsonUtils;
import com.amaslov.android.popularmovies.utilities.MovieDBUrlUtils;

@SuppressLint("StaticFieldLeak")
public class MovieDetailsTask extends AsyncTask<String[], Void, MovieDetails> {
    private ProgressDialog dialog;
    private OnEventListener<MovieDetails> mCallBack;
    private Context mContext;
    private Exception mException;

    public MovieDetailsTask(Context context, OnEventListener<MovieDetails> callback) {
        mContext = context;
        mCallBack = callback;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setTitle("Getting movie details...");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected MovieDetails doInBackground(String[]... strings) {
        String[] idAndUrl = strings[0];
        String movieDetailsUrl = MovieDBUrlUtils.getDetailsUrl(idAndUrl[0]);
        String moviePosterFullUrl = idAndUrl[1];
        OkHttpClient client = new OkHttpClient();
        Request reqMovieDetails = new Request.Builder()
                .url(movieDetailsUrl)
                .get()
                .build();
        try {
            Response resMovieDetails = client.newCall(reqMovieDetails).execute();
            String resDetailsJSON = resMovieDetails.body().string();
            return MovieDBJsonUtils
                    .getMovieDetails(resDetailsJSON, moviePosterFullUrl);
        } catch (Exception e) {
            mException = e;
            return null;
        }
    }

    protected void onPostExecute(MovieDetails movieDetails) {
        dialog.dismiss();
        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(movieDetails);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}


