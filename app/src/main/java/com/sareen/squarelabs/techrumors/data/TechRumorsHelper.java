package com.sareen.squarelabs.techrumors.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ashish on 11-09-2016.
 */
public class TechRumorsHelper extends SQLiteOpenHelper {

    // name of the file in which database is stored
    public static final String DATABASE_NAME = "techrumors.db";

    // Version of database
    // if schema of database is changed
    // then this needs to be increased.
    public static final int DATABASE_VERSION = 1;

    public TechRumorsHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
