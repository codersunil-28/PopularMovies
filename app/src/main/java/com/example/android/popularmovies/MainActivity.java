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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movies>> {

    private ArrayList<Movies> mMoviesData;
    private TextView mEmptyStateTextView;
    private PopularMovieAdapter mAdapter;
    private static final int MOVIE_LOADER_ID = 1;
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "8269544114add3a8508b7721bf799f09";
    private static final String API_KEY_STRING = "api_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        gridview.setEmptyView(mEmptyStateTextView);
        mMoviesData = new ArrayList<>();
        mAdapter = new PopularMovieAdapter(this, R.layout.grid_item, mMoviesData);
        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movies currentMovie = (Movies) parent.getItemAtPosition(position);
                String title = currentMovie.getOriginalTitle();
                String path = currentMovie.getPosterPath();
                String synopsis = currentMovie.getPlotSynopsis();
                double rating = currentMovie.getUserRating();
                String date = currentMovie.getReleaseDate();
                int movieId = currentMovie.getMovieId();

                Intent intent = new Intent(MainActivity.this, MovieDetail.class);
                intent.putExtra("title", title);
                intent.putExtra("path", path);
                intent.putExtra("synopsis", synopsis);
                intent.putExtra("rating", rating);
                intent.putExtra("date", date);
                intent.putExtra("movieId", movieId);
                startActivity(intent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).

            Bundle b = new Bundle();
            b.putString("sort_order", "popular");
            loaderManager.initLoader(MOVIE_LOADER_ID, b, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<Movies>> onCreateLoader(int id, Bundle args) {

        String apiParam = null;
        if ((args != null) && (args.getString("sort_order") != null)) {
            apiParam = args.getString("sort_order");
        }

        Uri baseUri = Uri.parse(MOVIE_BASE_URL + apiParam);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(API_KEY_STRING, API_KEY);
        uriBuilder.appendQueryParameter("language", "en-US");
        uriBuilder.appendQueryParameter("page", "1");

        return new MoviesLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {
        mAdapter.clear();
    }

    private static class MoviesLoader extends AsyncTaskLoader<List<Movies>> {
        /**
         * Tag for log messages
         */
        private final String LOG_TAG = MoviesLoader.class.getName();

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
        public List<Movies> loadInBackground() {
            Log.i(LOG_TAG, "TEST: loadInBackground() Called");
            if (mUrl == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of movie.
            List<Movies> moviesList = QueryUtils.fetchPopularMoviesData(mUrl);
            return moviesList;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> moviesList) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        if (moviesList != null && !moviesList.isEmpty()) {
            mMoviesData.addAll(moviesList);
            mAdapter.setGridData(mMoviesData);
        }

        // Set empty state text to display "No movies found."
        mEmptyStateTextView.setText(R.string.no_movies);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle b = new Bundle();
        switch (item.getItemId()) {
            case R.id.most_pop:
                b.putString("sort_order", "popular");
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, b, this);
                return true;

            case R.id.high_rated:
                b.putString("sort_order", "top_rated");
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, b, this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}