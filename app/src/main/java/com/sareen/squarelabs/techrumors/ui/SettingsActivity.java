package com.sareen.squarelabs.techrumors.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sareen.squarelabs.techrumors.R;

/**
 * Created by Ashish on 03-09-2016.
 */
public class SettingsActivity extends AppCompatActivity
{

    private static final String PREF_KEY_NIGHT_MODE = "pref_key_night_mode";
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onSwitchNightMode(View view)
    {
        /*SwitchCompat switchCompat = (SwitchCompat)view;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if(switchCompat.isChecked())
        {
            editor.putBoolean(PREF_KEY_NIGHT_MODE, true);
            editor.commit();
            Toast.makeText(this, "Night Mode On",Toast.LENGTH_SHORT).show();
        }
        else
        {
            editor.putBoolean(PREF_KEY_NIGHT_MODE, false);
            editor.commit();
            Toast.makeText(this, "Night Mode Off", Toast.LENGTH_SHORT).show();
        }*/
    }
}
