package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private ArrayList<Movies> mMoviesData = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private static final int MOVIE_LOADER_ID = 1;
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "8269544114add3a8508b7721bf799f09";
    private static final String API_KEY_STRING = "api_key";

    Cursor c;
    private final String TAG = MainActivity.class.getSimpleName();


    public static final int FAVORITES_MOVIE_LOADER_ID = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter(MainActivity.this, mMoviesData);
        recyclerView.setAdapter(movieAdapter);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            LoaderManager loaderManager = getLoaderManager();

            Bundle b = new Bundle();
            b.putString("sort_order", "popular");

            loaderManager.initLoader(MOVIE_LOADER_ID, b, this);

        } else {
            Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }


//        getLoaderManager().initLoader(FAVORITES_MOVIE_LOADER_ID, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        switch (id) {

            case MOVIE_LOADER_ID:

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

            case FAVORITES_MOVIE_LOADER_ID:

                return new AsyncTaskLoader<Cursor>(this) {
                    @Override
                    protected void onStartLoading() {
                        forceLoad();
                    }

                    @Override
                    public Cursor loadInBackground() {
                        try {
                            return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    null);

                        } catch (Exception e) {
                            Log.e(TAG, "Failed to asynchronously load data.");
                            e.printStackTrace();
                            return null;
                        }
                    }
                };

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

        if(loader.getId() == FAVORITES_MOVIE_LOADER_ID){
            movieAdapter.setMovieArrayList(null);
        }
    }

    private static class MoviesLoader extends AsyncTaskLoader<ArrayList<Movies>> {
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
        public ArrayList<Movies> loadInBackground() {
            Log.i(LOG_TAG, "TEST: loadInBackground() Called");
            if (mUrl == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of movie.
            ArrayList<Movies> moviesList = QueryUtils.fetchPopularMoviesData(mUrl);

            return moviesList;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case MOVIE_LOADER_ID:

                ArrayList<Movies> moviesList = (ArrayList) data;

                if (moviesList != null && !moviesList.isEmpty()) {

                    mMoviesData.addAll(moviesList);

                    movieAdapter.setMovieArrayList(mMoviesData);
                }

                break;

            case FAVORITES_MOVIE_LOADER_ID:

                c = (Cursor) data;

                movieAdapter.swapCursor(c);
                break;
        }
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

            case R.id.favorite:

                getFavoriteMovies();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void getFavoriteMovies() {

        LoaderManager loaderManager = getLoaderManager();
        Loader<Cursor> favoriteMovieLoader = loaderManager.getLoader(FAVORITES_MOVIE_LOADER_ID);

        if (favoriteMovieLoader == null){
            loaderManager.initLoader(FAVORITES_MOVIE_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(FAVORITES_MOVIE_LOADER_ID, null, this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(FAVORITES_MOVIE_LOADER_ID, null, this);
    }
}