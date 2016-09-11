package com.sareen.squarelabs.techrumors.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.sareen.squarelabs.techrumors.data.TechRumorsContract.SavedPostsEntry;

/**
 * Created by Ashish on 11-09-2016.
 */
public class TechRumorsProvider extends ContentProvider
{
    // URI Matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private TechRumorsHelper mOpenHelper;

    // Codes used for uri matcher
    static final int POSTS = 201;
    static final int POST_WITH_ID = 202;





    // This method is used to build the uri matcher and return it
    static UriMatcher buildUriMatcher()
    {
        // The code passed to constructor is code to match for root
        // In this case we do not want for root to match any data,
        // so NO_MATCH is passed
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TechRumorsContract.CONTENT_AUTHORITY;

        // this is matched when multiple posts are queried in saved activity
        uriMatcher.addURI(authority, TechRumorsContract.PATH_SAVED_POSTS, POSTS);
        // this is matched when single post is queried inside detail activity
        uriMatcher.addURI(authority, TechRumorsContract.PATH_SAVED_POSTS + "/#", POST_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new TechRumorsHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query
            (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            // multiple posts are queried
            case POSTS:
                retCursor = db.query(SavedPostsEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            // single post with is queried
            case POST_WITH_ID:
                long _id = SavedPostsEntry.getIdFromUri(uri);
                retCursor = db.query(SavedPostsEntry.TABLE_NAME, projection,
                        sSavedIdSelection, new String[]{Long.toString(_id)},
                        null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private static final String sSavedIdSelection =
            SavedPostsEntry.TABLE_NAME + "." +
                    SavedPostsEntry._ID + " = ?";
    @Nullable
    @Override
    public String getType(Uri uri)
    {
        // use the uri matcher to find out what type of uri this is
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case POSTS:
                return SavedPostsEntry.CONTENT_TYPE;
            case POST_WITH_ID:
                return SavedPostsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            // insertion is going to be in posts only
            case POSTS:
                long _id = db.insert(SavedPostsEntry.TABLE_NAME, null, values);
                if(_id > 0)
                {
                    returnUri = SavedPostsEntry.buildSavedPostWithIdUri(_id);
                }
                else
                {
                    throw new SQLiteException("Failed to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if(selection == null)
        {
            // all rows need to be deleted
            // To return no of rows deleted,
            // we need to make selection = "1";
            selection = "1";
        }

        switch (match)
        {
            // deletion is going to be in posts only
            case POSTS:
                rowsDeleted = db.delete(SavedPostsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match)
        {
            // updating is going to be in posts only
            case POSTS:
                rowsUpdated = db.update(SavedPostsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


}
