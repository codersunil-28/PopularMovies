package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.popularmovies.data.MovieContract.MovieEntry.COLUMN_POSTER_PATH;
import static com.example.android.popularmovies.data.MovieContract.MovieEntry.CONTENT_URI;
import static com.example.android.popularmovies.data.MovieContract.MovieEntry.PATH_MOVIES_COLUMN_POSTER_PATH;
import static com.example.android.popularmovies.data.MovieContract.MovieEntry.TABLE_NAME;

/**
 * Created by sunilkumar on 07/12/17.
 */

public class MovieContentProvider extends ContentProvider {

//    public static final int MOVIES = 100;
    public static final int MOVIES_COLUMN_POSTER_PATH = 101;
    private MovieDbHelper movieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        uriMatcher.addURI(MovieContract.AUTHORITY,MovieContract.PATH_MOVIES,MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, PATH_MOVIES_COLUMN_POSTER_PATH, MOVIES_COLUMN_POSTER_PATH);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match){
            case MOVIES_COLUMN_POSTER_PATH:
                retCursor = db.query(PATH_MOVIES_COLUMN_POSTER_PATH,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch(match){
            case MOVIES_COLUMN_POSTER_PATH:
                long id = db.insert(PATH_MOVIES_COLUMN_POSTER_PATH, null,values);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int tasksDeleted;
        switch (match) {

            case MOVIES_COLUMN_POSTER_PATH:

                String posterPath = uri.getPathSegments().get(1);

                tasksDeleted = db.delete(TABLE_NAME, COLUMN_POSTER_PATH + "=?", new String[]{posterPath});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
