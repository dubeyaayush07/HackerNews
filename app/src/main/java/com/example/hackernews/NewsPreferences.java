package com.example.hackernews;

import android.content.Context;
import android.preference.PreferenceManager;

public class NewsPreferences {

    private static final String PREF_NEWS_QUERY = "newsQuery";
    public static String getStoredQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_NEWS_QUERY, null);
    }
    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_NEWS_QUERY, query)
                .apply();
    }

}
