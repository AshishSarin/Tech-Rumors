package com.sareen.squarelabs.techrumors.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sareen.squarelabs.techrumors.R;
import com.sareen.squarelabs.techrumors.Utility.Utility;
import com.sareen.squarelabs.techrumors.adapters.SavedPostsAdapter;
import com.sareen.squarelabs.techrumors.data.TechRumorsContract.SavedPostsEntry;

public class SavedActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>
{


    private static final int SAVED_POSTS_LOADER = -901;
    private ListView mSavedListView;
    private SavedPostsAdapter mSavedAdapter;

    // Columns to retrieve from databse for save posts lists
    private String[] saveListProjection =
            {
                    SavedPostsEntry._ID,
                    SavedPostsEntry.COLUMN_POST_TITLE,
                    SavedPostsEntry.COLUMN_POST_TITLE_IMAGE
            };

    private String sortOrder =
            SavedPostsEntry._ID + " DESC";

    private ProgressBar savedProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        intializeAds();

        savedProgressBar = (ProgressBar)findViewById(R.id.saved_progress_bar);

        mSavedListView = (ListView)findViewById(R.id.listview_saved_articles);
        mSavedListView.setEmptyView(findViewById(R.id.empty_view_saved));

        // setting up the adapter for list view
        mSavedAdapter = new SavedPostsAdapter(this);
        mSavedListView.setAdapter(mSavedAdapter);

        setListViewOnItemClickListener();

        getSupportLoaderManager()
                .initLoader(SAVED_POSTS_LOADER, null, this);


        // remove elevation from action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setElevation(0f);
        }



    }

    private void intializeAds()
    {
        MobileAds.initialize
                (getApplicationContext(),
                        "ca-app-pub-2077732222500987~6301066550");
        AdView mAdView = (AdView) findViewById(R.id.adView_saved);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume()
    {
        getSupportLoaderManager().restartLoader(SAVED_POSTS_LOADER, null, this);
        super.onResume();
    }

    private void setListViewOnItemClickListener()
    {
        mSavedListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(SavedActivity.this, DetailActivity.class);
                Cursor c = mSavedAdapter.getCursor();
                c.moveToPosition(position);
                long post_db_id = c.getLong(c.getColumnIndex(SavedPostsEntry._ID));
                intent.putExtra(Utility.POST_DB_ID, post_db_id);
                intent.putExtra(Utility.CALLER_ACTIVITY, Utility.CALLER_SAVED_ACTIVITY);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        savedProgressBar.setVisibility(View.VISIBLE);
        return new CursorLoader
                (
                        this,
                        SavedPostsEntry.CONTENT_URI,
                        saveListProjection,
                        null,
                        null,
                        sortOrder
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(data != null)
        {
            Log.d("SavedActivity", "null");
        }
        mSavedAdapter.swapCursor(data);
        savedProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mSavedAdapter.swapCursor(null);
    }
}
