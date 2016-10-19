package sxccal.edu.android.remouse.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client module
 * @author Sudipto Bhattacharjee
 * @author Sayantan Majumdar
 */
public class Client {

    private Socket mSocket;
    private PrintWriter mPrintWriter;
    private BufferedReader mBufferedReader;

    public static boolean sConnectionAlive;

    public Client(String address, int port) throws IOException {
        mSocket = new Socket(address, port);
        mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
        mBufferedReader = new BufferedReader(new InputStreamReader(
                mSocket.getInputStream()));
    }

    public void sendPairingKey(String pairingKey) throws IOException {
        mPrintWriter.println(pairingKey);
    }

    public boolean getConfirmation() throws IOException {
        return mBufferedReader.readLine().equals("1");
    }
    
    public void sendMouseData(int x, int y) throws IOException {
        String data = "Mouse " + x + " " + y ;
        Log.d("Client Sent String: ", data);
        mPrintWriter.println(data);
    }

    public void sendStopSignal() { mPrintWriter.println("Stop"); }

    public void sendKeyboardData(String s) throws IOException {
        Log.d("Client Sent String: ", s);
        String data = "Keyboard " + s;
        mPrintWriter.println(data);
    }

    public boolean getStopSignal() throws IOException {
        String s = mBufferedReader.readLine();
        return s != null && s.equals("-1");
    }
    
    public void close() throws IOException {
        mSocket.close();
    }
}