package com.example.hackernews;

import android.net.Uri;

public class NewsItem {

    private String mTitle;
    private String mId;
    private String mAuthor;
    private String mScore;
    private String mLink;

    private static NewsItem sPlaceholder;

    public static NewsItem getPlaceholder() {
        if (sPlaceholder == null) {
            sPlaceholder = new NewsItem();
            sPlaceholder.setTitle("Loading...");
            sPlaceholder.setId("12345");
            sPlaceholder.setAuthor("author");
            sPlaceholder.setScore("42");
            sPlaceholder.setLink("https://news.ycombinator.com/");
        }

        return sPlaceholder;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getScore() {
        return mScore;
    }

    public void setScore(String score) {
        mScore = score;
    }

    public Uri getLink() {
        return Uri.parse(mLink);
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getUrl() {
        String result = "https://hacker-news.firebaseio.com/v0/item/" + mId + ".json";
        return result;
    }


}
