package com.sareen.squarelabs.techrumors.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sareen.squarelabs.techrumors.R;
import com.sareen.squarelabs.techrumors.data.TechRumorsContract.SavedPostsEntry;

import java.io.File;

/**
 * Created by Ashish on 11-09-2016.
 */
public class SavedPostsAdapter extends CursorAdapter
{
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_tech_news, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.title_text.setText(cursor.getString
                (cursor.getColumnIndex(SavedPostsEntry.COLUMN_POST_TITLE)));
        Bitmap bitmap = getBitmap(cursor.getString
                (cursor.getColumnIndex(SavedPostsEntry.COLUMN_POST_TITLE_IMAGE)));
        viewHolder.title_image.setImageBitmap(bitmap);
    }

    private Bitmap getBitmap(String imageName)
    {
        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        File imageDir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String fileName = imageName;
        File imagePath = new File(imageDir, fileName + ".jpg");
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath.toString());
        return bitmap;
    }

    private Context mContext;

    public SavedPostsAdapter(Context context)
    {
        super(context, null, 0);
        mContext = context;
    }

    private static class ViewHolder
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
