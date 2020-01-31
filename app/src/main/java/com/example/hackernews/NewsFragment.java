package com.example.hackernews;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";
    private static final String NEW_STORIES_URL = "https://hacker-news.firebaseio.com/v0/newstories.json";
    private static final String TOP_STORIES_URL = "https://hacker-news.firebaseio.com/v0/topstories.json";

    private RecyclerView mNewsRecyclerView;
    private List<NewsItem> mItems =  new ArrayList<>();
    private NewsDownloader<NewsHolder> mNewsDownloader;

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        Handler responseHander = new Handler();

        mNewsDownloader = new NewsDownloader<>(responseHander);
        mNewsDownloader.setNewsDownloadListener(
                new NewsDownloader.NewsDownloadListener<NewsHolder>() {
                    @Override
                    public void onNewsDownload(NewsHolder target, NewsItem newsItem) {
                        target.bindNewsItem(newsItem);
                    }
                }
        );
        mNewsDownloader.start();
        mNewsDownloader.getLooper();
        Log.i(TAG, "Background thread started");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        mNewsRecyclerView = v.findViewById(R.id.news_recycler_view);
        mNewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNewsDownloader.quit();
        Log.i(TAG, "Background thread destroyed");

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_news, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_stories:
                NewsPreferences.setStoredQuery(getActivity(), TOP_STORIES_URL);
                updateItems();
                return true;
            case R.id.new_stories:
                NewsPreferences.setStoredQuery(getActivity(), NEW_STORIES_URL);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateItems() {
        String query = NewsPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private void setupAdapter() {
        if (isAdded()) {
            mNewsRecyclerView.setAdapter(new NewsAdapter(mItems));
        }
    }

    private class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDetailTextView;
        private NewsItem mNewsItem;

        public NewsHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.news_title);
            mDetailTextView = (TextView) itemView.findViewById(R.id.news_detail);
            itemView.setOnClickListener(this);
        }

        private void bindNewsItem(NewsItem item) {
            mTitleTextView.setText(item.getTitle());
            String details = getString(R.string.detail_string, item.getScore(), item.getAuthor());
            mDetailTextView.setText(details);
            mNewsItem = item;
        }

        @Override
        public void onClick(View v) {
            Intent i = NewsLinkActivity.newIntent(getActivity(), mNewsItem.getLink());
            startActivity(i);

        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {

        private List<NewsItem> mNewsItems;

        public NewsAdapter(List<NewsItem> newsItems) {
            mNewsItems = newsItems;
        }

        @NonNull
        @Override
        public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item, parent, false);
            return new NewsHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
            NewsItem newsItem = mNewsItems.get(position);
            holder.bindNewsItem(NewsItem.getPlaceholder());
            mNewsDownloader.queueThumbnail(holder, newsItem.getUrl());

        }

        @Override
        public int getItemCount() {
            return mNewsItems.size();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mNewsDownloader.clearQueue();
    }


    private class FetchItemsTask extends AsyncTask<Void,Void,List<NewsItem>> {

        private String mUrl;

        public FetchItemsTask(String url) {
            mUrl = url;
        }

        @Override
        protected List<NewsItem> doInBackground(Void... voids) {
            if (mUrl == null) return new NewsFetchr().fetchItems(TOP_STORIES_URL);
            else return new NewsFetchr().fetchItems(mUrl);

        }

        @Override
        protected void onPostExecute(List<NewsItem> newsItems) {
            mItems = newsItems;
            setupAdapter();
        }
    }
}
