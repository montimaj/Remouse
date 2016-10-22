package sxccal.edu.android.remouse.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import sxccal.edu.android.remouse.security.EKEProvider;

import static sxccal.edu.android.remouse.net.ClientConnectionThread.sServerPublicKey;

/**
 * Client module
 * @author Sayantan Majumdar
 * @author Sudipto Bhattacharjee
 */
public class Client {

    private static Socket sSocket;
    private static PrintWriter sOut;
    private static final int TCP_PORT = 1234;

    static final byte[] PUBLIC_KEY = new EKEProvider().getBase64EncodedPubKey();
    static BufferedReader sIn;

    private EKEProvider mEKEProvider;

    public Client(String address) throws IOException {
        sSocket = new Socket(address, TCP_PORT);
        sOut = new PrintWriter(sSocket.getOutputStream(), true);
        sOut.println(new String(PUBLIC_KEY));
        sIn = new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
    }

    Client(byte[] pairingKey) {
        mEKEProvider = new EKEProvider(pairingKey, sServerPublicKey);
    }

    void sendPairingKey(String pairingKey) throws IOException {
        sOut.println(mEKEProvider.encryptString(pairingKey));
    }

    BufferedReader getSocketReader() throws IOException {
        return new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
    }

    EKEProvider getEKEProvider() {
        return mEKEProvider;
    }
    
    public void sendMouseData(int x, int y) throws IOException {
        String data = "Mouse " + x + " " + y ;
        Log.d("Client Sent String: ", data);
        sOut.println(mEKEProvider.encryptString(data));
    }

    public void sendStopSignal(boolean makeSecured) {
        if(makeSecured && mEKEProvider != null) {
            sOut.println(mEKEProvider.encryptString("Stop"));
        } else  sOut.println("Stop");
    }

    public void sendKeyboardData(String s) throws IOException {
        String data = "Key " + s;
        Log.d("Client Sent String: ", data);
        sOut.println(mEKEProvider.encryptString(data));
    }

    public void close() throws IOException {
        if(sSocket != null) sSocket.close();
    }
}