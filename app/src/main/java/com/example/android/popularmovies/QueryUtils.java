package com.example.android.popularmovies;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilkumar on 29/10/17.
 */

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<Movies> fetchPopularMoviesData(String requestUrl) {
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        List<Movies> moviesData = extractFeatureFromJson(jsonResponse);

        return moviesData;
    }


    public static Movies fetchReviewsAndTrailers(String requestUrl) {
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        Movies movieReviewsAndTrailers = extractReviewsAndTrailersFromJson(jsonResponse);

        return movieReviewsAndTrailers;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return an {@link Movies} object by parsing out information
     * about the "Most Popular Movie" from the input movieJSON string.
     */
    private static List<Movies> extractFeatureFromJson(String movieJSON) {

        // Create an empty ArrayList that we can start adding movies to
        List<Movies> moviesDataList = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        try {

            // Create a JSONObject
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or movies).
            JSONArray movieArray = baseJsonResponse.getJSONArray("results");

            // For each movies in the moviesArray, create an {@link Movie} object
            for (int i = 0; i < movieArray.length(); i++) {

                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);

                String title = currentMovie.getString("original_title");

                String path = currentMovie.getString("poster_path");

                String synopsis = currentMovie.getString("overview");

                double rating = currentMovie.getDouble("vote_average");

                String date = currentMovie.getString("release_date");

                int id = currentMovie.getInt("id");

                Movies movie = new Movies(title, path, synopsis, rating, date, id);

                // Add the new {@link movie} to the list of movies.
                moviesDataList.add(movie);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
        }
        return moviesDataList;
    }


    private static Movies extractReviewsAndTrailersFromJson(String movieJSON) {

        List<String> reviews = new ArrayList<>();
        List<String> youtubeIds = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        try {

            // Create a JSONObject
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            JSONObject movieReviews = baseJsonResponse.getJSONObject("reviews");

            JSONArray reviewsResultsArray = movieReviews.getJSONArray("results");

            for (int i = 0; i < reviewsResultsArray.length(); i++) {

                JSONObject currentReviewResult = reviewsResultsArray.getJSONObject(i);

                String review = currentReviewResult.getString("content");

                reviews.add(review);
            }

            JSONObject movieVideos = baseJsonResponse.getJSONObject("videos");
            JSONArray videosResultsArray = movieVideos.getJSONArray("results");
            for (int i = 0; i < videosResultsArray.length(); i++) {

                JSONObject currentVideoResult = videosResultsArray.getJSONObject(i);

                String youtubeId = currentVideoResult.getString("key");

                youtubeIds.add(youtubeId);
            }

            Movies reviewsAndTrailers = new Movies(reviews, youtubeIds);
            return reviewsAndTrailers;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
            return null;
        }

    }


}
