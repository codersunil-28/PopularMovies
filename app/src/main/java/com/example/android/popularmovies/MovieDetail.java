package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movies> {

    private static final String API_KEY = "";
    private static final String API_KEY_STRING = "api_key";
    private final String LOG_TAG = MovieDetail.class.getName();
    private static final int MOVIE_LOADER_ID = 1;
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private TextView mEmptyStateTextView;
    private static final String APPEND_STRING = "append_to_response";
    private static final String REVIEWS_AND_TRAILERS = "reviews,videos";
    private String trailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Get intent data
        Intent i = getIntent();


        String title = i.getExtras().getString("title");
        String path = i.getExtras().getString("path");
        String synopsis = i.getExtras().getString("synopsis");
        double rating = i.getExtras().getDouble("rating");
        String date = i.getExtras().getString("date");
        final int id = i.getExtras().getInt("movieId");
        String movieId = String.valueOf(id);

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(title);

        ImageView ivPoster = (ImageView) findViewById(R.id.iv_poster);
        Picasso.with(this).load(path).into(ivPoster);

        TextView tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvReleaseDate.setText(date);

        TextView tvRating = (TextView) findViewById(R.id.tv_rating);
        tvRating.setText(String.valueOf(rating));

        TextView tvSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        tvSynopsis.setText(synopsis);


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            Bundle b = new Bundle();
            b.putString("movie_id", movieId);
            loaderManager.initLoader(MOVIE_LOADER_ID, b, this);
        }else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


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
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);



        if (data != null) {

            List<String> movieReviews = data.getMovieReview();
            List<String> movieTrailers = data.getMovieTrailers();

            for(String youtubeId : movieTrailers){

                trailer = youtubeId;
                Button showTrailer = (Button) findViewById(R.id.button_show_trailer);
                showTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent openTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer));

                    if (openTrailer.resolveActivity(getPackageManager()) != null) {
                        startActivity(openTrailer);
                    }

                }
                });
            }
        }

        // Set empty state text to display "No trailers and reviews found."
        mEmptyStateTextView.setText(R.string.no_trailers_and_reviews);

    }

    @Override
    public void onLoaderReset(Loader<Movies> loader) {

    }
}
