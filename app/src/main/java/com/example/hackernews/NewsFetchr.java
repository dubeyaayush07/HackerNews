package com.example.hackernews;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsFetchr {

    private static final String TAG = "NewsFetchr";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<NewsItem> fetchItems(String url) {

        List<NewsItem> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONArray jsonArray = new JSONArray(jsonString);
            parseItems(items, jsonArray);


        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);

        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);

        }

        return items;
    }

    private void parseItems(List<NewsItem> items, JSONArray jsonArray) throws IOException, JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            String id = jsonArray.getString(i);
            NewsItem item = new NewsItem();
            item.setId(id);
            items.add(item);
        }

    }

    public NewsItem fetchNewsItem(String url) {
        NewsItem newsItem = new NewsItem();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);

            newsItem.setTitle(jsonBody.getString("title"));
            newsItem.setId(jsonBody.getString("id"));
            newsItem.setAuthor(jsonBody.getString("by"));
            newsItem.setScore(jsonBody.getString("score"));
            newsItem.setLink(jsonBody.getString("url"));

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);

        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);

        }

        return newsItem;

    }
}
