package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

import sxccal.edu.android.remouse.NetworkService;
import sxccal.edu.android.remouse.security.EKEProvider;

import static sxccal.edu.android.remouse.ConnectionFragment.sListItemClicked;
import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;

/**
 * @author Sayantan Majumdar
 */

public class ClientIOThread implements Runnable {

    private Activity mActivity;
    private String mAddress;
    private EKEProvider mEKEProvider;

    private boolean mStopFlag;
    public static boolean sConnectionAlive;

    private static final int PAIRING_KEY_LENGTH = 6;

    public ClientIOThread(Activity activity, String pairingKey, String address) throws IOException {
        mActivity = activity;
        mAddress = address;

        sSecuredClient = new Client(pairingKey.getBytes());
        mEKEProvider = sSecuredClient.getEKEProvider();
        int len = pairingKey.length();
        if(len != PAIRING_KEY_LENGTH )    pairingKey = "1";
        final String key = pairingKey;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sSecuredClient.sendPairingKey(key);
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            while (!mStopFlag) {
                recieveData();
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sListItemClicked = false;
                    Toast.makeText(mActivity, "Server " + mAddress + " disconnected! " +
                            "You may reconnect", Toast.LENGTH_LONG).show();
                }
            });
            sSecuredClient.close();
        } catch (IOException e) {}
    }
    private void recieveData() throws IOException {
        BufferedReader in = sSecuredClient.getSocketReader();
        if(!in.ready())    return;
        String s = mEKEProvider.decryptString(in.readLine());
        if(s != null) {
            if (s.equals("Stop")) {
                sConnectionAlive = false;
                mStopFlag = true;
                return;
            }
            sConnectionAlive = s.equals("1");
        }
        if (!sConnectionAlive) {
            displayError(mActivity);
        } else {
            sListItemClicked = true;
            Intent intent = new Intent(mActivity, NetworkService.class);
            intent.putExtra("Server", mAddress);
            mActivity.startService(intent);
        }
    }

    private void displayError(final Activity activity) throws IOException {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Incorrect pin! Try connecting again",
                        Toast.LENGTH_LONG).show();
                sListItemClicked = false;
            }
        });
        sSecuredClient.close();
    }
}
