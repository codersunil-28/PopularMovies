package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.databinding.ActivityMovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movies> {

    private static final String API_KEY = "8269544114add3a8508b7721bf799f09";
    private static final String API_KEY_STRING = "api_key";
    private final String LOG_TAG = MovieDetail.class.getName();
    private static final int MOVIE_LOADER_ID = 1;
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private TextView mEmptyStateTextView;
    private static final String APPEND_STRING = "append_to_response";
    private static final String REVIEWS_AND_TRAILERS = "reviews,videos";
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private ActivityMovieDetailBinding detailsBinding;
    private Boolean favorite = false;



    private String path;
//    FavMovieAdapter favMovieAdapter = new FavMovieAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Get intent data
        Intent i = getIntent();


        String title = i.getExtras().getString("title");
        path = i.getExtras().getString("path");
        String synopsis = i.getExtras().getString("synopsis");
        double rating = i.getExtras().getDouble("rating");
        String date = i.getExtras().getString("date");
        int id = i.getExtras().getInt("movieId");
        String movieId = String.valueOf(id);

        detailsBinding.tvTitle.setText(title);

        Picasso.with(this).load(path).into(detailsBinding.ivPoster);

        detailsBinding.tvReleaseDate.setText(date);

        detailsBinding.tvRating.setText(String.valueOf(rating));

        detailsBinding.tvSynopsis.setText(synopsis);


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
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
        String apiParam = null;
        if ((args != null) && (args.getString("movie_id") != null)) {
            apiParam = args.getString("movie_id");
        }

        Uri baseUri = Uri.parse(MOVIE_BASE_URL + apiParam);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(API_KEY_STRING, API_KEY);
        uriBuilder.appendQueryParameter(APPEND_STRING, REVIEWS_AND_TRAILERS);

        return new MovieDetail.MoviesLoader(this, uriBuilder.toString());
    }

    private static class MoviesLoader extends AsyncTaskLoader<Movies> {
        /**
         * Tag for log messages
         */
        private final String LOG_TAG = MovieDetail.MoviesLoader.class.getName();

        /**
         * Query URL
         */
        private String mUrl;

        public MoviesLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        /**
         * This is on a background thread.
         */
        @Override
        public Movies loadInBackground() {
            Log.i(LOG_TAG, "TEST: loadInBackground() Called");
            if (mUrl == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of movie.
            Movies movieReviewsAndTrailers = QueryUtils.fetchReviewsAndTrailers(mUrl);
            return movieReviewsAndTrailers;
        }
    }

    @Override
    public void onLoadFinished(Loader<Movies> loader, Movies data) {

        if (data != null) {

            List<String> movieReviewsAuthor = data.getMovieReviewsAuthor();
            List<String> movieReviewsContent = data.getMovieReviewsContent();
            List<String> movieTrailers = data.getMovieTrailers();

            if(movieTrailers.size() > 0){
                trailerAdapter.setTrailerArrayList(movieTrailers);
            }

            if (movieReviewsAuthor.size() > 0 && movieReviewsContent.size() > 0){
                reviewAdapter.setReviewArrayList(movieReviewsAuthor, movieReviewsContent);
            }


//                        // Set empty state text to display "No trailers and reviews found."
//                        mEmptyStateTextView.setText(R.string.no_trailers_and_reviews);
//                    }

        }

    }

    @Override
    public void onLoaderReset(Loader<Movies> loader) {

    }

    public void insertAndDeleteData(View view){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            if(favorite){
                detailsBinding.ivFavButton.setImageResource(R.drawable.ic_favorite_black_48dp);
                deleteFavoriteFromDb(path);
//                favMovieAdapter.swapCursor(getAllPosterPath());
                favorite = false;
            }else {
                detailsBinding.ivFavButton.setImageResource(R.drawable.ic_favorite_red_48dp);
                insertFavoriteToDb(path);
//                favMovieAdapter.swapCursor(getAllPosterPath());
                favorite = true;
            }
        }else{
            Toast.makeText(MovieDetail.this, "Favorites can't be modified offline", Toast.LENGTH_SHORT).show();
            return;
        }

    }


    private void insertFavoriteToDb(String posterPath) {

        if((posterPath == null) || (TextUtils.isEmpty(posterPath))){
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,posterPath);
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,contentValues);

        if(uri != null) {
            Toast.makeText(getBaseContext(), "Movie added to Favorite", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteFavoriteFromDb(String posterPath) {

        String where = MovieContract.MovieEntry.COLUMN_POSTER_PATH + "=?";
        getContentResolver().delete(MovieContract.MovieEntry.MOVIE_CONTENT_URI, where, new String[]{posterPath});
    }

//    private Cursor getAllPosterPath() {
//        return mDb.query(
//                MovieContract.MovieEntry.TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,
//                MovieContract.MovieEntry.COLUMN_POSTER_PATH
//        );
//    }


}
