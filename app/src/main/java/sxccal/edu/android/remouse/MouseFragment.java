package sxccal.edu.android.remouse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.io.IOException;

import static sxccal.edu.android.remouse.ConnectionFragment.sClient;

/**
 * @author Sayantan Majumdar
 */

public class MouseFragment extends Fragment {

    private SwitchCompat mSwitch;
    static boolean sConnectionAlive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mouse, container, false);
        mSwitch = (SwitchCompat) view.findViewById(R.id.switch2);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mSwitch.setChecked(true);
                    sConnectionAlive = true;
                    if(sClient != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendMouseMovementData();
                            }
                        }).start();
                    }
                } else  {
                    mSwitch.setChecked(false);
                    sConnectionAlive = false;
                }
            }
        });
        return view;
    }

    private void sendMouseMovementData() {
        try {
            int x = (int) (Math.random() * 10), y = (int) (Math.random() * 10), k = 0;
            while (sConnectionAlive) {
                sClient.sendMouseData(k,x,y);
                ++k;
                x += 50;
                y += 50;
                Log.d("ClientConnection: ", "" + x + " " + y);
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException e) { e.printStackTrace(); }
    }
}
