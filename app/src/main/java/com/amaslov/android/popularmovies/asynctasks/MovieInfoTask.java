package com.amaslov.android.popularmovies.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amaslov.android.popularmovies.R;
import com.amaslov.android.popularmovies.parcelables.MovieInfo;
import com.amaslov.android.popularmovies.utilities.JsonUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("StaticFieldLeak")
public class MovieInfoTask extends AsyncTask<String, Void, MovieInfo> {

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
        dialog.setTitle(R.string.getting_movies);
        dialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.show();
    }

    protected MovieInfo doInBackground(String... strings) {
        String configUrl = strings[0];
        String moviesUrl = strings[1];
        OkHttpClient client = new OkHttpClient();
        Request reqImageUrlConfig = new Request.Builder()
                .url(configUrl)
                .get()
                .build();
        Request reqMoviePostersJPG = new Request.Builder()
                .url(moviesUrl)
                .get()
                .build();
        try {
            Response resImageUrlConfig = client.newCall(reqImageUrlConfig).execute();
            Response resMoviePostersJPG = client.newCall(reqMoviePostersJPG).execute();

            String resConfigJSON = resImageUrlConfig.body().string();
            String resMoviesJSON = resMoviePostersJPG.body().string();
            String imageUrlConfig = JsonUtils
                    .getImageUrl(resConfigJSON);
            String[] moviePosterJPGs = JsonUtils
                    .getMovieValues(resMoviesJSON, JsonUtils.MOVIEDB_POSTER_PATH);
            String[] movieIDs = JsonUtils
                    .getMovieValues(resMoviesJSON, JsonUtils.MOVIEDB_ID);
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
