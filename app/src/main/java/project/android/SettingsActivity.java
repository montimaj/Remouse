package project.android;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import static project.android.MainActivity.sSharedPrefs;

/**
 * Class representing the <code>Activity</code> for app settings.
 *
 * <p>
 *     It displays the app settings and enables customization
 *     for the users. There are two customizations that are
 *     present :
 *     <ol>
 *         <li>3D Mouse Sensitivity.</li>
 *         <li>2D Mouse Sensitivity.</li>
 *     </ol>
 *     Each of these can have three possible values - : <i>"Low"</i>,
 *     <i>"Medium"</i> and <i>"High"</i>. The default value is
 *     <i>"Medium"</i>.
 * </p>
 * <p>
 *     This class is made separate from the {@link MainActivity}
 *     to enable the <i>"Settings"</i> to be opened from any
 *     fragment of the <code>MainActivity</code>. This avoids
 *     overlapping of other fragments - that are already rendered -
 *     by the {@link SettingsFragment}.
 * </p>
 * <p>
 *     It also implements the
 *     <code>SharedPreferences.OnSharedPreferenceListener</code>
 *     of the Android API. The
 *     {@link #onSharedPreferenceChanged(SharedPreferences, String)}
 *     method listens to any changes made in the app settings.
 * </p>
 *
 * @see project.android.MainActivity
 * @see project.android.SettingsFragment
 * @see #onSharedPreferenceChanged(SharedPreferences, String)
 */
public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static float sMouseSensitivity3d = sSharedPrefs.getFloat("3d", 2000.0f);
    public static float sMouseSensitivity2d = sSharedPrefs.getFloat("2d", 1.5f);

    /**
     * Overrides the <code>AppCompatActivity.onCreate(Bundle)</code>
     * of the Android API. <br/>
     *
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized
     *                           after previously being shut down then
     *                           this <code>Bundle</code> contains the
     *                           data it most recently supplied.<br/>
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
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

    /**
     * Overrides the
     * <code>
     *     OnSharedPreferenceChangeListener.onSharedPreferenceChanged
     *     (SharedPreferences, String)
     * </code>
     * method of the Android API.
     *
     * @param sharedPreferences the <code>SharedPreferences</code>
     *                          that received the change.
     * @param key the key of the preference that was changed, added,
     *            or removed.
     */
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
     * Overrides the <code>FragmentActivity.onResume()</code>
     * method of the Android API. <br/>
     *
     * Called when the activity will start interacting with
     * the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Overrides the <code>FragmentActivity.onPause()</code>
     * method of the Android API. <br/>
     *
     * Called when the system is about to start resuming a
     * previous activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }
}
