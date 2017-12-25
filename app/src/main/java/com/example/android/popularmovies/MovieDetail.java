package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.databinding.ActivityMovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movies> {

    private static final String API_KEY = "8269544114add3a8508b7721bf799f09";
    private static final String API_KEY_STRING = "api_key";
    private final String LOG_TAG = MovieDetail.class.getName();
    private static final int MOVIE_LOADER_ID = 1;
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String APPEND_STRING = "append_to_response";
    private static final String REVIEWS_AND_TRAILERS = "reviews,videos";
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private ActivityMovieDetailBinding detailsBinding;
    private Boolean favorite = false;
    private Movies currentMovie;
    private static final int REVIEW_AND_TRAILER_LOADER_ID = 1;
    private final String TAG = MovieDetail.class.getSimpleName();
    private String title;
    private String path;
    private String synopsis;
    private double rating;
    private String date;
    private int id;
    private String movieId;
    private String movieRating;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    private static final int FAVORITES_MOVIE_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent i = getIntent();

        currentMovie = i.getParcelableExtra(MainActivity.CURRENT_MOVIE_KEY);


        title = currentMovie.getOriginalTitle();
        path = currentMovie.getPosterPath();
        synopsis = currentMovie.getPlotSynopsis();
        rating = currentMovie.getUserRating();
        movieRating = String.valueOf(rating);
        date = currentMovie.getReleaseDate();
        id = currentMovie.getMovieId();
        movieId = String.valueOf(id);

        detailsBinding.tvTitle.setText(title);

        Picasso.with(this).load(path).into(detailsBinding.ivPoster);

        detailsBinding.tvReleaseDate.setText(date);

        detailsBinding.tvRating.setText(movieRating);

        detailsBinding.tvSynopsis.setText(synopsis);


        connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            Bundle b = new Bundle();
            b.putString("movie_id", movieId);
            loaderManager.initLoader(MOVIE_LOADER_ID, b, this);
        }

        //Setup the movie trailer RecyclerView
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trailerAdapter = new TrailerAdapter(this);
        SnapHelper snapHelper = new LinearSnapHelper();
        detailsBinding.inTrailers.rvMovieTrailers.setLayoutManager(horizontalLayoutManager);
        detailsBinding.inTrailers.rvMovieTrailers.setAdapter(trailerAdapter);
        snapHelper.attachToRecyclerView(detailsBinding.inTrailers.rvMovieTrailers);

        //Setup the movie review RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        reviewAdapter = new ReviewAdapter(this);
        detailsBinding.inReviews.rvReviews.setLayoutManager(layoutManager);
        detailsBinding.inReviews.rvReviews.setAdapter(reviewAdapter);

    }

    @Override
    public Loader<Movies> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case REVIEW_AND_TRAILER_LOADER_ID:

                String apiParam = null;
                if ((args != null) && (args.getString("movie_id") != null)) {
                    apiParam = args.getString("movie_id");
                }

                Uri baseUri = Uri.parse(MOVIE_BASE_URL + apiParam);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter(API_KEY_STRING, API_KEY);
                uriBuilder.appendQueryParameter(APPEND_STRING, REVIEWS_AND_TRAILERS);

                return new MoviesLoader(this, uriBuilder.toString());

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Movies> loader, Movies data) {
        if (data != null) {

            Movies movieReviewsAndTrailers = (Movies) data;
            ArrayList<String> movieReviewsAuthor = movieReviewsAndTrailers.getMovieReviewsAuthor();
            ArrayList<String> movieReviewsContent = movieReviewsAndTrailers.getMovieReviewsContent();
            ArrayList<String> movieTrailers = movieReviewsAndTrailers.getMovieTrailers();

            if (movieTrailers.size() > 0) {
                trailerAdapter.setTrailerArrayList(movieTrailers);
            }

            if (movieReviewsAuthor.size() > 0 && movieReviewsContent.size() > 0) {
                reviewAdapter.setReviewArrayList(movieReviewsAuthor, movieReviewsContent);
            }


//                        // Set empty state text to display "No trailers and reviews found."
//                        mEmptyStateTextView.setText(R.string.no_trailers_and_reviews);
//                    }

        }
    }

        private static class MoviesLoader extends AsyncTaskLoader<Movies> {

            private final String LOG_TAG = MoviesLoader.class.getName();

            private String mUrl;

            public MoviesLoader(Context context, String url) {
                super(context);
                mUrl = url;
            }

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Movies loadInBackground() {
                Log.i(LOG_TAG, "TEST: loadInBackground() Called");
                if (mUrl == null) {
                    return null;
                }

                Movies movieReviewsAndTrailers = QueryUtils.fetchReviewsAndTrailers(mUrl);
                return movieReviewsAndTrailers;
            }
        }

        @Override
        public void onLoaderReset (Loader<Movies> loader){

        }

    @Override
    protected void onResume() {
        getLoaderManager().restartLoader(FAVORITES_MOVIE_LOADER_ID, null, favoriteLoaderManager);
        super.onResume();
    }

    public void insertDeleteFavMovie(View view){
        if (networkInfo != null && networkInfo.isConnected()) {

            if (favorite) {

                        deleteFavoriteFromDb(movieId);
                        favorite = false;
            } else {

                        insertFavoriteToDb();
                        favorite = true;
            }
        }else
        {
            Toast.makeText(MovieDetail.this, "Favorites can't be modified offline", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void insertFavoriteToDb() {

        if(favorite == false) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, path);
            contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieRating);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, date);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);

            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            Log.v(LOG_TAG, uri.toString());
            if (uri != null) {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            }
            getLoaderManager().initLoader(FAVORITES_MOVIE_LOADER_ID, null, favoriteLoaderManager);
        }else return;

    }

    private void deleteFavoriteFromDb(String movieId) {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId).build();
        int taskDeleted = getContentResolver().delete(uri, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + currentMovie.getMovieId(), null);
        Log.v(LOG_TAG,String.valueOf(taskDeleted));

        getLoaderManager().initLoader(FAVORITES_MOVIE_LOADER_ID, null, favoriteLoaderManager);

    }


    private LoaderManager.LoaderCallbacks<Cursor> favoriteLoaderManager = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getApplicationContext(), MovieContract.MovieEntry.CONTENT_URI, new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + currentMovie.getMovieId(), null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            boolean favMovieStatus;

            if (data != null && data.moveToFirst()) {
                data.close();
                 favMovieStatus = true;
            } else {
                 favMovieStatus = false;
            }

            if (favMovieStatus) {
                detailsBinding.faFavButton.setImageResource(R.drawable.ic_favorite_red_48dp);
            } else {
                detailsBinding.faFavButton.setImageResource(R.drawable.ic_favorite_black_48dp);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


}
