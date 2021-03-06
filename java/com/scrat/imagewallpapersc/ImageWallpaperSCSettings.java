package com.scrat.imagewallpapersc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressLint("ExportedPreferenceActivity")
public class ImageWallpaperSCSettings extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final int OPEN_DIRECTORY_REQUEST_CODE = 0xf11e;
    private final int OPEN_MULTIPLE_REQUEST_CODE = 0xf11f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if  (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        addPreferencesFromResource(R.xml.settings);

        android.app.ActionBar actionbar;
        actionbar = getActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        findPreference("directory").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                openDirectory();
                return true;
            }
        });
        findPreference("multi").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                selectMulti();
                return true;
            }
        });
    }

    private void openDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |  Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
    }

    private void selectMulti () {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |  Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, OPEN_MULTIPLE_REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            SharedPreferences.Editor settings = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            if (requestCode == OPEN_DIRECTORY_REQUEST_CODE) {

                Uri directoryUri = imageReturnedIntent.getData();
                assert directoryUri != null;
                getContentResolver().takePersistableUriPermission(
                        directoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
                settings.putString("directory", directoryUri.toString());
                settings.putInt("mode", 2);
                settings.apply();
            }
            if (requestCode == OPEN_MULTIPLE_REQUEST_CODE) {
                ArrayList<String> listUri = new ArrayList<>();
                ClipData clipData = imageReturnedIntent.getClipData();
                if (clipData!=null)
                for (int i = 0; i < clipData.getItemCount(); i++) listUri.add(clipData.getItemAt(i).getUri().toString());
                else listUri.add(Objects.requireNonNull(imageReturnedIntent.getData()).toString());
                Set<String> arraySet = new HashSet<>(listUri);
                settings.putStringSet("multi", arraySet);
                settings.putInt("mode", 1);
                settings.apply();
            }
            sendBroadcast(new Intent("com.scrat.imagewallpapersc.UpdateDBForSaveFolder"));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        EditTextPreference duration = (EditTextPreference) findPreference("duration");
        duration.setSummary(duration.getText() +" "+ getResources().getString(R.string.min));
        String[] Speed_value = getResources().getStringArray(R.array.speed_values);
        ListPreference SpeedSetting = (ListPreference) findPreference("speed");
        if (SpeedSetting.getValue().equals(Speed_value[0])) {
            SpeedSetting.setSummary(getResources().getString(R.string.speed_fast));
        }
        if (SpeedSetting.getValue().equals(Speed_value[1])) {
            SpeedSetting.setSummary(getResources().getString(R.string.speed_normal));
        }
        if (SpeedSetting.getValue().equals(Speed_value[2])) {
            SpeedSetting.setSummary(getResources().getString(R.string.speed_low));
        }
        if (SpeedSetting.getValue().equals(Speed_value[3])) {
            SpeedSetting.setSummary(getResources().getString(R.string.speed_off));
        }

        String[] touch_value = getResources().getStringArray(R.array.touch_values);
        ListPreference TouchSetting = (ListPreference) findPreference("touch");
        if (TouchSetting.getValue().equals(touch_value[2])) {
            TouchSetting.setSummary(getResources().getString(R.string.touch_off));
        }
        if (TouchSetting.getValue().equals(touch_value[1])) {
            TouchSetting.setSummary(getResources().getString(R.string.touch_offset));
        }
        if (TouchSetting.getValue().equals(touch_value[0])) {
            TouchSetting.setSummary(getResources().getString(R.string.touch_parallax));
        }


        ListPreference QualitySetting = (ListPreference) findPreference("quality");
        String[] Quality_value = getResources().getStringArray(R.array.quality_values);
        if (QualitySetting.getValue().equals(Quality_value[0])) {
            QualitySetting.setSummary(getResources().getString(R.string.quality_ultra));
        }
        if (QualitySetting.getValue().equals(Quality_value[1])) {
            QualitySetting.setSummary(getResources().getString(R.string.quality_high));
        }
        if (QualitySetting.getValue().equals(Quality_value[2])) {
            QualitySetting.setSummary(getResources().getString(R.string.quality_normal));
        }
        if (QualitySetting.getValue().equals(Quality_value[3])) {
            QualitySetting.setSummary(getResources().getString(R.string.quality_low));
        }

        ListPreference BlurSetting = (ListPreference) findPreference("blur_level");
        String[] Blur_value = getResources().getStringArray(R.array.blur_values);
        if (BlurSetting.getValue().equals(Blur_value[0])) {
            BlurSetting.setSummary(getResources().getString(R.string.blur_low));
        }
        if (BlurSetting.getValue().equals(Blur_value[1])) {
            BlurSetting.setSummary(getResources().getString(R.string.blur_normal));
        }
        if (BlurSetting.getValue().equals(Blur_value[2])) {
            BlurSetting.setSummary(getResources().getString(R.string.blur_high));
        }
        if (BlurSetting.getValue().equals(Blur_value[3])) {
            BlurSetting.setSummary(getResources().getString(R.string.blur_ultra));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            if (key.equals("duration")) {
                etp.setSummary(etp.getText() + " " + getResources().getString(R.string.min));
            }
        }
        if (pref instanceof ListPreference) {
            ListPreference ltp = (ListPreference) pref;
            if (key.equals("speed")) {
                String[] Speed_value = getResources().getStringArray(R.array.speed_values);
                if (ltp.getValue().equals(Speed_value[0])) {
                    ltp.setSummary(getResources().getString(R.string.speed_fast));
                }
                if (ltp.getValue().equals(Speed_value[1])) {
                    ltp.setSummary(getResources().getString(R.string.speed_normal));
                }
                if (ltp.getValue().equals(Speed_value[2])) {
                    ltp.setSummary(getResources().getString(R.string.speed_low));
                }
                if (ltp.getValue().equals(Speed_value[3])) {
                    ltp.setSummary(getResources().getString(R.string.speed_off));
                }
            }
            if (key.equals("touch")) {
                String[] touch_value = getResources().getStringArray(R.array.touch_values);
                if (ltp.getValue().equals(touch_value[2])) {
                    ltp.setSummary(getResources().getString(R.string.touch_off));
                }
                if (ltp.getValue().equals(touch_value[1])) {
                    ltp.setSummary(getResources().getString(R.string.touch_offset));
                }
                if (ltp.getValue().equals(touch_value[0])) {
                    ltp.setSummary(getResources().getString(R.string.touch_parallax));
                }
            }
            if (key.equals("quality")) {
                String[] Quality_value = getResources().getStringArray(R.array.quality_values);
                if (ltp.getValue().equals(Quality_value[0])) {
                    ltp.setSummary(getResources().getString(R.string.quality_ultra));
                }
                if (ltp.getValue().equals(Quality_value[1])) {
                    ltp.setSummary(getResources().getString(R.string.quality_high));
                }
                if (ltp.getValue().equals(Quality_value[2])) {
                    ltp.setSummary(getResources().getString(R.string.quality_normal));
                }
                if (ltp.getValue().equals(Quality_value[3])) {
                    ltp.setSummary(getResources().getString(R.string.quality_low));
                }
            }

            if (key.equals("blur_level")) {
                String[] Blur_value = getResources().getStringArray(R.array.blur_values);
                if (ltp.getValue().equals(Blur_value[0])) {
                    ltp.setSummary(getResources().getString(R.string.blur_low));
                }
                if (ltp.getValue().equals(Blur_value[1])) {
                    ltp.setSummary(getResources().getString(R.string.blur_normal));
                }
                if (ltp.getValue().equals(Blur_value[2])) {
                    ltp.setSummary(getResources().getString(R.string.blur_high));
                }
                if (ltp.getValue().equals(Blur_value[3])) {
                    ltp.setSummary(getResources().getString(R.string.blur_ultra));
                }
            }
        }
    }
}