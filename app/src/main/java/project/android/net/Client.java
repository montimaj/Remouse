package project.android.net;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import project.android.security.EKEProvider;
import project.android.sensor.representation.Quaternion;

import static project.android.MainActivity.DEVICE_NAME;
import static project.android.MainActivity.sPublicKey;
import static project.android.SettingsActivity.sMouseSensitivity2d;
import static project.android.SettingsActivity.sMouseSensitivity3d;

/**
 * Client module
 * @author Sayantan Majumdar
 * @author Sudipto Bhattacharjee
 */
public class Client {

    private DataWrapper mDataWrapper;

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
        mDataWrapper = new DataWrapper(operationType, data);
        data = DataWrapper.getGsonString(mDataWrapper);
        data = mEKEProvider.encryptString(data);
        sOut.println(data);
    }

    public void sendData(int x, int y) {
        mDataWrapper = new DataWrapper(x, y, sMouseSensitivity2d);
        String data = new Gson().toJson(mDataWrapper);
        sOut.println(mEKEProvider.encryptString(data));
    }

    public void sendData(Quaternion quaternion, boolean isInitQuat) {
        mDataWrapper = new DataWrapper(quaternion, isInitQuat, sMouseSensitivity3d);
        String data = new Gson().toJson(mDataWrapper);
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