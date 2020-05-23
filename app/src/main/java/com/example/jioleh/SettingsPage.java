package com.example.jioleh;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference.OnPreferenceClickListener;

public class SettingsPage extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initialiseToolbar();
        setTheme(R.style.SettingsFragmentStyle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference pref = findPreference("changepassword");
            pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(SettingsFragment.this.getActivity(),ChangePasswordPage.class));
                    return true;
                }
            });
        }
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbSettings);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
    }
}