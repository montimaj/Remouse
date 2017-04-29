package project.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Class representing the <code>Fragment</code> for changing app
 * settings.
 *
 * This <code>SettingsFragment</code> is created by the
 * {@link SettingsActivity#onCreate(Bundle)} method.
 *
 * @see project.android.SettingsActivity
 * @see project.android.SettingsActivity#onCreate(Bundle)
 */
public class SettingsFragment extends PreferenceFragment {

    /**
     * Overrides the <code>PreferenceFragment.onCreate(Bundle)</code>
     * method of the Android API.
     *
     * Called when this fragment is created.
     *
     * @param savedInstanceState If the fragment is being re-created
     *                           from a previous saved state, this is
     *                           the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    /**
     * Overrides the <code>PreferenceFragment.onResume()</code> method
     * of the Android API.
     *
     * Called when this fragment will start interacting with the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Overrides the <code>PreferenceFragment.onPause()</code> method
     * of the Android API.
     *
     * Called when the system is about to start resuming a previous activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }
}