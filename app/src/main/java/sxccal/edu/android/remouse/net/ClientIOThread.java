package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

import sxccal.edu.android.remouse.security.EKEProvider;

import static sxccal.edu.android.remouse.ConnectionFragment.sAdapter;
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
        sSecuredClient.sendPairingKey(pairingKey);
        System.out.println("Pairing key: " + pairingKey);
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
                    sAdapter.clear();
                    Toast.makeText(mActivity, "Server closed! " +
                            "Please search for available servers", Toast.LENGTH_LONG).show();
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
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "Connected to " + mAddress +
                            "\nOpen either Mouse or Keyboard Tabs from the navigation bar",
                                    Toast.LENGTH_LONG).show();
                }
            });
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
