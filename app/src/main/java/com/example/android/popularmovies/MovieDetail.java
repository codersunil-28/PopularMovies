package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Get intent data
        Intent i = getIntent();


        String title = i.getExtras().getString("title");
        String path = i.getExtras().getString("path");
        String synopsis = i.getExtras().getString("synopsis");
        double rating = i.getExtras().getDouble("rating");
        String date = i.getExtras().getString("date");


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

    }

}
