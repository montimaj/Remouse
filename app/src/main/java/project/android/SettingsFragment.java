package project.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Class representing the <code>Fragment</code> for changing app settings
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    /**
     * Overrides the <code>android.support.v4.app.Fragment.onResume()</code>.<br/>
     *
     * Called when the activity will start interacting with the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Overrides the <code>android.support.v4.app.Fragment.onPause()</code>.<br/>
     *
     * Called when the system is about to start resuming a previous activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }
}
