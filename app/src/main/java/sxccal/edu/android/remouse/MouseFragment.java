package sxccal.edu.android.remouse;

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

import sxccal.edu.android.remouse.sensor.orientation.KalmanFilterProvider;
import sxccal.edu.android.remouse.sensor.orientation.OrientationProvider;

import static sxccal.edu.android.remouse.ConnectionFragment.sConnectionAlive;
import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;

/**
 * @author Sayantan Majumdar
 */

public class MouseFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private OrientationProvider mOrientationProvider;

    private long mTouchTime;
    private boolean mFirstTouch;

    public static boolean sMouseAlive;

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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sMouseAlive = false;
        if(mOrientationProvider != null)    mOrientationProvider.sensorStop();
    }

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