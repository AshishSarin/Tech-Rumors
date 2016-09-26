package com.sareen.squarelabs.techrumors.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sareen.squarelabs.techrumors.BuildConfig;
import com.sareen.squarelabs.techrumors.R;

public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setElevation(0f);
        TextView versionText = (TextView)findViewById(R.id.about_version_text);
        String versionName = BuildConfig.VERSION_NAME;
        versionText.setText("Version: " + versionName);
    }
}
