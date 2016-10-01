package sxccal.edu.android.remouse.net.server;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.InterfaceAddress;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.Enumeration;
import java.util.List;

import static sxccal.edu.android.remouse.net.server.NetworkManager.sPublicKey;


/**
 * @author Sudipto Bhattacharjee
 * @author Sayantan Majumdar
 */

class BroadcastThread implements Runnable {

    private boolean mStopFlag;
    private static final int BROADCAST_PORT = 1236;

    void stopBroadcast() { mStopFlag = true; }

    @Override
    public void run() {
        mStopFlag = false;
        try {
            while(!mStopFlag) {
                Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();

                while (nis.hasMoreElements()) {
                    NetworkInterface ni = nis.nextElement();
                    if (!ni.isLoopback()) {
                        List<InterfaceAddress> ias = ni.getInterfaceAddresses();

                        for(InterfaceAddress addr: ias) {
                            InetAddress broadcastIA = addr.getBroadcast();
                            if(broadcastIA != null) {
                                DatagramSocket datagramSocket = new DatagramSocket();
                                datagramSocket.setBroadcast(true);
                                DatagramPacket datagramPacket = new DatagramPacket(sPublicKey, sPublicKey.length,
                                        broadcastIA, BROADCAST_PORT);
                                datagramSocket.send(datagramPacket);
                                datagramSocket.close();
                            }
                        }
                    }
                }
                Thread.sleep(1000);
            }
        } catch(InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}