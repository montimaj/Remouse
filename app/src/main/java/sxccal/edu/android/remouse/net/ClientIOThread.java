package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import sxccal.edu.android.remouse.net.server.NetworkManager;


/**
 * @author Sayantan Majumdar
 */
public class ClientIOThread implements Runnable {
    private static Client sClient;
    private Activity mActivity;
    private String mAddress;
    private String mPairingKey;
    private NetworkManager mNetworkManager;

    public ClientIOThread(Activity activity, NetworkManager networkManager, String pairingKey,
                          String address) {
        mActivity = activity;
        mNetworkManager = networkManager;
        mPairingKey = pairingKey;
        mAddress = address;
    }

    @Override
    public void run() {
        try {
            sClient = new Client(mAddress, NetworkManager.TCP_PORT);
            sClient.sendPairingKey(mPairingKey);
            System.out.println("Pairing key: " + mPairingKey);
            if (!sClient.getConfirmation()) {
                Log.d("ClientConnection: ", "Declined by server. Aborting !!!");
                sClient.close();
                try {
                    if (mNetworkManager != null)    mNetworkManager.stopServer();
                } catch(IOException e) { e.printStackTrace(); }
            } else {
                try {
                    if (mNetworkManager != null)    mNetworkManager.stopServer();
                } catch(IOException e) { e.printStackTrace(); }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Connected to " + mAddress, Toast.LENGTH_LONG).show();
                    }
                });
                int x = 0, y = 0, k = 0;
                while (k++ < 10) {
                    sClient.send(k, x, y);
                    x += 50;
                    y += 50;
                    Log.d("ClientConnection: ", "" + x + " " + y);
                    Thread.sleep(2000);
                }
                sClient.close();
            }

        } catch (IOException | InterruptedException e) { e.printStackTrace(); }
    }
}