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
 * @author Sudipto Bhattacharjee
 * @author Sayantan Majumdar
 */
public class Client {

    private static Socket sSocket;
    private static PrintWriter sOut;
    private static BufferedReader sIn;

    private EKEProvider mEKEProvider;

    public static boolean sConnectionAlive;

    public Client(String address, int port) throws IOException {
        sSocket = new Socket(address, port);
        sOut = new PrintWriter(sSocket.getOutputStream(), true);
        sIn = new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
    }

    public Client(String pairingKey) {
        mEKEProvider = new EKEProvider(pairingKey, sServerPublicKey);
    }

    public void sendPairingKey(String pairingKey) throws IOException {
        sOut.println(mEKEProvider.encryptString(pairingKey));
    }

    public boolean getConfirmation() throws IOException {
        String s = mEKEProvider.decryptString(sIn.readLine());
        return s != null && s.equals("1");
    }
    
    public void sendMouseData(int x, int y) throws IOException {
        String data = "Mouse " + x + " " + y ;
        Log.d("Client Sent String: ", data);
        sOut.println(mEKEProvider.encryptString(data));
    }

    public void sendStopSignal() {
        sOut.println(mEKEProvider.encryptString("Stop"));
    }

    public void sendKeyboardData(String s) throws IOException {
        String data = "Key " + s;
        Log.d("Client Sent String: ", data);
        sOut.println(mEKEProvider.encryptString(data));
    }

    public boolean getStopSignal() throws IOException {
        String s = mEKEProvider.decryptString(sIn.readLine());
        return s != null && s.equals("-1");
    }

    public void close() throws IOException {
        sSocket.close();
    }
}