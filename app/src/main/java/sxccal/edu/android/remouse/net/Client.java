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
class Client {

    private Socket mSocket;

    Client(String address, int port) throws IOException {
        mSocket = new Socket(address, port);
    }
    
    /*public lient(InetAddress inetAddress, int port) throws IOException {
        mPort = port;
        mSocket = new Socket(inetAddress, mPort);
    }*/

    void sendPairingKey(String pairingKey) throws IOException {
        PrintWriter printWriter = new PrintWriter(mSocket.getOutputStream(), true);
        printWriter.println(pairingKey);
    }

    boolean getConfirmation() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                mSocket.getInputStream()));
        String s = bufferedReader.readLine();
        return s.equals("1");
    }
    
    void send(int k, int x, int y) throws IOException {
        PrintWriter printWriter = new PrintWriter(mSocket.getOutputStream(), true);
        String s = k + " " + x + " " + y ;
        Log.d("Client Sent String: ", s);
        printWriter.println(s);
    }
    
    void close() throws IOException {
        mSocket.close();
    }
}