package com.amaslov.android.popularmovies.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amaslov.android.popularmovies.parcelables.MovieInfo;
import com.amaslov.android.popularmovies.utilities.MovieDBJsonUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("StaticFieldLeak")
public class MovieInfoTask extends AsyncTask<String[], Void, MovieInfo> {

    private ProgressDialog dialog;
    private OnEventListener<MovieInfo> mCallBack;
    private Context mContext;
    private Exception mException;

    public MovieInfoTask(Context context, OnEventListener<MovieInfo> callback) {
        mContext = context;
        mCallBack = callback;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setTitle("Getting movies...");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    protected MovieInfo doInBackground(String[]... strings) {
        String[] urls = strings[0];
        OkHttpClient client = new OkHttpClient();
        Request reqImageUrlConfig = new Request.Builder()
                .url(urls[0])
                .get()
                .build();
        Request reqMoviePostersJPG = new Request.Builder()
                .url(urls[1])
                .get()
                .build();
        try {
            Response resImageUrlConfig = client.newCall(reqImageUrlConfig).execute();
            Response resMoviePostersJPG = client.newCall(reqMoviePostersJPG).execute();

            String resConfigJSON = resImageUrlConfig.body().string();
            String resMoviesJSON = resMoviePostersJPG.body().string();
            String imageUrlConfig = MovieDBJsonUtils
                    .getImageUrl(resConfigJSON);
            String[] moviePosterJPGs = MovieDBJsonUtils
                    .getMovieValues(resMoviesJSON, MovieDBJsonUtils.MOVIE_DB_POSTER_PATH);
            String[] movieIDs = MovieDBJsonUtils
                    .getMovieValues(resMoviesJSON, MovieDBJsonUtils.MOVIE_DB_ID);
            String[] fullImageUrls = new String[moviePosterJPGs.length];
            for (int i = 0; i < fullImageUrls.length; i++) {
                fullImageUrls[i] = imageUrlConfig + moviePosterJPGs[i];
            }
            return new MovieInfo(fullImageUrls, movieIDs);
        } catch (Exception e) {
            mException = e;
            return null;
        }
    }

    protected void onPostExecute(MovieInfo moviesInfo) {
        dialog.dismiss();
        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(moviesInfo);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
