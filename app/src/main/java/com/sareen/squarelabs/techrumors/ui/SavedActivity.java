package com.sareen.squarelabs.techrumors.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.sareen.squarelabs.techrumors.R;
import com.sareen.squarelabs.techrumors.adapters.SavedArticleAdapter;

public class SavedActivity extends AppCompatActivity {

    ListView mSavedListView;
    SavedArticleAdapter mSavedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        mSavedListView = (ListView)findViewById(R.id.listview_saved_articles);
        mSavedListView.setEmptyView(findViewById(R.id.empty_view_saved));

        // setting up the adapter for list view
        mSavedAdapter = new SavedArticleAdapter(this);
        mSavedListView.setAdapter(mSavedAdapter);
    }
}
