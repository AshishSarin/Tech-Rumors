package com.sareen.squarelabs.techrumors.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sareen.squarelabs.techrumors.Utility.MyTechNews;
import com.sareen.squarelabs.techrumors.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashish on 28-07-2016.
 */
public class TechNewsAdapter extends BaseAdapter
{
    private ArrayList<MyTechNews> newsArrayList;
    private Context mContext;

    public TechNewsAdapter(Context context, List<MyTechNews> mNewList)
    {
        mContext = context;
        newsArrayList = (ArrayList<MyTechNews>)mNewList;
    }

    @Override
    public int getCount()
    {
        return newsArrayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return newsArrayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_tech_news, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        MyTechNews item = newsArrayList.get(position);
        if(item!= null)
        {
            viewHolder.title_text.setText(item.title_text);
            Picasso.with(mContext)
                    .load(item.title_image_url)
                    .placeholder(R.drawable.tr_placeholder_error)
                    .error(R.drawable.tr_placeholder_error)
                    .resizeDimen(R.dimen.list_item_title_image_dimen, R.dimen.list_item_title_image_dimen)
                    .centerInside()
                    .into(viewHolder.title_image);
        }
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView title_text;
        public ImageView title_image;

        public ViewHolder(View v)
        {
            title_text = (TextView)v.findViewById(R.id.list_item_title_text);
            title_image = (ImageView)v.findViewById(R.id.list_item_title_image);
        }
    }



}


