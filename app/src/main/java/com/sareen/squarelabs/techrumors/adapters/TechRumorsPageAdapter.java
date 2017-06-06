package com.sareen.squarelabs.techrumors.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sareen.squarelabs.techrumors.ui.TechNewsFragment;

/**
 * Created by Ashish on 27-08-2016.
 */
public class TechRumorsPageAdapter extends FragmentPagerAdapter
{

    public TechRumorsPageAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Top Stories";
            case 1:
                return "Gaming";
            case 2:
                return "Mobile";
            case 3:
                return "News";
            case 4:
                return "Mobile Videos";
            case 5:
                return "Tech Videos";
            default:
                return "Top Stories";

        }
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
               return TechNewsFragment.newInstance("home");
            case 1:
                return TechNewsFragment.newInstance("gaming-news");
            case 2:
                return TechNewsFragment.newInstance("mobile-news");
            case 3:
                return TechNewsFragment.newInstance("tech-news");
            case 4:
                return TechNewsFragment.newInstance("mobile-videos");
            case 5:
                return TechNewsFragment.newInstance("tech-videos");
            default:
                return TechNewsFragment.newInstance("home");

        }
    }

    @Override
    public int getCount()
    {
        return 6;
    }
}
