package sxccal.edu.android.remouse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;
import static sxccal.edu.android.remouse.net.ClientIOThread.sConnectionAlive;

/**
 * @author Sayantan Majumdar
 */

public class TouchpadFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private long mTouchTime;
    private boolean mFirstTouch;

    private float mDownX;
    private float mDownY;

    private float mLastMoveX = Float.MAX_VALUE;
    private float mLastMoveY = Float.MAX_VALUE;

    private long mLastMoveTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_touchpad, container, false);
        Button left = (Button) view.findViewById(R.id.button_touch_left);
        Button right = (Button) view.findViewById(R.id.button_touch_right);
        Button middle = (Button) view.findViewById(R.id.button_touch_middle);
        Button touchButton = (Button) view.findViewById(R.id.button_touch);
        ImageButton upScroll = (ImageButton) view.findViewById(R.id.touch_upscroll);
        ImageButton downScroll = (ImageButton) view.findViewById(R.id.touch_downscroll);

        touchButton.setOnTouchListener(this);
        left.setOnTouchListener(this);

        right.setOnClickListener(this);
        middle.setOnClickListener(this);
        upScroll.setOnClickListener(this);
        downScroll.setOnClickListener(this);

        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.button_touch) {
            if (sConnectionAlive)   processTouch(motionEvent);
        }
        if (view.getId() == R.id.button_touch_left) {
            if (sConnectionAlive && motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
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
        switch (view.getId()) {

            case R.id.button_touch_right:
                data = "right";
                break;

            case R.id.button_touch_middle:
                data = "middle";
                break;

            case R.id.touch_upscroll:
                data = "upscroll";
                break;

            case R.id.touch_downscroll:
                data = "downscroll";
        }
        sendMouseButtonData(data);
    }

    private void sendMouseButtonData(final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sConnectionAlive) {
                    sSecuredClient.sendData("Mouse_Button", data);
                }
            }
        }).start();
    }

    private void processTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onSingleClick(event, true);
                break;
            case MotionEvent.ACTION_UP:
                onSingleClick(event, false);
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
        }
    }

    private void sendMouseMovementData(final int x, final int y) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sConnectionAlive) {
                    sSecuredClient.sendData(x, y);
                }
            }
        }).start();
    }

    private void onMove(MotionEvent event) {
        float curMoveX = event.getX();
        float curMoveY = event.getY();
        int distanceX = 0, distanceY = 0;

        if (mLastMoveX != Float.MAX_VALUE && mLastMoveY != Float.MAX_VALUE) {
            distanceX = (int) (curMoveX - mLastMoveX /*mDownX*/);
            distanceY = (int) (curMoveY - mLastMoveY /*mDownY*/);
        }

        int distance = (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        // send a move command per 0.5 s
//        if (distance > 100 || (System.currentTimeMillis() - mLastMoveTime) > 10) {
            sendMouseMovementData(distanceX, distanceY);
//            Log.d("Touch", ""+distanceX+distanceY);
            mLastMoveX = curMoveX;
            mLastMoveY = curMoveY;
            mLastMoveTime = System.currentTimeMillis();
//        }
    }

    private void onSingleClick(MotionEvent event, boolean down) {
        if (down) {
//            mDownX = event.getX();
//            mDownY = event.getY();
            mLastMoveX = event.getX();
            mLastMoveY = event.getY();
        }
    }
}