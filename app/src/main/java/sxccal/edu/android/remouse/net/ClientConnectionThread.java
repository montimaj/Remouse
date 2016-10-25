package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;

import sxccal.edu.android.remouse.ConnectionFragment;

import static sxccal.edu.android.remouse.ConnectionFragment.sListItemClicked;
import static sxccal.edu.android.remouse.ConnectionFragment.sSwitchChecked;
import static sxccal.edu.android.remouse.net.ClientIOThread.sConnectionAlive;

/**
 * Client to Server connection
 * @author Sayantan Majumdar
 * @author Sudipto Bhattacharjee
 */
public class ClientConnectionThread implements Runnable {

    private Context mContext;
    private Activity mActivity;
    private HashSet<String> mLocalDevices = new HashSet<>();

    private static final int UDP_PORT = 1235;
    static byte[] sServerPublicKey;

    public ClientConnectionThread(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    @Override
    public void run() {
        try {
            WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiManager.MulticastLock lock = wifi.createMulticastLock("remouseMulticastLock");

            while (sSwitchChecked) {
                lock.acquire();
                DatagramSocket datagramSocket = new DatagramSocket(UDP_PORT);
                datagramSocket.setBroadcast(true);
                final DatagramPacket datagramPacket = new DatagramPacket(new byte[Client.PUBLIC_KEY.length],
                        Client.PUBLIC_KEY.length);
                datagramSocket.receive(datagramPacket);
                lock.release();

                byte[] receivedData = datagramPacket.getData();
                final String serverAddress = datagramPacket.getAddress().toString().substring(1);
                if (new String(receivedData).contains("Stop")) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "Server " + serverAddress + " stopped!",
                                    Toast.LENGTH_LONG).show();
                            ConnectionFragment.removeItem(serverAddress);
                            ConnectionFragment.dismissAlertDialog();
                            sListItemClicked = false;
                            sConnectionAlive = false;
                        }
                    });
                } else {
                    sServerPublicKey = receivedData;
                    mLocalDevices.add(serverAddress);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mLocalDevices.isEmpty()) {
                                Toast.makeText(mActivity, "No local devices found!", Toast.LENGTH_LONG).show();
                            } else {
                                ConnectionFragment.addItems(mLocalDevices);
                            }
                        }
                    });
                }
                datagramSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

