package com.sareen.squarelabs.techrumors.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailActivity extends AppCompatActivity
{

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0f);
        initializeViews();

        long id = getIntent().getLongExtra(Utility.POST_ID, 0);
       /* if(id != 0)
        {
            fetchPost(id);
        }*/
        if(id != 0)
        {
            Intent intent  = getIntent();
            String content  = intent.getStringExtra(Utility.POST_CONTENT);
            String title = intent.getStringExtra(Utility.POST_TITLE);
            String author_name = intent.getStringExtra(Utility.POST_AUTHOR);
            String date = intent.getStringExtra(Utility.POST_DATE);
            String url = intent.getStringExtra(Utility.POST_URL);
            String cat = intent.getStringExtra(Utility.POST_CATEGORY);
            if(title != null)
            {
                post_title = title;
            }
            else
            {
                post_title = "Title Not Found  :(";
            }
            if(author_name != null)
            {
                post_author = author_name;
            }
            else
            {
                post_author = "James";
            }
            if(date != null)
            {
                post_date = date;
            }
            else
            {
                post_date = "";
            }
            if(content != null)
            {
                ParseHTMLTask parseJSONTask = new ParseHTMLTask();
                parseJSONTask.execute(content);
            }
            if(url != null)
            {
                post_url = url;
            }
            else
            {
                post_url = "Error finding post";
            }
            if(cat != null)
            {
                post_category = cat;
            }
            else
            {
                post_category = "home";
            }
        }

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
}
