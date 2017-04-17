package project.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Class representing the <code>Fragment</code> for displaying app information.
 *
 */
public class AboutFragment extends Fragment {

    /**
     * Overrides the
     * <code>Fragment.onCreateView(LayoutInflater, ViewGroup, Bundle)</code>
     * method of the Android API. <br/>
     *
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The <code>LayoutInflater</code> object that can be used to
     *                 inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     *                  UI should be attached to.The fragment should not add the
     *                  view itself, but this can be used to generate the
     *                  <code>LayoutParams</code> of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return the <code>View</code> for the fragment's UI, or <code>null</code>.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        WebView mWebView = (WebView) view.findViewById(R.id.webView);
        mWebView.loadUrl("file:///android_asset/about.html");
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        return view;
    }

    /**
     * Overrides the <code>Fragment.onResume()</code> method
     * of the Android API. <br/>
     *
     * Called when the activity will start interacting with the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Overrides the <code>Fragment.onPause()</code> method
     * of the Android API. <br/>
     *
     * Called when the system is about to start resuming a previous
     * activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }
}