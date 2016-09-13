package com.sareen.squarelabs.techrumors.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sareen.squarelabs.techrumors.R;
import com.sareen.squarelabs.techrumors.Utility.EndlessScrollListener;
import com.sareen.squarelabs.techrumors.Utility.MyTechNews;
import com.sareen.squarelabs.techrumors.Utility.Utility;
import com.sareen.squarelabs.techrumors.adapters.TechNewsAdapter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ashish on 28-07-2016.
 */
public class TechNewsFragment extends Fragment
{
    private static final String LOG_TAG = TechNewsFragment.class.getSimpleName();
    private TechNewsAdapter mNewsAdapter;
    ArrayList<MyTechNews> mNewsList;

    private boolean isFirstTime = true;
    private boolean isLoadNextPage = false;
    private int page;
    private String category;
    private View footerView;
    private ListView mTechNewsListView;
    private StringRequest mStringRequest;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean showNetworkError = true;
    private boolean isRecentData;
    private boolean mGetMoreRecentPosts;
    private int recentPage;
    private int refreshIterationCount;
    private int normalIterationCount = 1;

    private long firstPostId = 0;
    private long newFirstPostId = 0;

    public TechNewsFragment()
    {
        setHasOptionsMenu(true);
    }

    public static TechNewsFragment newInstance(String cat)
    {
        TechNewsFragment fragment = new TechNewsFragment();
        Bundle args = new Bundle();
        args.putString(Utility.CATEGORY, cat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_tech_news,
                null, false);


        // initialize swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout);

        mTechNewsListView = (ListView)
                rootView.findViewById(R.id.listview_tech_news);


        /*Code for adding footer view*/
        footerView = inflater.inflate(R.layout.loading_footer_view,null, false);
        mTechNewsListView.addFooterView(footerView);
        // setting up the empty view
        mTechNewsListView.setEmptyView(rootView.findViewById(R.id.empty_view));

        mNewsList = new ArrayList<MyTechNews>();
        // TODO: Set up listview adapter
        mNewsAdapter = new TechNewsAdapter(getActivity(), mNewsList);

        mTechNewsListView.setAdapter(mNewsAdapter);

        // setting the onclick listener of listview
        mTechNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Open detail activity
                MyTechNews item = (MyTechNews)mNewsAdapter.getItem(position);
                //TODO: Do the below stuff usng a single object using parceable
                long post_id = item.post_id;
                String content = item.post_content;
                String title = item.title_text;
                String author = item.post_author;
                String date = item.post_date;
                String url = item.post_url;
                String cat = item.post_category;
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Utility.POST_ID, post_id);
                intent.putExtra(Utility.POST_CONTENT, content);
                intent.putExtra(Utility.POST_TITLE, title);
                intent.putExtra(Utility.POST_AUTHOR, author);
                intent.putExtra(Utility.POST_DATE, date);
                intent.putExtra(Utility.POST_URL, url);
                intent.putExtra(Utility.POST_CATEGORY, cat);
                intent.putExtra(Utility.CALLER_ACTIVITY, Utility.CALLER_MAIN_ACTIVITY);
                startActivity(intent);
            }
        });

        mTechNewsListView.setOnScrollListener(new EndlessScrollListener()
        {
            @Override
            public boolean onLoadMore(int pg, int totalItemsCount)
            {
                /*In this method updateTechNewsData will be called only
                if either the loading has failed or next page has been requested.
                This will ensure if there are any bugs because of which this method
                 is called twice for one scroll, data is only updated once.*/


                if(!isFirstTime)    // try to load next page only if data is loaded first time already
                {
                    if (isLoadNextPage)
                    {

                        page++;
                        updateTechNewsData();
                    }
                    else if(EndlessScrollListener.loadingFailed)
                    {
                        updateTechNewsData();
                    }
                }
                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                // Refresh page only if isFirstTime is done.
                if(!isFirstTime)
                {
                    Log.d(LOG_TAG, "onRefresh");
                    mGetMoreRecentPosts = true;
                    recentPage = 0;
                    refreshIterationCount = 0;
                    loadRecentPosts();
                }

            }
        });

        Bundle args = getArguments();
        category = args.getString(Utility.CATEGORY);
        if(category == null)
        {
            category = "home";
        }
        updateTechNewsData();
        return rootView;
    }

    private void loadRecentPosts()
    {
        if(mGetMoreRecentPosts)
        {
            refreshIterationCount++;
            recentPage++;
            isRecentData = true;
            updateTechNewsData();
        }
        else
        {
            isRecentData = false;
        }
    }


    public void updateTechNewsData()
    {
        isLoadNextPage = false;
        fetchNewsData();
    }

    private void fetchNewsData()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
        {
            Log.d(LOG_TAG, "FirstTime: " + isFirstTime);
            // Network is available. So fetch data
            // Set it to true so that error is shown next time network is not available
            showNetworkError = true;

            // This checks whether the user refreshed the view or
            // new category selected by user
            if(isFirstTime && !isRecentData)
            {
                // clearing the adapter and setting the page = 1
                page = 1;
                mNewsList.clear();
                mNewsAdapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "Adapter Cleared");


                // Stop Volley request
                if(mStringRequest != null && !mStringRequest.isCanceled())
                {
                    mStringRequest.cancel();
                    mStringRequest = null;
                    Log.d(LOG_TAG, "Volley canceled");
                }
            }
            if(category == null || category.equals(""))
            {
                category = "home";
            }

            // Setting up the argument for url query
            int posts_count = 10;
            String include="title,id,thumbnail,content,categories,author,date,url";
            String order_by = "date";
            String order = "desc";

            final String PAGE_PARAM="page";
            final String COUNT_PARAM="count";
            final String INCLUDE_PARAM="include";
            final String CATEGORY_PARAM="slug";
            final String ORDER_BY_PARAM = "order_by";
            final String ORDER_PARAM = "order";
            Uri builtUri;
            int currentPage = (isRecentData) ? recentPage : page;
            if (category.equals("home"))
            {
                builtUri = Uri.parse("http://www.techrumors.org/api/get_posts").buildUpon()
                        .appendQueryParameter(PAGE_PARAM, Integer.toString(currentPage))
                        .appendQueryParameter(COUNT_PARAM, Integer.toString(posts_count))
                        .appendQueryParameter(INCLUDE_PARAM, include)
                        .appendQueryParameter(ORDER_BY_PARAM, order_by)
                        .appendQueryParameter(ORDER_PARAM, order)
                        .build();
            }


            else
            {
                // user has selected some category
                builtUri = Uri.parse("http://www.techrumors.org/api/get_category_posts").buildUpon()
                        .appendQueryParameter(CATEGORY_PARAM, category)
                        .appendQueryParameter(PAGE_PARAM, Integer.toString(currentPage))
                        .appendQueryParameter(COUNT_PARAM, Integer.toString(posts_count))
                        .appendQueryParameter(INCLUDE_PARAM, include)
                        .appendQueryParameter(ORDER_BY_PARAM, order_by)
                        .appendQueryParameter(ORDER_PARAM, order)
                        .build();
            }


            Log.v(LOG_TAG, builtUri.toString());

            // Create the HTTP connection and set request methods

            try
            {
                URL url = new URL(builtUri.toString());
                mStringRequest = new StringRequest
                        (url.toString(), new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                String newsJsonStr = response; // contains returned json response
                                parseJson(newsJsonStr);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                // check if swipe refreshing view is refreshing
                                // if true stop it
                                if(swipeRefreshLayout.isRefreshing())
                                {
                                    swipeRefreshLayout.setRefreshing(false);
                                }

                                Log.d(LOG_TAG, "onErrorResponse");
                                 Toast.makeText(getActivity(),
                                        "Error fetching data...", Toast.LENGTH_SHORT).show();
                                Log.e(LOG_TAG, error.toString());
                                retry();
                            }
                        });
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(mStringRequest);
            }
            catch(MalformedURLException e)
            {
                Log.e(LOG_TAG, "Invalid URL");
                retry();
            }
        }
        else
        {
            if(isFirstTime)
            {
                mNewsList.clear();
                mNewsAdapter.notifyDataSetChanged();
            }
            if(showNetworkError)
            {
                Toast.makeText
                        (getActivity(), "Network not available..." , Toast.LENGTH_SHORT).show();
                showNetworkError = true;
            }

            // check if swipe refreshing view is refreshing
            // if true stop it
            if(swipeRefreshLayout.isRefreshing())
            {
                swipeRefreshLayout.setRefreshing(false);
            }

           if(!isRecentData)
           {
               retry();
           }

        }
    }



    public void retry()
    {
        // TODO create option for retry
        // Data fetching couldn't be completed

        // Setting the setLoadingFailed to true
        // so that endless scroll listener can detect
        // that loading is failed and then retries to query data

        EndlessScrollListener.setLoadingFailed();

        Log.d(LOG_TAG, "onRetry");

        //isLoadNextPage set false so that same page is fetched on retry

        if(!isRecentData)
        {
            isLoadNextPage = false;
        }
        //TODO remove progress dialog and display a reload button

        if(isFirstTime)
        {
            // show reload button
        }
    }

    private void parseJson(String newsJsonStr)
    {
        ParseJsonTask parseJsonTask = new ParseJsonTask();
        parseJsonTask.execute(newsJsonStr);
    }


    public class ParseJsonTask extends AsyncTask<String, Void, Void>
    {
        private final String LOG_TAG = ParseJsonTask.class.getSimpleName();

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.d(LOG_TAG, "onPostExecute");

            // Adapter is notified of new items
            if(!isRecentData)
            {
                mNewsAdapter.notifyDataSetChanged();
            }
            else if(isRecentData && !mGetMoreRecentPosts)
            {
                mNewsAdapter.notifyDataSetChanged();
                // check if swipe refreshing view is refreshing
                // if true stop it
                if(swipeRefreshLayout.isRefreshing())
                {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }


            // At this step it is sure that the current
            // page data has been fetched and shown to user.
            // so setting isLoadNextPage to true
            isLoadNextPage = true;

            // Check if this was first time loading
            // if true set isFirstTime to false
            if(isFirstTime)
            {
                isFirstTime = false;
            }


            if(isRecentData)
            {
                loadRecentPosts();
            }

        }

        @Override
        protected Void doInBackground(String... params)
        {
            String newsJsonStr = params[0];
            int currentPage = (isRecentData) ? recentPage : page;
            Log.d(LOG_TAG,"parseJOSN " + category + " " + currentPage);
            String TR_POSTS = "posts";
            String TR_TITLE = "title";
            String TR_THUMBNAIL = "thumbnail";
            String TR_POST_ID = "id";
            String TR_POST_CONTENT = "content";
            String TR_POST_AUTHOR = "author";
            String TR_POST_AUTHOR_NAME = "name";
            String TR_POST_DATE = "date";
            String TR_POST_URL = "url";
            try
            {
                JSONObject newsJson = new JSONObject(newsJsonStr);
                JSONArray postsArray = newsJson.getJSONArray(TR_POSTS);

                if(isRecentData)
                {

                    for (int i = 0; i < postsArray.length(); i++)
                    {
                        JSONObject post = postsArray.getJSONObject(i);
                        long id = post.getLong(TR_POST_ID);
                        Log.d(LOG_TAG, "Comparing " + id + " - " + firstPostId);
                        if(id > firstPostId)
                        {
                            Log.d(LOG_TAG, "Recent Post id: " + id);
                            if((refreshIterationCount == 1) && (i == 0))
                            {
                                newFirstPostId = id;
                            }
                            String title = StringEscapeUtils.unescapeXml(post.getString(TR_TITLE));
                            String image = post.optString(TR_THUMBNAIL);
                            if (image.equals(""))
                            {
                                image = "null";
                            }

                            String post_url = post.getString("url");

                            String content = post.getString(TR_POST_CONTENT);

                            JSONObject author_json = post.getJSONObject(TR_POST_AUTHOR);
                            String author_name = author_json.getString(TR_POST_AUTHOR_NAME);

                            String date = post.getString(TR_POST_DATE);

                            JSONArray catArray = post.getJSONArray("categories");

                            JSONObject catObject = catArray.getJSONObject(0);

                            String slug = catObject.getString("slug");
                            Log.d(LOG_TAG, slug);

                            MyTechNews myTechNews = new MyTechNews
                                    (title, image, id, content, author_name, date, post_url,category);
                            mNewsList.add(0, myTechNews);
                        }
                        else
                        {
                            // the id of post is smaller or lesser than firstPostId
                            // make firstPostId equal to highest id of newer posts
                            if(newFirstPostId != 0)
                            {
                                // it means the above if statement executed at least once
                                firstPostId = newFirstPostId;
                            }
                            mGetMoreRecentPosts = false;
                            break;
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < postsArray.length(); i++)
                    {
                        JSONObject post = postsArray.getJSONObject(i);
                        String title = StringEscapeUtils.unescapeXml(post.getString(TR_TITLE));
                        String image = post.optString(TR_THUMBNAIL);
                        if (image.equals("")) {
                            image = "null";
                        }
                        long id = post.getLong(TR_POST_ID);
                        String content = post.getString(TR_POST_CONTENT);

                        JSONObject author_json = post.getJSONObject(TR_POST_AUTHOR);
                        String author_name = author_json.getString(TR_POST_AUTHOR_NAME);

                        String post_url = post.getString("url");

                        String date = post.getString(TR_POST_DATE);

                        JSONArray catArray = post.getJSONArray("categories");

                        JSONObject catObject = catArray.getJSONObject(0);

                        String slug = catObject.getString("slug");
                        Log.d(LOG_TAG, slug);

                        MyTechNews myTechNews = new MyTechNews
                                (title, image, id, content, author_name, date, post_url, category);
                        mNewsList.add(myTechNews);

                        //TODO Later refractor the code to use data instead of id
                        /*This code is to get id of the first post*/
                        if (normalIterationCount == 1)
                        {
                            Log.d(LOG_TAG, normalIterationCount + "");
                            firstPostId = id;
                            normalIterationCount++;
                        }
                    }
                }
            }
            catch (JSONException e)
            {
                Log.e(LOG_TAG, e.getMessage());
            }
            return null;
        }
    }



    public void setFirstTime(boolean firstTime)
    {
        isFirstTime = firstTime;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        return super.onOptionsItemSelected(item);
    }

    private void loadMore() {
        Log.i(LOG_TAG, "loading more data");
        if(isLoadNextPage)
        {
            page++;
        }
        updateTechNewsData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
//        inflater.inflate(R.menu.fragment_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    public void resetNormalIterationCount()
    {
        normalIterationCount = 1;
    }

}

