package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by sunilkumar on 06/12/17.
 */

public class FavMovieAdapter extends CursorAdapter {

    private Cursor cursorData;
    public static final String LOG_TAG = FavMovieAdapter.class.getName();


    public FavMovieAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        cursorData = cursor;
    }

    public FavMovieAdapter(Context context) {
        super(context, null, 0);

    }

    static class ViewHolder {

        ImageView imageView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        cursorData = cursor;
        if (cursorData != null) {

            if (cursorData.moveToNext()) {

                FavMovieAdapter.ViewHolder holder = null;
                holder = new FavMovieAdapter.ViewHolder();
                holder.imageView = (ImageView) view.findViewById(R.id.iv_movie);
                view.setTag(holder);
                String path = cursorData.getString(cursorData.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                Log.i(LOG_TAG, "Path of the movie Poster: " + path);
                Picasso.with(context).load(path).into(holder.imageView);
            }
        }
        cursorData.close();
    }

    public Cursor swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (cursorData != null) cursorData.close();
        cursorData = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
        return cursorData;
    }

    @Override
    public int getCount() {
        return cursorData.getCount();
    }
}
