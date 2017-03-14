package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

import sxccal.edu.android.remouse.ConnectionFragment;
import sxccal.edu.android.remouse.MainActivity;
import sxccal.edu.android.remouse.NetworkService;
import sxccal.edu.android.remouse.security.EKEProvider;

import static sxccal.edu.android.remouse.ConnectionFragment.sConnectionAlive;
import static sxccal.edu.android.remouse.ConnectionFragment.sSelectedServer;
import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;

/**
 * @author Sayantan Majumdar
 */

class ClientIOThread implements Runnable {

    private Activity mActivity;
    private EKEProvider mEKEProvider;
    private ServerInfo mServerInfo;
    private String mAddress;
    private boolean mStopFlag;

    ClientIOThread(Activity activity, final ServerInfo serverInfo, EKEProvider ekeProvider) throws IOException {
        mActivity = activity;
        mServerInfo = serverInfo;
        mAddress = serverInfo.getAddress();
        mEKEProvider = ekeProvider;
        sSecuredClient.setEKEProvider(ekeProvider);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sSecuredClient.sendPairingKey(mEKEProvider.encryptString(serverInfo.getPairingKey()));
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
                    sSelectedServer.remove(mServerInfo);
                    ConnectionFragment connectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();
                    try {
                        connectionFragment.resetIcon(mServerInfo);
                    } catch (RuntimeException ignored) {}
                    Toast.makeText(mActivity, "Server " + mAddress + " disconnected! ", Toast.LENGTH_SHORT).show();
                }
            });
            sSecuredClient.close();
        } catch (IOException ignored) {}
    }

    private void recieveData() throws IOException {
        BufferedReader in = sSecuredClient.getSocketReader();
        if(!in.ready()) return;
        String s = mEKEProvider.decryptString(in.readLine());
        if(s != null) {
            if (s.equals("Stop")) {
                sConnectionAlive.put(mAddress, false);
                mStopFlag = true;
                return;
            }
            sConnectionAlive.put(mAddress, s.equals("1"));
        }
        if (s == null || !sConnectionAlive.get(mAddress)) {
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