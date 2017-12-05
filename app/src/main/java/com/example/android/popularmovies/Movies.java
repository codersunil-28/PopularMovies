package com.example.android.popularmovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilkumar on 29/10/17.
 */

public class Movies {

    private String originalTitle;
    private String posterPath;
    private String plotSynopsis;
    private double userRating;
    private String releaseDate;
    private int movieId;
    private static final String BASE_PATH_OF_POSTER = "http://image.tmdb.org/t/p/w185";
    List<String> mReviewsAuthor = new ArrayList<>();
    List<String> mReviewsContent = new ArrayList<>();
    List<String> mYoutubeId = new ArrayList<>();

    public Movies(String title, String path, String synopsis, double rating, String date, int id) {
        originalTitle = title;
        posterPath = path;
        plotSynopsis = synopsis;
        userRating = rating;
        releaseDate = date;
        movieId = id;
    }

    public Movies(List<String> reviewsAuthor, List<String> reviewsContent, List<String> youtubeId){
        mReviewsAuthor = reviewsAuthor;
        mReviewsContent = reviewsContent;
        mYoutubeId = youtubeId;
    }




    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        String completePathOfPoster = BASE_PATH_OF_POSTER + posterPath;
        return completePathOfPoster;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getMovieId() {
        return movieId;
    }

    public List<String> getMovieReviewsAuthor() {
        return mReviewsAuthor;
    }

    public List<String> getMovieReviewsContent() {
        return mReviewsContent;
    }

    public List<String> getMovieTrailers() {
        return mYoutubeId;
    }

}
