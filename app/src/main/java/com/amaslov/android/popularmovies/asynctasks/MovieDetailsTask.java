package com.amaslov.android.popularmovies.asynctasks;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.utilities.JsonUtils;
import com.amaslov.android.popularmovies.utilities.UrlUtils;

@SuppressLint("StaticFieldLeak")
public class MovieDetailsTask extends AsyncTask<String, Void, MovieDetails> {
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
        dialog.setTitle(R.string.getting_movie_details);
        dialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected MovieDetails doInBackground(String... strings) {
        String movieId = strings[0];
        String movieFullUrl = strings[1];
        String movieDetailsUrl = UrlUtils.getDetailsUrl(movieId);
        OkHttpClient client = new OkHttpClient();
        Request reqMovieDetails = new Request.Builder()
                .url(movieDetailsUrl)
                .get()
                .build();
        try {
            Response resMovieDetails = client.newCall(reqMovieDetails).execute();
            String resDetailsJSON = resMovieDetails.body().string();
            return JsonUtils
                    .getMovieDetails(resDetailsJSON, movieFullUrl);
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


