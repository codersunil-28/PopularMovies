package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.android.popularmovies.data.MovieContract.MovieEntry.COLUMN_MOVIE_ID;
import static com.example.android.popularmovies.data.MovieContract.MovieEntry.COLUMN_POSTER_PATH;
import static com.example.android.popularmovies.data.MovieContract.MovieEntry.COLUMN_RATING;
import static com.example.android.popularmovies.data.MovieContract.MovieEntry.COLUMN_RELEASE_DATE;
import static com.example.android.popularmovies.data.MovieContract.MovieEntry.COLUMN_SYNOPSIS;
import static com.example.android.popularmovies.data.MovieContract.MovieEntry.COLUMN_TITLE;

/**
 * Created by sunilkumar on 11/12/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    Context context;
    ArrayList movieList;
    private Cursor mCursor;
    public static final String LOG_TAG = MovieAdapter.class.getName();

    public MovieAdapter(Context context, ArrayList movieList) {
        this.context = context;
        this.movieList = movieList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (mCursor != null) {
            int titleIndex = mCursor.getColumnIndex(COLUMN_TITLE);
            int posterPathIndex = mCursor.getColumnIndex(COLUMN_POSTER_PATH);
            int synopsisIndex = mCursor.getColumnIndex(COLUMN_SYNOPSIS);
            int ratingIndex = mCursor.getColumnIndex(COLUMN_RATING);
            int dateIndex = mCursor.getColumnIndex(COLUMN_RELEASE_DATE);
            int movieIdIndex = mCursor.getColumnIndex(COLUMN_MOVIE_ID);
            while (mCursor.moveToNext()) {
                String title = mCursor.getString(titleIndex);
                String path = mCursor.getString(posterPathIndex);
                String synopsis = mCursor.getString(synopsisIndex);
                String rating = mCursor.getString(ratingIndex);
                double movieRating = Double.parseDouble(rating);
                String date = mCursor.getString(dateIndex);
                String movieId = mCursor.getString(movieIdIndex);
                int id = Integer.parseInt(movieId);

                Movies movies = new Movies(title, path, synopsis, movieRating, date, id);
                movieList.add(movies);
            }

        }


        Movies currentMovie = (Movies) movieList.get(position);
        String currentMoviePoster = currentMovie.getPosterPath();
        Log.i(LOG_TAG, "Movie Poster path : " + currentMoviePoster);

        Picasso.with(context).load(currentMoviePoster).into(holder.moviePoster);

    }

    @Override
    public int getItemCount() {
        return movieList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView moviePoster;

        public MyViewHolder(View itemView) {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.iv_movie_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MovieDetail.class);
            int position = getAdapterPosition();
            intent.putExtra("currentMovie", (Parcelable) movieList.get(position));
            context.startActivity(intent);
        }
    }

    void setMovieArrayList(ArrayList<Movies> movieList) {
        this.movieList.clear();
        this.movieList.addAll(movieList);
        if (movieList != null) {
            notifyDataSetChanged();
        }
    }

    public Cursor swapCursor(Cursor c) {

        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;


        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}
