package project.android.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

import project.android.BluetoothConnectionFragment;
import project.android.MainActivity;

public class DiscoveryThread implements Runnable {

    private Activity mActivity;

    public DiscoveryThread(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void run() { ////TODO later add LE scan
        Log.d("DiscoveryThread" , "foo");
        final Set<BluetoothDevice> devices = MainActivity.sState.getAdapter().getBondedDevices();
        MainActivity.sState.replaceConnDevices(devices);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BluetoothConnectionFragment fragment =
                        (BluetoothConnectionFragment) MainActivity.getBluetoothConnectionFragment();
                fragment.notifyDataSetChanged();
            }
        });
    }
}
