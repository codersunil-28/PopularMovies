package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilkumar on 29/10/17.
 */

public class PopularMovieAdapter extends ArrayAdapter<Movies> {

    public static final String LOG_TAG = PopularMovieAdapter.class.getName();
    private Context context;
    private int layoutResourceId;
    private List<Movies> moviesData = new ArrayList();


    public PopularMovieAdapter(Context context, int layoutResourceId, List<Movies> moviesData) {
        super(context, layoutResourceId, moviesData);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.moviesData = moviesData;
    }

    public int getCount() {
        return moviesData.size();
    }


    /**
     * Updates grid data and refresh grid items.
     *
     * @param moviesData
     */
    public void setGridData(List<Movies> moviesData) {
        this.moviesData = moviesData;
        notifyDataSetChanged();
    }


    /**
     * Returns a GridView item that displays popular movie poster at the given position
     * in the GridView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View row = convertView;
        ViewHolder holder = null;

        int movieDataSize = getCount();
        Log.i(LOG_TAG, "Size of ArrayList of Movie DATA: " + movieDataSize);


        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.iv_movie);
            row.setTag(holder);
        } else {

            holder = (ViewHolder) row.getTag();
        }

        Movies currentMovie = moviesData.get(position);
        String path = currentMovie.getPosterPath();

        Log.i(LOG_TAG, "Path of the movie Poster: " + path);
        Picasso.with(context).load(path).into(holder.imageView);

        return row;

    }

    static class ViewHolder {

        ImageView imageView;
    }
}
