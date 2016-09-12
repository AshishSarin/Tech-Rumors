package com.sareen.squarelabs.techrumors.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sareen.squarelabs.techrumors.R;
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
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        mSavedListView = (ListView)findViewById(R.id.listview_saved_articles);
        mSavedListView.setEmptyView(findViewById(R.id.empty_view_saved));

        // setting up the adapter for list view
        mSavedAdapter = new SavedPostsAdapter(this);
        mSavedListView.setAdapter(mSavedAdapter);

        setListViewOnItemClickListener();

        getSupportLoaderManager()
                .initLoader(SAVED_POSTS_LOADER, null, this);
    }

    private void setListViewOnItemClickListener()
    {
        mSavedListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader
                (
                        this,
                        SavedPostsEntry.CONTENT_URI,
                        saveListProjection,
                        null,
                        null,
                        null
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mSavedAdapter.swapCursor(null);
    }
}
