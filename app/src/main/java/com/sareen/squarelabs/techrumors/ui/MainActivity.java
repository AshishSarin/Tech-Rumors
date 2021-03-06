package com.sareen.squarelabs.techrumors.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sareen.squarelabs.techrumors.R;
import com.sareen.squarelabs.techrumors.adapters.TechRumorsPageAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private TechRumorsPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private View navHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MobileAds.initialize
                (getApplicationContext(),
                        "ca-app-pub-2077732222500987~6301066550");

        AdView mAdView = (AdView) findViewById(R.id.adView_main);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        mPageAdapter = new TechRumorsPageAdapter(getSupportFragmentManager());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =
                (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        navHeader = navigationView.getHeaderView(0);



        mViewPager = (ViewPager)findViewById(R.id.main_pager);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                /*int themeColor;
                switch (position)
                {
                    case 0:
                        themeColor = ContextCompat.getColor(MainActivity.this, R.color.colorPrimary);
                        break;
                    case 1:
                        themeColor = ContextCompat.getColor(MainActivity.this, R.color.teal);
                        break;
                    case 2:
                        themeColor = ContextCompat.getColor(MainActivity.this, R.color.red);
                        break;
                    case 3:
                        themeColor = ContextCompat.getColor(MainActivity.this, R.color.brown);
                        break;
                    case 4:
                        themeColor = ContextCompat.getColor(MainActivity.this, R.color.purple);
                        break;
                    case 5:
                        themeColor = ContextCompat.getColor(MainActivity.this, R.color.light_blue);
                        break;
                    default:
                        themeColor = ContextCompat.getColor(MainActivity.this, R.color.colorPrimary);


                }
                toolbar.setBackgroundColor(themeColor);
                tabLayout.setBackgroundColor(themeColor);
                navHeader.setBackgroundColor(themeColor);*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout)findViewById(R.id.main_tablayout);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_saved_articles:
                startActivity(new Intent(this, SavedActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
}
