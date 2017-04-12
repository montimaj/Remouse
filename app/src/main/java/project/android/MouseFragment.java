package project.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import project.android.sensor.orientation.KalmanFilterProvider;
import project.android.sensor.orientation.OrientationProvider;

import static project.android.ConnectionFragment.sConnectionAlive;
import static project.android.ConnectionFragment.sSecuredClient;

/**
 * @author Sayantan Majumdar
 */

public class MouseFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private OrientationProvider mOrientationProvider;

    private long mTouchTime;
    private boolean mFirstTouch;

    public static boolean sMouseAlive;

    /**
     * Overrides the <code>android.support.v4.app.Fragment.onCreateView(LayoutInflater, ViewGroup, Bundle)</code>.<br/>
     *
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The <code>LayoutInflater</code> object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate
     *                  the <code>LayoutParams</code> of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return the <code>View</code> for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mouse, container, false);
        Button left = (Button) view.findViewById(R.id.button_left);
        Button right = (Button) view.findViewById(R.id.button_right);
        Button middle = (Button) view.findViewById(R.id.button_middle);
        Button moveButton = (Button) view.findViewById(R.id.moveButton);
        ImageButton upScroll = (ImageButton) view.findViewById(R.id.upscroll);
        ImageButton downScroll = (ImageButton) view.findViewById(R.id.downscroll);

        moveButton.setLongClickable(true);
        moveButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(sConnectionAlive.containsValue(true)) {
                    sMouseAlive = true;
                    sendMouseMovementData();
                }
                return true;
            }
        });
        moveButton.setOnTouchListener(this);
        left.setOnTouchListener(this);

        right.setOnClickListener(this);
        middle.setOnClickListener(this);
        upScroll.setOnClickListener(this);
        downScroll.setOnClickListener(this);

        return view;
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

    /**
     * Overrides the <code>android.support.v4.app.Fragment.onDestroyView()</code>.<br/>
     *
     * Called when the view previously created by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sMouseAlive = false;
        if(mOrientationProvider != null)    mOrientationProvider.sensorStop();
    }

    /**
     * Overrides the <code>android.view.View.onTouchListener.onTouch(View, MotionEvent)</code>.<br/>
     *
     * Called when a touch event is dispatched to a <code>View</code>. This allows listeners to get
     * a chance to respond before the target <code>View</code>.
     * @param view The view the touch event has been dispatched to.
     * @param motionEvent The MotionEvent object containing full information about the event.
     * @return <code>true</code> if the listener has consumed the event,<br/>
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId() == R.id.moveButton) {
            if(sConnectionAlive.containsValue(true) && sMouseAlive && motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                sMouseAlive = false;
                if(mOrientationProvider != null)    mOrientationProvider.sensorStop();
            }
            return false;
        }
        if(view.getId() == R.id.button_left) {
            if (sConnectionAlive.containsValue(true) && motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                if (mFirstTouch && (System.currentTimeMillis() - mTouchTime) <= 300) {
                    mFirstTouch = false;
                    sendMouseButtonData("left");
                } else {
                    mFirstTouch = true;
                    mTouchTime = System.currentTimeMillis();
                    sendMouseButtonData("left");
                }
            }
        }
        return true;
    }

    /**
     * Overrides the <code>android.view.View.onClickListener.onClick(View)</code>.<br/>
     *
     * Called when a view has been clicked.
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        String data = "";
        switch(view.getId()) {

            case R.id.button_right:
                data = "right";
                break;

            case R.id.button_middle:
                data = "middle";
                break;

            case R.id.upscroll:
                data = "upscroll";
                break;

            case R.id.downscroll:
                data = "downscroll";
        }
        sendMouseButtonData(data);
    }

    private void sendMouseButtonData(final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sConnectionAlive.containsValue(true)) {
                    sSecuredClient.sendData("Mouse_Button", data);
                }
            }
        }).start();
    }

    private void sendMouseMovementData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mOrientationProvider = new KalmanFilterProvider((SensorManager)
                        getActivity().getSystemService(Activity.SENSOR_SERVICE));
                if (!sMouseAlive || !sConnectionAlive.containsValue(true)) mOrientationProvider.sensorStop();
            }
        }).start();
    }
}