package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sunilkumar on 29/10/17.
 */

public class Movies implements Parcelable{

    private String originalTitle;
    private String posterPath;
    private String plotSynopsis;
    private double userRating;
    private String releaseDate;
    private int movieId;
    private static final String BASE_PATH_OF_POSTER = "http://image.tmdb.org/t/p/w185";
    ArrayList<String> mReviewsAuthor = new ArrayList<>();
    ArrayList<String> mReviewsContent = new ArrayList<>();
    ArrayList<String> mYoutubeId = new ArrayList<>();

    public Movies(String title, String path, String synopsis, double rating, String date, int id) {
        originalTitle = title;
        posterPath = path;
        plotSynopsis = synopsis;
        userRating = rating;
        releaseDate = date;
        movieId = id;
    }

    public Movies(ArrayList<String> reviewsAuthor, ArrayList<String> reviewsContent, ArrayList<String> youtubeId){
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

    public ArrayList<String> getMovieReviewsAuthor() {
        return mReviewsAuthor;
    }

    public ArrayList<String> getMovieReviewsContent() {
        return mReviewsContent;
    }

    public ArrayList<String> getMovieTrailers() {
        return mYoutubeId;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(plotSynopsis);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeInt(movieId);
//        dest.writeList(mReviewsAuthor);
//        dest.writeList(mReviewsContent);
//        dest.writeList(mYoutubeId);
    }

//    http://stackoverflow.com/questions/22446359/android-class-parcelable-with-arraylist

    public Movies(Parcel parcel){
        originalTitle = parcel.readString();
        posterPath = parcel.readString();
        plotSynopsis = parcel.readString();
        userRating = parcel.readDouble();
        releaseDate = parcel.readString();
        movieId = parcel.readInt();
//        mReviewsAuthor = parcel.readArrayList();
//        mReviewsContent = parcel.readArrayList();
//        mYoutubeId = parcel.readArrayList();
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>(){

        @Override
        public Movies createFromParcel(Parcel parcel) {
            return new Movies(parcel);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[0];
        }
    };
}
