package sxccal.edu.android.remouse.net;

import java.net.Socket;

/**
 * @author Sayantan Majumdar
 */

public class ClientInfo {
    private String mClientInfo;
    private byte[] mPublicKey;
    private Socket mClientSocket;

    public ClientInfo(String clientInfo, byte[] publicKey) {
        mClientInfo = clientInfo;
        mPublicKey = publicKey;
    }

    public void setSocket(Socket socket) { mClientSocket = socket; }

    public Socket getSocket() { return mClientSocket; }
    public String getClientInfo() { return mClientInfo; }
    public byte[] getPublicKey() { return mPublicKey; }
}
