package sxccal.edu.android.remouse.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client module
 * @author Sudipto <ttsudipto@gmail.com>, Sayantan <monti.majumdar@gmail.com>
 */
public class Client {

    private int mPort;
    private Socket mSocket;
    private PrintWriter mPrintWriter;
    private BufferedReader mBufferedReader;

    public Client(String address, int port) throws IOException {
        mPort = port;
        mSocket = new Socket(address, mPort);
    }
    
    public Client(InetAddress inetAddress, int port) throws IOException {
        mPort = port;
        mSocket = new Socket(inetAddress, mPort);
    }

    public boolean getConfirmation() throws IOException {
        mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        String s = mBufferedReader.readLine();
        return s.equals("1");
    }
    
    public void send(int k, int x, int y) throws IOException {
        mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
        String s = k + " " + x + " " + y ;
        Log.d("Client Sent String: ", s);
        mPrintWriter.println(s);
    }
    
    public void close() throws IOException {
        mSocket.close();
    }
}