package sxccal.edu.android.remouse.net;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Connect to server.
 */
public class ConnectionManager {

    private Socket mSocket;
    private int mPort = -1;

    public void connectToServer(InetAddress inetAddress, int port) {
        //TODO Add connection module
    }

    public int getLocalPort() {
        return mPort;
    }
}
