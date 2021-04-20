package com.example.foodliappserver.Screens.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import com.example.foodliapp.R;

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPrefrenceFragment()).commit();
    }
    public static class MyPrefrenceFragment extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefrences);

        }
    }
}