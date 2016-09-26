/*
 * Copyright (C) 2014-2016 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2014 drawk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sareen.squarelabs.techrumors.HTMLParser;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.sareen.squarelabs.techrumors.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Copied from http://stackoverflow.com/a/22298833
 */
public class HtmlLocalImageGetter implements Html.ImageGetter
{
    TextView container;
    ArrayList<String> imagePathList;
    private int count = 0;

    public HtmlLocalImageGetter(TextView textView, ArrayList<String> imagePathList)
    {
        count = 0;
        this.container = textView;
        this.imagePathList = imagePathList;
    }

    public HtmlLocalImageGetter(TextView textView)
    {
        this.container = textView;
    }



    private boolean matchParentWidth = true;

    private float getScale(Drawable drawable)
    {
        if (!matchParentWidth || container == null) {
            return 1f;
        }
        float maxWidth = container.getWidth();
        float originalDrawableWidth = drawable.getIntrinsicWidth();
        return maxWidth / originalDrawableWidth;
    }

    public Drawable getDrawable(String source)
    {
        Context context = container.getContext();
        Drawable d;
        if((imagePathList!= null) && (count < imagePathList.size()))
        {
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            File imageDir = cw.getDir("imageDir", Context.MODE_PRIVATE);
            String fileName = imagePathList.get(count);
            File imagePath = new File(imageDir, fileName + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath.toString());
            d = new BitmapDrawable(context.getResources(), bitmap);
            float scale = getScale(d);
            d.setBounds(0, 0, (int) (d.getIntrinsicWidth() * scale), (int) (d.getIntrinsicHeight() * scale));
            Log.e("imageListSize: " , imagePathList.size()+"");
            Log.e("COUNT: " , count+"");
            Log.e("ImagePath: ", imagePath.toString());

        }

        else
        {
            d = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }

        count++;
        return d;
        /*Context context = container.getContext();
        int id = context.getResources().getIdentifier(source, "drawable", context.getPackageName());

        if (id == 0)
        {
            // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
            id = context.getResources().getIdentifier(source, "drawable", "android");
        }

        if (id == 0)
        {
            // prevent a crash if the resource still can't be found
            Log.e(HtmlTextView.TAG, "source could not be found: " + source);
            return null;
        }
        else
        {
            Drawable d = context.getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }*/
    }



}