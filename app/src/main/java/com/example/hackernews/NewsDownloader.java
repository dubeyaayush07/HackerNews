package com.example.hackernews;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class NewsDownloader<T> extends HandlerThread {

    private static final String TAG = "NewsDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;


    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private ConcurrentHashMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private NewsDownloadListener<T> mNewsDownloadListener;

    public interface NewsDownloadListener<T> {
        void onNewsDownload(T target, NewsItem newsItem);
    }

    public void setNewsDownloadListener(NewsDownloadListener<T> newsDownloadListener) {
        mNewsDownloadListener = newsDownloadListener;
    }

    public NewsDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            mRequestMap.remove(target, url);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }


    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }


    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);
        if (url == null) {
            return;
        }

        final NewsItem newsItem = new NewsFetchr().fetchNewsItem(url);

        Log.i(TAG, "NewsItem created");

        mResponseHandler.post(new Runnable() {
            public void run() {
                if (mRequestMap.get(target) != url ||
                        mHasQuit) {
                    return;
                }
                mRequestMap.remove(target);
                mNewsDownloadListener.onNewsDownload(target, newsItem);
            }
        });


    }


    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }





}
