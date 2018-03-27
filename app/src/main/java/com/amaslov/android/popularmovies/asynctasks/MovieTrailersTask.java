package com.amaslov.android.popularmovies.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.parcelables.MovieTrailerInfo;
import com.amaslov.android.popularmovies.utilities.JsonUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("StaticFieldLeak")
public class MovieTrailersTask extends AsyncTask<String, Void, MovieTrailerInfo> {
    private ProgressDialog dialog;
    private OnEventListener<MovieTrailerInfo> mCallBack;
    private Context mContext;
    private Exception mException;

    public MovieTrailersTask(Context context, OnEventListener<MovieTrailerInfo> callback) {
        mContext = context;
        mCallBack = callback;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setTitle(mContext.getString(R.string.getting_movie_trailers));
        dialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected MovieTrailerInfo doInBackground(String... strings) {
        String movieTrailerUrl = strings[0];
        OkHttpClient client = new OkHttpClient();
        Request reqMovieTrailers = new Request.Builder()
                .url(movieTrailerUrl)
                .get()
                .build();
        try {
            Response resMovieTrailers = client.newCall(reqMovieTrailers).execute();
            String resTrailersJSON = resMovieTrailers.body().string();
            return JsonUtils.getMovieTrailerKeys(resTrailersJSON);
        } catch (Exception e) {
            mException = e;
            return null;
        }
    }

    protected void onPostExecute(MovieTrailerInfo movieTrailerInfo) {
        dialog.dismiss();
        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(movieTrailerInfo);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
