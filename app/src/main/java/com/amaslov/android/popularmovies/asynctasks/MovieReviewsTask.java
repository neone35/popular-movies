package com.amaslov.android.popularmovies.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.parcelables.MovieReviewInfo;
import com.amaslov.android.popularmovies.parcelables.MovieTrailerInfo;
import com.amaslov.android.popularmovies.utilities.JsonUtils;
import com.amaslov.android.popularmovies.utilities.UrlUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("StaticFieldLeak")
public class MovieReviewsTask extends AsyncTask<String, Void, MovieReviewInfo> {
    private ProgressDialog dialog;
    private OnEventListener<MovieReviewInfo> mCallBack;
    private Context mContext;
    private Exception mException;

    public MovieReviewsTask(Context context, OnEventListener<MovieReviewInfo> callback) {
        mContext = context;
        mCallBack = callback;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setTitle(mContext.getString(R.string.getting_reviews));
        dialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected MovieReviewInfo doInBackground(String... strings) {
        String movieId = strings[0];
        String movieReviewsUrl = UrlUtils.getReviewsUrl(movieId);
        OkHttpClient client = new OkHttpClient();
        Request reqMovieReviews = new Request.Builder()
                .url(movieReviewsUrl)
                .get()
                .build();
        try {
            Response resMovieReviews = client.newCall(reqMovieReviews).execute();
            String resReviewsJSON = resMovieReviews.body().string();
            return JsonUtils.getMovieReviewsInfo(resReviewsJSON);
        } catch (Exception e) {
            mException = e;
            return null;
        }
    }

    protected void onPostExecute(MovieReviewInfo movieReviewInfo) {
        dialog.dismiss();
        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(movieReviewInfo);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
