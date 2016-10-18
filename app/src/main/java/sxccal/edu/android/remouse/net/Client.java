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

    public Client(String address, int port) throws IOException {
        mSocket = new Socket(address, port);
    }

    public void sendPairingKey(String pairingKey) throws IOException {
        PrintWriter printWriter = new PrintWriter(mSocket.getOutputStream(), true);
        printWriter.println(pairingKey);
    }

    private String readSocketData() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                mSocket.getInputStream()));
        return bufferedReader.readLine();
    }

    public boolean getConfirmation() throws IOException {
        return readSocketData().equals("1");
    }
    
    public void sendMouseData(int k, int x, int y) throws IOException {
        PrintWriter printWriter = new PrintWriter(mSocket.getOutputStream(), true);
        String s = k + " " + x + " " + y ;
        Log.d("Client Sent String: ", s);
        printWriter.println(s);
    }

    public boolean getStopSignal() throws IOException {
        return readSocketData().equals("-1");
    }
    
    public void close() throws IOException {
        mSocket.close();
    }
}