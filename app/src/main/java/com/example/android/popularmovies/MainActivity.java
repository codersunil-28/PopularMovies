package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movies>>,
MovieAdapter.setOnMovieClick{

    private ArrayList<Movies> mMoviesData;
    private MovieAdapter movieAdapter;
    private static final int MOVIE_LOADER_ID = 1;
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "8269544114add3a8508b7721bf799f09";
    private static final String API_KEY_STRING = "api_key";
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    public static final String CURRENT_MOVIE_KEY = "currentMovie";
    public static final String SORT_ORDER = "sort_order";
    public static final String TOP_RATED = "top_rated";
    public static final String POPULAR = "popular";
    Cursor c;
    private final String TAG = MainActivity.class.getSimpleName();
    private ProgressBar mProgressBar;


    private static final int FAVORITES_MOVIE_LOADER_ID = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mMoviesData = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter(MainActivity.this, mMoviesData, this);
        recyclerView.setAdapter(movieAdapter);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            LoaderManager loaderManager = getLoaderManager();

            Bundle b = new Bundle();
            b.putString(SORT_ORDER, POPULAR);

            loaderManager.initLoader(MOVIE_LOADER_ID, b, this);

        } else {

            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public Loader<ArrayList<Movies>> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case MOVIE_LOADER_ID:

            String apiParam = null;
            if ((args != null) && (args.getString(SORT_ORDER) != null)) {
                apiParam = args.getString(SORT_ORDER);
            }

            Uri baseUri = Uri.parse(MOVIE_BASE_URL + apiParam);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter(API_KEY_STRING, API_KEY);
            uriBuilder.appendQueryParameter("language", "en-US");
            uriBuilder.appendQueryParameter("page", "1");

            return new MoviesLoader(this, uriBuilder.toString());

//            case FAVORITES_MOVIE_LOADER_ID:
//
//                return new CursorLoader(getApplicationContext(), MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.MOVIE_COLUMNS, null, null, null);

//                return new AsyncTaskLoader<Cursor>(this) {
//                    @Override
//                    protected void onStartLoading() {
//                        forceLoad();
//                    }
//
//                    @Override
//                    public Cursor loadInBackground() {
//                        try {
//                            return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
//                                    null,
//                                    null,
//                                    null,
//                                    null);
//
//                        } catch (Exception e) {
//                            Log.e(TAG, "Failed to asynchronously load data.");
//                            e.printStackTrace();
//                            return null;
//                        }
//                    }
//                };

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> data) {

        mProgressBar.setVisibility(View.GONE);

        mMoviesData = data;

        if (mMoviesData != null && !mMoviesData.isEmpty()) {

            boolean val = mMoviesData.addAll(mMoviesData);
            movieAdapter.setMovieArrayList(mMoviesData);
            Log.v(TAG, "onLoadFinished ArrayList value "+ mMoviesData.size() + val);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movies>> loader) {

    }

    @Override
    public void onMovieClick(int position) {
        Movies movie = mMoviesData.get(position);
        if (movie != null && mMoviesData.size() > 0) {
            Intent intent = new Intent(this, MovieDetail.class);
            intent.putExtra(CURRENT_MOVIE_KEY, movie);
            startActivity(intent);
        }
    }

    private static class MoviesLoader extends AsyncTaskLoader<ArrayList<Movies>> {

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



//            case FAVORITES_MOVIE_LOADER_ID:
//
//                c = (Cursor) data;
//
//                movieAdapter.swapCursor(c);
//                break;

    private LoaderManager.LoaderCallbacks<Cursor> favoriteLoaderManager = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getApplicationContext(), MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.MOVIE_COLUMNS, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            movieAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

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
                b.putString(SORT_ORDER, POPULAR);
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, b, this);
                return true;

            case R.id.high_rated:
                b.putString(SORT_ORDER, TOP_RATED);
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, b, this);
                return true;

            case R.id.favorite:

                getLoaderManager().initLoader(FAVORITES_MOVIE_LOADER_ID, null, favoriteLoaderManager);
//                getFavoriteMovies();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

//    public void getFavoriteMovies() {
//
//        LoaderManager loaderManager = getLoaderManager();
//        Loader<Cursor> favoriteMovieLoader = loaderManager.getLoader(FAVORITES_MOVIE_LOADER_ID);
//
//        if (favoriteMovieLoader == null){
//            loaderManager.initLoader(FAVORITES_MOVIE_LOADER_ID, null, this);
//        } else {
//            loaderManager.restartLoader(FAVORITES_MOVIE_LOADER_ID, null, this);
//        }
//
//    }

}