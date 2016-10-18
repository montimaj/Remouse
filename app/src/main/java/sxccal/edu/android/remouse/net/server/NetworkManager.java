package sxccal.edu.android.remouse.net.server;

import sxccal.edu.android.remouse.security.EKEProvider;

import java.io.IOException;

/**
 * @author Sudipto Bhattacharjee
 * @author Sayantan Majumdar
 */
public class NetworkManager {

    private Server mServer;
    private BroadcastThread mBroadcastThread;
    private Thread mThread;
    private NetworkState mState;

    public static byte[] sPublicKey;

    public static final int TCP_PORT = 1234;

    public NetworkManager() {
        mState = new NetworkState();
        sPublicKey = new EKEProvider().getBase64EncodedPubKey();
        mBroadcastThread = new BroadcastThread();
        System.out.println("Phone public key: " + new String(sPublicKey));
    }

    void startServer() throws IOException, InterruptedException {
        mServer = new Server(mState, TCP_PORT);
        mThread = new Thread(mBroadcastThread);
        mThread.start();
        System.out.println("Broadcast started ...");
        System.out.println("Server started ...");
        mServer.listen();
    }

    public void stopServer() throws IOException {
        if(mBroadcastThread != null )   mBroadcastThread.stopBroadcast();
        mThread = null;
        if(mServer != null) {
            mServer.setStopFlag();
            mServer.close();
        }
    }
}
