package com.sareen.squarelabs.techrumors.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sareen.squarelabs.techrumors.R;

/**
 * Created by Ashish on 11-09-2016.
 */
public class SavedArticleAdapter extends CursorAdapter
{
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {

    }

    public SavedArticleAdapter(Context context)
    {
        super(context, null, 0);
    }

    public static class ViewHolder
    {
        public TextView title_text;
        public ImageView title_image;

        public ViewHolder(View v)
        {
            title_text = (TextView) v.findViewById(R.id.list_item_title_text);
            title_image = (ImageView) v.findViewById(R.id.list_item_title_image);
        }
    }
}
