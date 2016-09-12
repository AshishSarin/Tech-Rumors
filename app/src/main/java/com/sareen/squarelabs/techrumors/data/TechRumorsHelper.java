package com.sareen.squarelabs.techrumors.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.sareen.squarelabs.techrumors.data.TechRumorsContract.SavedPostsEntry;

/**
 * Created by Ashish on 11-09-2016.
 */
public class TechRumorsHelper extends SQLiteOpenHelper {

    // name of the file in which database is stored
    public static final String DATABASE_NAME = "techrumors.db";

    // Version of database
    // if schema of database is changed
    // then this needs to be increased.
    public static final int DATABASE_VERSION = 2;

    public TechRumorsHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL_CREATE_SAVED_POSTS_TABLE = "CREATE TABLE "
                + SavedPostsEntry.TABLE_NAME + " ("
                + SavedPostsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SavedPostsEntry.COLUMN_POST_TITLE + " TEXT NOT NULL, "
                + SavedPostsEntry.COLUMN_POST_CONTENT + " TEXT"
                + ");";

        db.execSQL(SQL_CREATE_SAVED_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // TODO : Modify this so that user data is not lost when database schema is changed
        db.execSQL("DROP TABLE IF EXISTS " + SavedPostsEntry.TABLE_NAME);
        onCreate(db);
    }
}
