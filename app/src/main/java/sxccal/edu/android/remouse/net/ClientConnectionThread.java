package sxccal.edu.android.remouse.net;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import sxccal.edu.android.remouse.ConnectionFragment;

/**
 * Client to Server connection
 * @author Sudipto <ttsudipto@gmail.com>, Sayantan <monti.majumdar@gmail.com>
 */
public class ClientConnectionThread implements Runnable {

    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(1235);
            Log.d("ClientConnection: ", "Connecting...");
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(new byte[32],32);
            datagramSocket.receive(datagramPacket);
            Log.d("ClientConnection: ", new String(datagramPacket.getData()));
            Log.d("ClientConnection: ", ""+datagramSocket.getInetAddress());
            InetAddress inetAddress = datagramPacket.getAddress();
            Log.d("ClientConnection: ", datagramPacket.getAddress().toString());
            Client client= new Client(inetAddress, 1234);
            if(!client.getConfirmation()) {
                Log.d("ClientConnection: ","Declined by server. Aborting !!!");
            } else {
                int x=0, y=0, k=0;
                while(k++ < 10) {
                    client.send(k, x, y);
                    x+=50;
                    y+=50;
                    Log.d("ClientConnection: ",""+x+" "+y);
                    Thread.sleep(2000);
                }
                client.close();
                datagramSocket.close();
                ConnectionFragment.sActiveConnection = false;
            }
        }catch(IOException|InterruptedException e) {
            e.printStackTrace();
        }
    }
}
