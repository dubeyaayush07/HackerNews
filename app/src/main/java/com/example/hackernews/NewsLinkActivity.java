package com.example.hackernews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

public class NewsLinkActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri newsLink) {
        Intent i = new Intent(context, NewsLinkActivity.class);
        i.setData(newsLink);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return NewsLinkFragment.newInstance(getIntent().getData());
    }
}
