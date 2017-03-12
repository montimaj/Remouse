package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;;

import sxccal.edu.android.remouse.ConnectionFragment;
import sxccal.edu.android.remouse.MainActivity;

import static sxccal.edu.android.remouse.ConnectionFragment.sConnectionAlive;

/**
 * Client to Server connection
 * @author Sayantan Majumdar
 * @author Sudipto Bhattacharjee
 */

public class ClientConnectionThread implements Runnable {

    private Activity mActivity;
    private WifiManager.MulticastLock mLock;
    private ArrayList<String> mServerAddress;
    private DatagramSocket mDatagramSocket;
    private ConnectionFragment mConnectionFragment;

    private static final int UDP_PORT = 1235;

    public ClientConnectionThread(Activity activity, WifiManager.MulticastLock lock) {
        mActivity = activity;
        mLock = lock;
        mDatagramSocket = null;
        mServerAddress = new ArrayList<>();
        mConnectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();;
    }

    @Override
    public void run() {
        try {
            mLock.acquire();
            if (mDatagramSocket == null) {
                mDatagramSocket = new DatagramSocket(UDP_PORT);
                mDatagramSocket.setBroadcast(true);
            }
        } catch (IOException e) { e.printStackTrace(); }

        while (mDatagramSocket != null) {
            DatagramPacket datagramPacket = null;
            try {
                if (!mLock.isHeld()) mLock.acquire();
                datagramPacket = new DatagramPacket(new byte[ServerInfo.SERVER_INFO_LENGTH],
                        ServerInfo.SERVER_INFO_LENGTH);
                mDatagramSocket.receive(datagramPacket);
                mLock.release();
            } catch (IOException ignored) {}
            String gsonString = new String(datagramPacket.getData());
            gsonString = gsonString.substring(0, gsonString.indexOf("}") + 1);
            ServerInfo serverInfo = new Gson().fromJson(gsonString, ServerInfo.class);
            String serverAddress = datagramPacket.getAddress().toString().substring(1);
            serverInfo.setServerAddress(serverAddress);

            checkAvailableDevices(serverInfo, serverAddress);
        }
    }
    private void checkAvailableDevices(final ServerInfo serverInfo, final String serverAddress) {

        if (serverInfo.getStopFlag()) {
            mServerAddress.remove(serverAddress);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "Server " + serverInfo.getAddress() + " stopped!",
                            Toast.LENGTH_SHORT).show();
                    mConnectionFragment.removeItem(serverInfo);
                    mConnectionFragment.dismissAlertDialog();
                }
            });
        } else {
            if (mServerAddress.isEmpty() || !mServerAddress.contains(serverAddress)) {
                mServerAddress.add(serverAddress);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnectionFragment.addItem(serverInfo);
                        sConnectionAlive.put(serverAddress, false);
                    }
                });
            }
        }
    }

    public void close() {
        if(mDatagramSocket != null) {
            mDatagramSocket. close();
            mDatagramSocket = null;
            mServerAddress = null;
        }
    }
}