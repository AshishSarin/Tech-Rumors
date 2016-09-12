package com.sareen.squarelabs.techrumors.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ashish on 11-09-2016.
 */
public class TechRumorsContract
{
    public static final String CONTENT_AUTHORITY =
            "com.sareen.squarelabs.techrumors.provider";

    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SAVED_POSTS = "saved_posts";

    public static final class SavedPostsEntry implements BaseColumns
    {
        /*Declaring and defining constants for table name and columns*/

        // SavedPosts table name
        public static final String TABLE_NAME = "saved_posts";

        //Column with title of the saved post
        public static final String COLUMN_POST_TITLE = "post_title";

        //Column with content of post in html format
        public static final String COLUMN_POST_CONTENT = "post_content";

        // Declaring and defining uris for content provider
        // content_uri is defined as
        // "com.sareen.squarelabs.techrumors.provider/saved_posts"
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVED_POSTS)
                .build();

        // Defining the type of content returned by uris
        // [single or multiple items]
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_SAVED_POSTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_SAVED_POSTS;

        public static Uri buildSavedPostsUri()
        {
            return CONTENT_URI;
        }

        public static Uri buildSavedPostWithIdUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri)
        {
            return ContentUris.parseId(uri);
        }


    }

}
