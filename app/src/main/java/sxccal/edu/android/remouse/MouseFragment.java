package sxccal.edu.android.remouse;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import java.io.IOException;

import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;
import static sxccal.edu.android.remouse.net.ClientIOThread.sConnectionAlive;

/**
 * @author Sayantan Majumdar
 */

public class MouseFragment extends Fragment implements View.OnClickListener {

    private SwitchCompat mSwitch;

    private static boolean sFirstTouch = false;
    private static long sTouchTime;
    static boolean sMouseAlive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mouse, container, false);
        mSwitch = (SwitchCompat) view.findViewById(R.id.switch2);
        Button left = (Button) view.findViewById(R.id.button_left);
        Button right = (Button) view.findViewById(R.id.button_right);
        Button middle = (Button) view.findViewById(R.id.button_middle);
        ImageButton upScroll = (ImageButton) view.findViewById(R.id.upscroll);
        ImageButton downScroll = (ImageButton) view.findViewById(R.id.downscroll);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mSwitch.setChecked(true);
                    if(sConnectionAlive) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sMouseAlive = true;
                                sendMouseMovementData();
                            }
                        }).start();
                    }
                } else  {
                    mSwitch.setChecked(false);
                    sMouseAlive = false;
                }
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(sConnectionAlive && motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if(sFirstTouch && (System.currentTimeMillis() - sTouchTime) <= 300 ) {
                        sFirstTouch = false;
                        sSecuredClient.sendMouseData("left");
                    } else {
                        sFirstTouch = true;
                        sTouchTime = System.currentTimeMillis();
                        sSecuredClient.sendMouseData("left");
                    }
                }
                return true;
            }
        });

        right.setOnClickListener(this);
        middle.setOnClickListener(this);
        upScroll.setOnClickListener(this);
        downScroll.setOnClickListener(this);

        return view;
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
        if(sConnectionAlive)    sSecuredClient.sendMouseData(data);
    }

    private void sendMouseMovementData() {
        try {
            int x = (int) (Math.random() * 10), y = (int) (Math.random() * 10);
            while (sConnectionAlive && sMouseAlive) {
                sSecuredClient.sendMouseData(x, y);
                x += 50;
                y += 50;
                Log.d("ClientConnection: ", "" + x + " " + y);
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException e) { e.printStackTrace(); }
    }


}
