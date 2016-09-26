package com.sareen.squarelabs.techrumors.Utility;

import android.graphics.Bitmap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ashish on 03-08-2016.
 */
public class Utility
{
    public static final String CATEGORY = "category";
    public static final String POST_ID = "post_id";
    public static final String POST_DB_ID = "post_db_id";
    public static final String POST_CONTENT = "post_content";
    public static final String POST_TITLE = "post_title";
    public static final String POST_AUTHOR = "post_author";
    public static final String POST_DATE = "post_date";
    public static final String POST_URL = "post_url";
    public static final String POST_CATEGORY = "post_cat";

    public static Bitmap bitmap;


    // These codes are passed to detail activity
    // to determine from where it being called.
    public static final int CALLER_MAIN_ACTIVITY = 5652; // code for MainActivity
    public static final int CALLER_SAVED_ACTIVITY = 5353; // code for SavedActivity

    // Constant for calling activity
    public static final String CALLER_ACTIVITY = "caller_activity";

    public static String extractYTId(String ytUrl)
    {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }

}
