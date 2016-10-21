package sxccal.edu.android.remouse.net.server;

import sxccal.edu.android.remouse.security.EKEProvider;

import java.io.IOException;

/**
 * @author Sudipto Bhattacharjee
 * @author Sayantan Majumdar
 */
public class NetworkManager {

    private BroadcastThread mBroadcastThread;
    private Thread mThread;

    public static byte[] sPublicKey;

    public static final int TCP_PORT = 1234;

    public NetworkManager() {
        sPublicKey = new EKEProvider().getBase64EncodedPubKey();
        mBroadcastThread = new BroadcastThread();
        System.out.println("Phone public key: " + new String(sPublicKey));
    }

    void startBroadcast() throws IOException, InterruptedException {
        mThread = new Thread(mBroadcastThread);
        mThread.start();
        System.out.println("Broadcast started ...");
    }

    public void stopBroadcast() throws IOException {
        if(mBroadcastThread != null )   {
            mBroadcastThread.stopBroadcast();
            System.out.println("Broadcast stopped!");
        }
        mThread = null;
    }
}
