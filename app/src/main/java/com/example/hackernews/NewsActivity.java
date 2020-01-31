package com.example.hackernews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class NewsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NewsFragment.newInstance();
    }
}
