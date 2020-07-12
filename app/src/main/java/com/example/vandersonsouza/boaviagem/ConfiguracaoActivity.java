package com.example.vandersonsouza.boaviagem;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ConfiguracaoActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}