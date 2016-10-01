package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashSet;

import sxccal.edu.android.remouse.ConnectionFragment;

import static sxccal.edu.android.remouse.net.server.NetworkManager.sPublicKey;

/**
 * Client to Server connection
 * @author Sayantan Majumdar
 * @author Sudipto Bhattacharjee
 */
public class ClientConnectionThread implements Runnable {

    private Context mContext;
    private Activity mActivity;
    private HashSet<String> mLocalDevices = new HashSet<>();

    private static final int SOCKET_TIMEOUT = 5000;
    private static final int UDP_PORT = 1235;
    private static ProgressDialog sProgressDialog;

    public ClientConnectionThread(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            sProgressDialog.dismiss();
        }
    };

    @Override
    public void run() {
        try {
            WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiManager.MulticastLock lock = wifi.createMulticastLock("remouseMulticastLock");

            long startTime = System.currentTimeMillis(), currentTime;

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sProgressDialog = ProgressDialog.show(mContext, "Scanning for Local Devices",
                            "Please wait!", false, false);
                }
            });

            do {
                lock.acquire();
                DatagramSocket datagramSocket = new DatagramSocket(UDP_PORT);
                Log.d("ClientConnection: ", "Connecting...");
                datagramSocket.setBroadcast(true);
                DatagramPacket datagramPacket = new DatagramPacket(new byte[sPublicKey.length], sPublicKey.length);
                try {
                    datagramSocket.setSoTimeout(SOCKET_TIMEOUT);
                    datagramSocket.receive(datagramPacket);
                } catch(SocketTimeoutException e) {
                    lock.release();
                    datagramSocket.close();
                    break;
                }
                lock.release();

                Log.d("Server public key: ", new String(datagramPacket.getData()));

                InetAddress inetAddress = datagramPacket.getAddress();
                mLocalDevices.add(inetAddress.toString().substring(1));

                currentTime = System.currentTimeMillis();
                datagramSocket.close();
             } while((currentTime - startTime) < SOCKET_TIMEOUT);
            handler.sendEmptyMessage(0);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mLocalDevices.isEmpty()) {
                        Toast.makeText(mActivity, "No local devices found!", Toast.LENGTH_LONG).show();
                    } else {
                        ConnectionFragment.addItems(mLocalDevices);
                    }
                }
            });
        }catch(IOException e) { e.printStackTrace(); }
    }
}
