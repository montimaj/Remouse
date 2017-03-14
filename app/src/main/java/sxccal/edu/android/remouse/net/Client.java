package sxccal.edu.android.remouse.net;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import sxccal.edu.android.remouse.security.EKEProvider;
import sxccal.edu.android.remouse.sensor.representation.Quaternion;

import static sxccal.edu.android.remouse.MainActivity.DEVICE_NAME;
import static sxccal.edu.android.remouse.MainActivity.sPublicKey;

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

    private EKEProvider mEKEProvider;

    public Client(String address) throws IOException {
        sSocket = new Socket(address, TCP_PORT);
        sOut = new PrintWriter(sSocket.getOutputStream(), true);
        String gsonStr = new Gson().toJson(new ClientInfo(DEVICE_NAME, sPublicKey));
        sOut.println(gsonStr);
    }

    void setEKEProvider(EKEProvider ekeProvider) { mEKEProvider = ekeProvider; }

    void sendPairingKey(String pairingKey) { sOut.println(pairingKey); }

    BufferedReader getSocketReader() throws IOException {
        return new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
    }

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
        sOut.println(mEKEProvider.encryptString(data));
    }

    public void sendStopSignal(boolean makeSecured) {
        if(makeSecured && mEKEProvider != null) {
            sendData("Stop","");
        } else  sOut.println("Cancelled");
    }

    public void close() throws IOException {
        if(sSocket != null) sSocket.close();
    }
}