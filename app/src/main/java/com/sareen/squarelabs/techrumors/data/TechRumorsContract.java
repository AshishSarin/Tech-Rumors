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

    public static final String PATH_SAVED_ARTICLES = "saved_articles";

    public static final class SavedArticlesEntry implements BaseColumns
    {
        /*Declaring and defining constants for table name and columns*/

        // SavedArticles table name
        public static final String TABLE_NAME = "saved_articles";

        //Column with title of the article
        public static final String COLUMN_POST_TITLE = "post_title";

        // Declaring and defining uris for content provider
        // content_uri is defined as
        // "com.sareen.squarelabs.techrumors.provider/saved_articles"
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVED_ARTICLES)
                .build();

        // Defining the type of content returned by uris
        // [single or multiple items]
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_SAVED_ARTICLES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_SAVED_ARTICLES;

        public static Uri buildSavedArticleUri()
        {
            return CONTENT_URI;
        }

        public static Uri buildSavedArticleWithIdUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public long getIdFromUri(Uri uri)
        {
            return ContentUris.parseId(uri);
        }


    }

}
