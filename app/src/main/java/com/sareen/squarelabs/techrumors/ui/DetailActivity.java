package com.sareen.squarelabs.techrumors.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.sareen.squarelabs.techrumors.HTMLParser.HtmlRemoteImageGetter;
import com.sareen.squarelabs.techrumors.HTMLParser.HtmlTextView;
import com.sareen.squarelabs.techrumors.R;
import com.sareen.squarelabs.techrumors.Utility.Config;
import com.sareen.squarelabs.techrumors.Utility.TRDetail;
import com.sareen.squarelabs.techrumors.Utility.Utility;
import com.sareen.squarelabs.techrumors.data.TechRumorsContract.SavedPostsEntry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>
{

    private static final int DETAIL_SAVED_LOADER = 801;
    private static final int RECOVERY_REQUEST = 1;


    private static final String LOG_TAG = DetailActivity.class.getSimpleName();


    // View used in detail activity
    private HtmlTextView detailText;
    private TextView detailTitle;
    private ProgressDialog progressDialog;
    private TextView detailAuthor;
    private TextView detailDate;

    private String youtube_video_url;
    private String post_title;
    private String post_author;
    private String post_date;
    private String post_url;
    private String post_category;
    private String post_content;
    private long post_id;

    private int calling_code;


    // This list temporarily store the bitmap,
    // for if user asks for storing post in saved articles
    private static ArrayList<Bitmap> detailBitmapList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailBitmapList = null;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0f);
        initializeViews();
        Intent intent = getIntent();
        calling_code = intent.getIntExtra(Utility.CALLER_ACTIVITY, -1012);
        if(calling_code == Utility.CALLER_MAIN_ACTIVITY)
        {
            // Calling activity was main
            setUpFromMain();
        }
        else if(calling_code == Utility.CALLER_SAVED_ACTIVITY)
        {
            // Calling activity was saved
            long _id = intent.getLongExtra(Utility.POST_DB_ID, 0L);
            setupFromSaved(_id);
        }
        else
        {
            // TODO Handle error if detail activity
            // was somehow called by some other unknown activity
        }

    }

    // This method is called when only id of post
    // saved in database is sent from saved activity
    // We query the data base and fill the view
    // using the cursor loader
    private void setupFromSaved(long _id)
    {
        setTitle(getString(R.string.app_name));
        if(_id != 0L)
        {
            getSupportLoaderManager()
                    .initLoader(DETAIL_SAVED_LOADER, null, this);
        }
        else
        {
            //TODO handle if the post db id is 0
            // show some error message
        }

    }


    /*Loader call backs for when data is loaded from the database*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection = new String[]
                {
                        SavedPostsEntry._ID,
                        SavedPostsEntry.COLUMN_POST_ID,
                        SavedPostsEntry.COLUMN_POST_TITLE,
                        SavedPostsEntry.COLUMN_POST_CONTENT,
                        SavedPostsEntry.COLUMN_POST_AUTHOR,
                        SavedPostsEntry.COLUMN_POST_DATE_TIME,
                        SavedPostsEntry.COLUMN_POST_IMAGES
                };
        long _id = getIntent().getLongExtra(Utility.POST_DB_ID, 0L);
        String selection = SavedPostsEntry.TABLE_NAME + "." +
                SavedPostsEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(_id)};
        return new CursorLoader
                (
                        this,
                        SavedPostsEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(data.moveToFirst())
        {
            post_title = data.getString(data.getColumnIndex(SavedPostsEntry.COLUMN_POST_TITLE));
            post_content = data.getString(data.getColumnIndex(SavedPostsEntry.COLUMN_POST_CONTENT));
            post_author = data.getString(data.getColumnIndex(SavedPostsEntry.COLUMN_POST_AUTHOR));
            post_date = data.getString(data.getColumnIndex(SavedPostsEntry.COLUMN_POST_DATE_TIME));

            detailTitle.setText(post_title);
            detailAuthor.setText(post_date);
            detailDate.setText(post_date);

            ParseHTMLTask task = new ParseHTMLTask();
            task.execute(post_content);

        }
        else
        {
            //ToDo: no data is returned, show some error message
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }
    // This method is called when data is sent
    // from main activity in html form
    // We set the title, date and author's name
    // and then parse html sent from main activity
    private void setUpFromMain()
    {
        long postId = getIntent().getLongExtra(Utility.POST_ID, 0);

        if (postId != 0)
        {
            Intent intent = getIntent();
            post_id = postId;
            String content = intent.getStringExtra(Utility.POST_CONTENT);
            String title = intent.getStringExtra(Utility.POST_TITLE);
            String author_name = intent.getStringExtra(Utility.POST_AUTHOR);
            String date = intent.getStringExtra(Utility.POST_DATE);
            String url = intent.getStringExtra(Utility.POST_URL);
            String cat = intent.getStringExtra(Utility.POST_CATEGORY);
            if (title != null) {
                post_title = title;
            } else {
                post_title = "Title Not Found  :(";
            }
            if (author_name != null) {
                post_author = author_name;
            } else {
                post_author = "James";
            }
            if (date != null) {
                post_date = date;
            } else {
                post_date = "";
            }
            if (content != null) {
                post_content = content;
                ParseHTMLTask parseJSONTask = new ParseHTMLTask();
                parseJSONTask.execute(post_content);
            }
            if (url != null) {
                post_url = url;
            } else {
                post_url = "Error finding post";
            }
            if (cat != null) {
                post_category = cat;
            } else {
                post_category = "home";
            }
            setActivityTitle();
        }
    }

    private void setActivityTitle() {
        String activityTitle;
        if(post_category != null)
        {
            if(post_category.equals("home"))
            {
                activityTitle = "Top Stories";
            }
            else if(post_category.equals("gaming-news"))
            {
                activityTitle = "Gaming News";
            }
            else if(post_category.equals("mobile-news"))
            {
                activityTitle = "Mobile News ";
            }
            else if(post_category.equals("tech-news"))
            {
                activityTitle = "Tech News";
            }
            else if(post_category.equals("tech-rumors"))
            {
                activityTitle = "Tech Rumors";
            }
            else if(post_category.equals("tech-videos"))
            {
                activityTitle = "Tech Videos";
            }
            else
            {
                activityTitle = "Tech Rumors";
            }
        }
        else
        {
            activityTitle = "Tech Rumors";
        }

        setTitle(activityTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId)
        {
            case R.id.action_save:

                Toast.makeText(this,
                        "This article has been added to Saved Articles",
                        Toast.LENGTH_SHORT).show();
                savePost();
                return true;
            case R.id.action_share_detail:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, post_url);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    This method save the post in the database
    private void savePost()
    {
        // storing images in file and getting paths
        ArrayList<String> bitmapsPathList = saveBitmaps();
        String bitmapsPathStr = convertArrayListToString(bitmapsPathList);
        ContentValues values = new ContentValues();
        values.put(SavedPostsEntry.COLUMN_POST_ID, post_id);
        values.put(SavedPostsEntry.COLUMN_POST_TITLE, post_title);
        values.put(SavedPostsEntry.COLUMN_POST_CONTENT, post_content);
        values.put(SavedPostsEntry.COLUMN_POST_AUTHOR, post_author);
        values.put(SavedPostsEntry.COLUMN_POST_DATE_TIME, post_date);
        values.put(SavedPostsEntry.COLUMN_POST_IMAGES, bitmapsPathStr);
        getContentResolver().insert(SavedPostsEntry.CONTENT_URI, values);
    }

    private String convertArrayListToString(ArrayList<String> pathList)
    {
        String seperator = "_,_";
        StringBuilder strBuilder = new StringBuilder("");
        for(int i=0; i<pathList.size(); i++)
        {
            strBuilder.append(pathList.get(i));
            if(i < pathList.size() - 1)
            {
                strBuilder.append(seperator);
            }
        }

        return strBuilder.toString();

    }

    private String[] convertStringToArray(String str)
    {
        String seperator = "_,_";
        String[] arr = str.split(seperator);
        return arr;
    }

    private ArrayList<String> saveBitmaps()
    {
        ArrayList<String> pathList = new ArrayList<String>();
        String fileName;
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File imageDir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        for(int i=0; i <detailBitmapList.size(); i++)
        {
            fileName = Long.toString(post_id) + Integer.toString(i + 1);
            File imagePath = new File(imageDir, fileName + ".jpg");
            FileOutputStream out = null;
            try
            {

                out = new FileOutputStream(imagePath);
                detailBitmapList.get(i)
                        .compress(Bitmap.CompressFormat.JPEG, 100, out);
                pathList.add(fileName);

                Log.d(LOG_TAG, "pathListSize: " + pathList.size());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                    try
                    {
                        if(out != null)
                        {
                            out.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
            }
        }
        Log.d(LOG_TAG, "fileName size: " + pathList.size());
        return pathList;

    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail, menu);
        return true;
    }

    private void initializeViews()
    {
        detailText = (HtmlTextView)findViewById(R.id.detail_text);
        detailDate = (TextView)findViewById(R.id.detail_post_date);
        detailAuthor = (TextView)findViewById(R.id.detail_author_name) ;
        detailTitle = (TextView)findViewById(R.id.detail_title);
    }




    public static String extractYTId(String ytUrl) {
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


    public class ParseHTMLTask extends AsyncTask<String, Void, TRDetail>
    {
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(DetailActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(TRDetail result)
        {
            if(progressDialog != null)
            {
                progressDialog.dismiss();
            }
            if(result != null)
            {
                //set post title
                detailTitle.setText(post_title);

                //set post author name
                detailAuthor.setText("By  " + post_author + "  |  ");

                //set post date
                detailDate.setText(post_date);

                result.content = result.content.replace
                        ("(adsbygoogle = window.adsbygoogle || []).push({});", "");

                detailText.setHtml(result.content,
                        new HtmlRemoteImageGetter(detailText, DetailActivity.this));

                // check if post has video url
                if (!result.video_url.equals("") && result.video_url.contains("www.youtube.com"))
                {
                    Log.d(LOG_TAG, "VIDEO VISIBLE" + result.video_url);
                    youtube_video_url = result.video_url;
                    // post has video url.
                    // make video view visible
                    TRYoutubeFragment ytFragment =
                            TRYoutubeFragment.newInstance(youtube_video_url);
                    ytFragment.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener()
                    {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer player, boolean wasRestored)
                        {
                            if (!wasRestored) {
                                player.cueVideo(extractYTId(youtube_video_url));
                            }
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult errorReason)
                        {
                            if (errorReason.isUserRecoverableError())
                            {
                                errorReason.getErrorDialog
                                        (DetailActivity.this, RECOVERY_REQUEST).show();
                            }
                            else
                            {
                                String error = "Error";
                                Toast.makeText(DetailActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.yt_container, ytFragment, "YT_FRAGMENT").commit();

                }
            }
            else
            {
                // result in null
                // TODO handle when no result is returned
                // again query data
            }
        }

        @Override
        protected TRDetail doInBackground(String... params)
        {
            String postHTMLStr = params[0];
            return parseHTML(postHTMLStr);
        }

        // Method used to parse JSON data fetched from server
        private TRDetail parseHTML(String content)
        {
            Document doc = Jsoup.parse(content);
            Elements iframe = doc.select("iframe");
            TRDetail trDetail = new TRDetail();
            trDetail.content = content;
            trDetail.video_url = iframe.attr("src");
            return trDetail;
        }

    }

    public static void addBitmapToDetailList(Bitmap bitmap)
    {
        if(detailBitmapList == null)
        {
            detailBitmapList = new ArrayList<Bitmap>();
        }
        Log.d(LOG_TAG, "addBitmapTopDetailList");
        detailBitmapList.add(bitmap);
        Log.d(LOG_TAG, "" + detailBitmapList.size());
    }
}
