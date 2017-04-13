package project.android.net;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

import project.android.ConnectionFragment;
import project.android.MainActivity;
import project.android.security.EKEProvider;

import static project.android.ConnectionFragment.sConnectionAlive;
import static project.android.ConnectionFragment.sSelectedServer;
import static project.android.ConnectionFragment.sSecuredClient;

/**
 * Class representing the client-server interaction module.<br/>
 *
 * {@link ConnectionTask} starts this thread by invoking {@link ConnectionTask#onPostExecute(EKEProvider)}.
 * This class is responsible for enabling/disabling client-server connection.
 * @see project.android.net.ConnectionTask
 * @see project.android.net.Client
 * @see project.android.net.NetworkService
 */
class ClientIOThread implements Runnable {

    private Activity mActivity;
    private EKEProvider mEKEProvider;
    private ServerInfo mServerInfo;
    private String mAddress;
    private boolean mStopFlag;

    /**
     * Constructor.<br/>
     * Initializes this <code>ClientIOThread</code>.
     * @param activity The current <code>android.app.Activity</code> object.
     * @param serverInfo {@link ServerInfo} object.
     * @param ekeProvider {@link project.android.security.EKEProvider} object.
     * @throws IOException
     */
    ClientIOThread(Activity activity, final ServerInfo serverInfo, EKEProvider ekeProvider) throws IOException {
        mActivity = activity;
        mServerInfo = serverInfo;
        mAddress = serverInfo.getAddress();
        mEKEProvider = ekeProvider;
        sSecuredClient.setEKEProvider(ekeProvider);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sSecuredClient.sendPairingKey(mEKEProvider.encryptString(serverInfo.getPairingKey()));
            }
        }).start();
    }

    /**
     * Performs operations for reading data from a server.
     * <ul>
     *     <li>Reads stop signal sent from server and disables the respective server</li>
     *     <li>Starts {@link NetworkService} upon successful server connection</li>
     * </ul>
     * @see project.android.ConnectionFragment
     */
    @Override
    public void run() {
        try {
            while (!mStopFlag) {
                recieveData();
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sSelectedServer.remove(mServerInfo);
                    ConnectionFragment connectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();
                    try {
                        connectionFragment.resetIcon(mServerInfo);
                    } catch (RuntimeException ignored) {}
                    Toast.makeText(mActivity, "Server " + mAddress + " disconnected! ", Toast.LENGTH_SHORT).show();
                }
            });
            sSecuredClient.close();
        } catch (IOException ignored) {}
    }

    private void recieveData() throws IOException {
        BufferedReader in = sSecuredClient.getSocketReader();
        if(!in.ready()) return;
        String s = mEKEProvider.decryptString(in.readLine());
        if(s != null) {
            if (s.equals("Stop")) {
                sConnectionAlive.put(mAddress, false);
                mStopFlag = true;
                return;
            }
            sConnectionAlive.put(mAddress, s.equals("1"));
        }
        if (s == null || !sConnectionAlive.get(mAddress)) {
            displayError(mActivity);
        } else {
            Intent intent = new Intent(mActivity, NetworkService.class);
            intent.putExtra("Server", mAddress);
            mActivity.startService(intent);
        }
    }

    private void displayError(final Activity activity) throws IOException {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Incorrect pin! Try connecting again",
                        Toast.LENGTH_LONG).show();
                sSelectedServer.remove(mServerInfo);
            }
        });
        sSecuredClient.close();
    }
}