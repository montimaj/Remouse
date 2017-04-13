package project.android;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import static project.android.MainActivity.sSharedPrefs;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static float sMouseSensitivity3d = sSharedPrefs.getFloat("3d", 2000.0f);
    public static float sMouseSensitivity2d = sSharedPrefs.getFloat("2d", 1.5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame_settings, new SettingsFragment())
                .commit();
        if (getSupportActionBar() != null)  getSupportActionBar().setTitle("Settings");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("sensitivity_3D")) {
            sMouseSensitivity3d = Float.parseFloat(sharedPreferences.getString(key, "2000.0"));
        } else {
            sMouseSensitivity2d = Float.parseFloat(sharedPreferences.getString(key, "1.5"));
        }
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.putFloat("2d", sMouseSensitivity2d);
        editor.putFloat("3d", sMouseSensitivity3d);
        editor.apply();
    }

    /**
     * Overrides the <code>android.support.v7.app.FragmentActivity.onResume()</code>.<br/>
     *
     * Called when the activity will start interacting with the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Overrides the <code>android.support.v7.app.FragmentActivity.onPause()</code>.<br/>
     *
     * Called when the system is about to start resuming a previous activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }
}
