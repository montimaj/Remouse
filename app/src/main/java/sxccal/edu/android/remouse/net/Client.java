package sxccal.edu.android.remouse.net;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import sxccal.edu.android.remouse.security.EKEProvider;
import sxccal.edu.android.remouse.sensor.representation.Quaternion;

import static sxccal.edu.android.remouse.net.ClientConnectionThread.sServerPublicKey;

/**
 * Client module
 * @author Sayantan Majumdar
 * @author Sudipto Bhattacharjee
 */
public class Client {

    private ClientDataWrapper mClientDataWrapper;

    private static Socket sSocket;
    private static PrintWriter sOut;
    private static final int TCP_PORT = 1234;

    static final byte[] PUBLIC_KEY = new EKEProvider().getBase64EncodedPubKey();

    private EKEProvider mEKEProvider;

    public Client(String address) throws IOException {
        sSocket = new Socket(address, TCP_PORT);
        sOut = new PrintWriter(sSocket.getOutputStream(), true);
        sOut.println(new String(PUBLIC_KEY));
    }

    Client(byte[] pairingKey) {
        mEKEProvider = new EKEProvider(pairingKey, sServerPublicKey);
    }

    void sendPairingKey(String pairingKey) {
        sOut.println(mEKEProvider.encryptString(pairingKey));
    }

    BufferedReader getSocketReader() throws IOException {
        return new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
    }

    EKEProvider getEKEProvider() { return mEKEProvider; }
    

    public void sendData(String operationType, String data) {
        mClientDataWrapper = new ClientDataWrapper(operationType, data);
        data = ClientDataWrapper.getGsonString(mClientDataWrapper);
        data = mEKEProvider.encryptString(data);
        sOut.println(data);
    }

    public void sendData(int x, int y) {
        mClientDataWrapper = new ClientDataWrapper(x,y);
        String data = new Gson().toJson(mClientDataWrapper);
        sOut.println(mEKEProvider.encryptString(data));
    }

    public void sendData(Quaternion quaternion, boolean isInitQuat) {
        mClientDataWrapper = new ClientDataWrapper(quaternion, isInitQuat);
        String data = new Gson().toJson(mClientDataWrapper);
        Log.d("Sensor: ", data);
        sOut.println(mEKEProvider.encryptString(data));
    }

    public void sendStopSignal(boolean makeSecured) {
        if(makeSecured && mEKEProvider != null) {
            sendData("Stop","");
        } else  sOut.println("Stop");
    }

    public void close() throws IOException {
        if(sSocket != null) sSocket.close();
    }
}