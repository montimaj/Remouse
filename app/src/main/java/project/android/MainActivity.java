package project.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import project.android.security.EKEProvider;
import project.android.sensor.orientation.OrientationProvider;

/**
 * Class representing the activity that is launched at app startup.
 *
 * <p>
 *     Screenshot: <br /> <br />
 *     <img src= "../../../../../../scr/remouse.png" width="180" height="320" />
 * </p>
 * <p>
 *      It provides an interface for navigation bar which contains the following
 *      navigation items:
 *      <ul>
 *          <li> 3D Mouse </li>
 *          <li> 2D Mouse </li>
 *          <li> Keyboard </li>
 *          <li> Connect </li>
 *          <li> About </li>
 *      </ul>
 * </p>
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static ArrayList<Fragment> sFragmentList = new ArrayList<>();

    public static File sRemouseDir = null;
    public static byte[] sPublicKey;
    public static SharedPreferences sSharedPrefs;
    public static final String DEVICE_NAME = Build.MANUFACTURER + " " + Build.MODEL;

    /**
     * Overrides the <code>android.support.v7.app.AppCompatActivity.onCreate(Bundle)</code>.<br/>
     *
     * Called when the activity is first created
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle).
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sSharedPrefs = getSharedPreferences("REMOUSE", Context.MODE_PRIVATE);
        makeRemouseDirectory();

        new Thread(new Runnable() {
            @Override
            public void run() {
                sPublicKey = new EKEProvider().getBase64EncodedPubKey();
            }
        }).start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        sFragmentList.add(new ConnectionFragment());
        sFragmentList.add(new AboutFragment());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Overrides the <code>android.support.v7.app.FragmentActivity.onResume()</code>.<br/>
     *
     * Called when the activity will start interacting with the user.
     */
    @Override
    public void onResume() {
        super.onResume();
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    /**
     * Overrides the <code>android.support.v7.app.FragmentActivity.onBackPressed()</code>.<br/>
     *
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Overrides the <code>android.support.v7.app.AppCompatActivity.onDestroy()</code>.<br/>
     *
     * The final call received before an activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopService(new Intent(this, NetworkService.class));
    }

    /**
     * Overrides the <code>android.support.v7.app.Activity.onCreateOptionsMenu(Menu)</code>.<br/>
     *
     * Initializes the contents of the Activity's standard options menu.
     * @param menu The options menu for placing items.
     * @return <code>true</code>, if the menu is to be displayed,
     *         <code>false</code>, otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Overrides the <code>android.support.v7.app.Activity.onOptionsItemSelected(MenuItem)</code>.<br/>
     *
     * Handles action bar item clicks. The action bar will automatically handle
     * clicks on the Home/Up button, so long as a parent activity is specified
     * in AndroidManifest.xml.
     * @param item The menu item that was selected.
     * @return <code>false</code>, if normal menu processing is allowed to proceed,
     *         <code>true</code>, if it is to be consumed here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Implements the <code>android.support.design.widget.NavigationView.OnNavigationItemSelectedListener.onNavigationItemSelected(MenuItem).</code>
     *
     * Called when an item in the navigation menu is selected.
     * @param item The selected item.
     * @return @return <code>true</code> if the selected item is to be displayed.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        if (id == R.id.nav_mouse) {
            // Handle the 3D mouse action
            if(OrientationProvider.checkGyro(this)) {
                fragment = new MouseFragment();
                title = "Remote Mouse";
            } else {
                Toast.makeText(this, "Gyrosope not present!", Toast.LENGTH_LONG).show();
            }

        } else if(id == R.id.nav_touchpad) {
            // 2D Mouse
            fragment = new TouchpadFragment();
            title = "Remote Mouse";

        } else if (id == R.id.nav_keyboard) {
            // Handle the keyboard action
            fragment = new KeyboardFragment();
            title="Remote Keyboard";

        } else if (id == R.id.nav_connect) {
            // connection module
            fragment = sFragmentList.get(0);
            title="Connect to PC";

        } else if (id == R.id.nav_about) {
            // App info
            fragment = sFragmentList.get(1);
            title="About app";
        }
        if(fragment != null)    displayFragment(fragment, title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Returns the {@link project.android.ConnectionFragment} object.
     * @return the {@link project.android.ConnectionFragment} object.
     */
    public static Fragment getConnectionFragment() { return sFragmentList.get(0); }

    private void displayFragment(Fragment fragment, String title) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        // set the toolbar title
        if (getSupportActionBar() != null)  getSupportActionBar().setTitle(title);
    }

    private void makeRemouseDirectory() {
        sRemouseDir = getDir("Remouse", Context.MODE_PRIVATE);
        if(!sRemouseDir.exists())   sRemouseDir.mkdir();
    }
}