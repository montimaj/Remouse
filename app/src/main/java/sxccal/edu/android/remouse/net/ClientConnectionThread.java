package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;

import sxccal.edu.android.remouse.ConnectionFragment;

/**
 * Client to Server connection
 * @author Sudipto <ttsudipto@gmail.com>, Sayantan <monti.majumdar@gmail.com>
 */
public class ClientConnectionThread implements Runnable {

    private Context mContext;
    private Activity mActivity;
    private static ProgressDialog sProgressDialog;
    public static DatagramSocket sDatagramSocket;

    private HashSet<String> mLocalDevices = new HashSet<>();

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
                sDatagramSocket = new DatagramSocket(1235);
                Log.d("ClientConnection: ", "Connecting...");
                sDatagramSocket.setBroadcast(true);
                DatagramPacket datagramPacket = new DatagramPacket(new byte[32],32);
                sDatagramSocket.receive(datagramPacket);
                lock.release();

                Log.d("ClientConnection: ", new String(datagramPacket.getData()));
                Log.d("ClientConnection: ", ""+sDatagramSocket.getInetAddress());

                InetAddress inetAddress = datagramPacket.getAddress();
                mLocalDevices.add(inetAddress.toString().substring(1));

                Log.d("ClientConnection: ", inetAddress.toString());
                currentTime = System.currentTimeMillis();
                sDatagramSocket.close();
             }while((currentTime - startTime)/1000 < 5);
            handler.sendEmptyMessage(0);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConnectionFragment.addItems(mLocalDevices);
                }
            });
        }catch(IOException e) { e.printStackTrace(); }
    }
}
