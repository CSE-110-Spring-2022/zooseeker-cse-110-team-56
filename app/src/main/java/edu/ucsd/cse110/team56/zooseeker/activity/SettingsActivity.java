package edu.ucsd.cse110.team56.zooseeker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import edu.ucsd.cse110.team56.zooseeker.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final var sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        final var styleSwitch = (SwitchCompat) findViewById(R.id.directions_style_switch);
        styleSwitch.setChecked(sharedPref.getBoolean(getString(R.string.directions_style_is_brief), false));

        styleSwitch.setOnCheckedChangeListener(this::onDirectionsStyleSwitchToggle);
    }

    private void onDirectionsStyleSwitchToggle(CompoundButton view, boolean isChecked) {
        final var editor = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                .edit();
        editor.putBoolean(getString(R.string.directions_style_is_brief), isChecked);
        editor.apply();
    }
}