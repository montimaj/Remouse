package project.android.net;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;;

import project.android.ConnectionFragment;
import project.android.MainActivity;

import static project.android.ConnectionFragment.sConnectionAlive;

/**
 * Implementation of the <code>java.lang.Runnable</code> for the thread
 * responsible for receiving broadcast from the server.
 *
 * <p>
 *     This class receives the UDP packets broadcasted by the server and updates
 *     the <code>ListView</code> in the {@link project.android.ConnectionFragment}
 *     accordingly. This thread is started by the
 *     {@link ConnectionFragment#onViewCreated(View, Bundle)} method and it runs
 *     until the app is closed.
 * </p>
 *
 * @see project.android.ConnectionFragment
 * @see project.android.net.ServerInfo
 * @see project.android.ConnectionFragment#onViewCreated(View, Bundle)
 */
public class BroadcastReceiverThread implements Runnable {

    private Activity mActivity;
    private WifiManager.MulticastLock mLock;
    private ArrayList<String> mServerAddress;
    private DatagramSocket mDatagramSocket;
    private ConnectionFragment mConnectionFragment;

    private static final int UDP_PORT = 1235;

    /**
     * Constructor.<br/>
     *
     * Initializes this <code>BroadcastReceiverThread</code>.
     *
     * @param activity The current <code>android.app.Activity</code> object.
     * @param lock an object of <code>WifiManager.MulticastLock</code> class
     *             of the Android API.
     */
    public BroadcastReceiverThread(Activity activity, WifiManager.MulticastLock lock) {
        mActivity = activity;
        mLock = lock;
        mDatagramSocket = null;
        mServerAddress = new ArrayList<>();
        mConnectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();;
    }

    /**
     * Performs the broadcast receiving operation.
     *
     * <ul>
     *     <li>
     *         Checks whether a stop signal has been broadcast by the server. If so,
     *         removes the {@link ServerInfo} object from the <code>ListView</code> in
     *         the {@link project.android.ConnectionFragment}.
     *     </li>
     *     <li>
     *         Constructs a {@link ServerInfo} object from the data obtained from the
     *         <code>DatagramPacket</code> using <code>com.google.Gson</code> API and
     *         updates the <code>ListView</code> in the
     *         {@link project.android.ConnectionFragment} accordingly.
     *     </li>
     * </ul>
     *
     * @see project.android.ConnectionFragment
     */
    @Override
    public void run() {
        try {
            mLock.acquire();
            if (mDatagramSocket == null) {
                mDatagramSocket = new DatagramSocket(UDP_PORT);
                mDatagramSocket.setBroadcast(true);
            }
        } catch (IOException e) { e.printStackTrace(); }

        while (mDatagramSocket != null) {
            DatagramPacket datagramPacket = null;
            try {
                if (!mLock.isHeld()) mLock.acquire();
                datagramPacket = new DatagramPacket(new byte[ServerInfo.SERVER_INFO_LENGTH],
                        ServerInfo.SERVER_INFO_LENGTH);
                mDatagramSocket.receive(datagramPacket);
                mLock.release();
            } catch (IOException ignored) {}
            String gsonString = new String(datagramPacket.getData());
            gsonString = gsonString.substring(0, gsonString.indexOf("}") + 1);
            ServerInfo serverInfo = new Gson().fromJson(gsonString, ServerInfo.class);
            String serverAddress = datagramPacket.getAddress().toString().substring(1);
            serverInfo.setServerAddress(serverAddress);

            checkAvailableDevices(serverInfo, serverAddress);
        }
    }
    private void checkAvailableDevices(final ServerInfo serverInfo, final String serverAddress) {

        if (serverInfo.getStopFlag()) {
            mServerAddress.remove(serverAddress);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "Server " + serverInfo.getAddress() + " stopped!",
                            Toast.LENGTH_SHORT).show();
                    mConnectionFragment.removeItem(serverInfo);
                    mConnectionFragment.dismissAlertDialog();
                }
            });
        } else {
            if (mServerAddress.isEmpty() || !mServerAddress.contains(serverAddress)) {
                mServerAddress.add(serverAddress);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnectionFragment.addItem(serverInfo);
                        sConnectionAlive.put(serverAddress, false);
                    }
                });
            }
        }
    }
}