package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import sxccal.edu.android.remouse.ConnectionFragment;
import sxccal.edu.android.remouse.NetworkService;
import sxccal.edu.android.remouse.R;
import sxccal.edu.android.remouse.security.EKEProvider;

import static sxccal.edu.android.remouse.ConnectionFragment.sConnectionAlive;
import static sxccal.edu.android.remouse.ConnectionFragment.sSelectedServer;
import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;
import static sxccal.edu.android.remouse.MainActivity.sFragmentList;

/**
 * @author Sayantan Majumdar
 */

public class ClientIOThread implements Runnable {

    private Activity mActivity;
    private EKEProvider mEKEProvider;
    private ServerInfo mServerInfo;
    private String mAddress;
    private byte[] mPairingKey;
    private byte[] mServerPubKey;

    private boolean mStopFlag;


    public ClientIOThread(Activity activity, final String pairingKey, ServerInfo serverInfo) throws IOException {
        mActivity = activity;
        mServerInfo = serverInfo;
        mAddress = serverInfo.getAddress();
        mServerPubKey = serverInfo.getServerPubKey();
        mPairingKey = pairingKey.getBytes();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sSecuredClient.sendPairingKey(pairingKey);
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            sSecuredClient = new Client(mPairingKey, mServerPubKey);
            mEKEProvider = sSecuredClient.getEKEProvider();
            while (!mStopFlag) {
                recieveData();
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sSelectedServer.remove(mServerInfo);
                    ConnectionFragment connectionFragment = (ConnectionFragment) sFragmentList.get(0);
                    try {
                        connectionFragment.setImage(mServerInfo, R.mipmap.laptop_icon);
                    } catch (RuntimeException ignored) {}
                    Toast.makeText(mActivity, "Server " + mAddress + " disconnected! ", Toast.LENGTH_SHORT).show();
                }
            });
            sSecuredClient.close();
        } catch (IOException ignored) {}
    }
    private void recieveData() throws IOException {
        BufferedReader in = sSecuredClient.getSocketReader();
        if(!in.ready())    return;
        String s = mEKEProvider.decryptString(in.readLine());
        if(s != null) {
            if (s.equals("Stop")) {
                sConnectionAlive.put(mAddress, false);
                mStopFlag = true;
                return;
            }
            sConnectionAlive.put(mAddress, s.equals("1"));
        }
        if (!sConnectionAlive.get(mAddress)) {
            displayError(mActivity);
        } else {
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
                sSelectedServer.remove(mServerInfo);
            }
        });
        sSecuredClient.close();
    }
}