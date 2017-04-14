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
 * Class representing the client module.<br/>
 *
 * <p>
 *     The constructor {@link Client#Client(String)} is used to set up a TCP connection with the respective
 *     server. The data obtained from the {@link project.android.KeyboardFragment}, {@link project.android.MouseFragment}
 *     and {@link project.android.TouchpadFragment} are wrapped using {@link DataWrapper} which
 *     internally uses the <code>com.google.Gson</code> API. The wrapped <code>JSON</code> string is
 *     encrypted using {@link project.android.security.EKEProvider#encryptString(String)} and
 *     transmitted over the network.
 * </p>
 * @see project.android.net.DataWrapper
 * @see project.android.security.EKEProvider
 */
public class Client {

    private DataWrapper mDataWrapper;

    private static Socket sSocket;
    private static PrintWriter sOut;
    private static final int TCP_PORT = 1234;

    private EKEProvider mEKEProvider;

    /**
     * Constructor.
     * Initializes this <code>Client</code>.
     * @param address IPv4 address of the server.
     * @throws IOException
     */
    public Client(String address) throws IOException {
        sSocket = new Socket(address, TCP_PORT);
        sOut = new PrintWriter(sSocket.getOutputStream(), true);
        String gsonStr = new Gson().toJson(new ClientInfo(DEVICE_NAME, sPublicKey));
        sOut.println(gsonStr);
    }

    /**
     * Sets {@link project.android.security.EKEProvider} object.
     * @param ekeProvider {@link project.android.security.EKEProvider} object.
     */
    void setEKEProvider(EKEProvider ekeProvider) { mEKEProvider = ekeProvider; }

    /**
     * Send the pairing key provided by the app user to the server.
     * @param pairingKey pairing key provided by the app user.
     */
    void sendPairingKey(String pairingKey) { sOut.println(pairingKey); }

    /**
     * Get <code>BufferedReader</code> object.
     * @return <code>BufferedReader</code> object.
     * @throws IOException
     */
    BufferedReader getSocketReader() throws IOException {
        return new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
    }

    /**
     * Send mouse button or keyboard data.
     * @param operationType type of the operation.
     * @param data data to be sent.
     * @see project.android.net.DataWrapper#DataWrapper(String, String)
     */
    public void sendData(String operationType, String data) {
        mDataWrapper = new DataWrapper(operationType, data);
        data = DataWrapper.getGsonString(mDataWrapper);
        data = mEKEProvider.encryptString(data);
        sOut.println(data);
    }

    /**
     * Send special key data
     * @param data special key data
     * @see project.android.net.DataWrapper#DataWrapper(String)
     */
    public void sendData(String data) {
        mDataWrapper = new DataWrapper(data);
        data = DataWrapper.getGsonString(mDataWrapper);
        data = mEKEProvider.encryptString(data);
        sOut.println(data);
    }

    /**
     * Send 2D mouse movement data.
     * @param x relative x coordinate.
     * @param y relative y coordinate.
     * @see project.android.net.DataWrapper#DataWrapper(int, int, float)
     */
    public void sendData(int x, int y) {
        mDataWrapper = new DataWrapper(x, y, sMouseSensitivity2d);
        String data = new Gson().toJson(mDataWrapper);
        sOut.println(mEKEProvider.encryptString(data));
    }

    /**
     * Send 3D mouse movement data.
     * @param quaternion {@link project.android.sensor.representation.Quaternion} object.
     * @param isInitQuat <code>true</code> if inital {@link project.android.sensor.representation.Quaternion} object,<br/>
     *                   <code>false</code> otherwise.
     * @see project.android.net.DataWrapper#DataWrapper(Quaternion, boolean, float)
     */
    public void sendData(Quaternion quaternion, boolean isInitQuat) {
        mDataWrapper = new DataWrapper(quaternion, isInitQuat, sMouseSensitivity3d);
        String data = new Gson().toJson(mDataWrapper);
        sOut.println(mEKEProvider.encryptString(data));
    }

    /**
     * Send a stop signal if the user disconnects the server from the app.
     * @param makeSecured <code>true</code> for valid pairing key, <br/>
     *                    <code>false</code> if pairing key alert dialog was cancelled.
     * @see project.android.security.EKEProvider
     */
    public void sendStopSignal(boolean makeSecured) {
        if(makeSecured && mEKEProvider != null) {
            sendData("Stop","");
        } else  sOut.println("Cancelled");
    }

    /**
     * Close client socket.
     * @throws IOException
     */
    public void close() throws IOException {
        if(sSocket != null) sSocket.close();
    }
}