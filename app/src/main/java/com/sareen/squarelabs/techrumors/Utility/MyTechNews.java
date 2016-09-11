package com.sareen.squarelabs.techrumors.Utility;


public class MyTechNews
{
    public String title_text;
    public String title_image_url;
    public long post_id;
    public String post_content;
    public String post_author;
    public String post_date;
    public String post_url;
    public String post_category;

    public MyTechNews(String title, String image, long id,
                      String content, String author, String date, String url,
                      String category)
    {
        title_text = title;
        title_image_url = image;
        post_id = id;
        post_content = content;
        post_author = author;
        post_date = date;
        post_url = url;
        post_category = category;
    }

    @Override
    public String toString()
    {
        return title_text;
    }
}
