package project.android.net;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;

import project.android.ConnectionFragment;
import project.android.MainActivity;
import project.android.security.EKEProvider;

/**
 * Extends the <code>AsyncTask</code> class of the Android API.
 *
 * <p>
 *     It performs the following operations :
 *     <ul>
 *         <li>
 *             Displaying a <code>ProgressDialog</code>.
 *         </li>
 *         <li>
 *             Constructing a {@link project.android.security.EKEProvider} object
 *             that will be used for encryption and decryption of TCP packets.
 *         </li>
 *         <li>
 *             Starting a {@link ClientIOThread}.
 *         </li>
 *     </ul>
 *     <code>AsyncTask</code> enables these operations to performed asynchronously
 *     by defining them in the {@link #onPreExecute()},
 *     {@link #doInBackground(ServerInfo...)} and {@link #onPostExecute(EKEProvider)}
 *     methods respectively. The starting of {@link ClientIOThread} waits for the
 *     construction of the {@link project.android.security.EKEProvider} object.
 *     A <code>ProgressDialog</code> is displayed for this duration.
 * </p>
 *
 * @see project.android.ConnectionFragment
 * @see project.android.net.ClientIOThread
 * @see project.android.net.ServerInfo
 * @see project.android.security.EKEProvider
 */

public class ConnectionTask extends AsyncTask<ServerInfo, Void, EKEProvider> {

    private ProgressDialog mProgessDialog;
    private ConnectionFragment mConnectionFragment;

    /**
     * Overrides the <code>AsyncTask.onPreExecute()</code> method of the Android API.
     *
     * Runs on the UI thread before the
     * {@link ConnectionTask#doInBackground(ServerInfo...)} method.
     *
     * @see project.android.net.ConnectionTask#doInBackground(ServerInfo...)
     * @see project.android.net.ConnectionTask#onPostExecute(EKEProvider)
     */
    @Override
    protected void onPreExecute() {
        mConnectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();
        mProgessDialog = ProgressDialog.show(mConnectionFragment.getContext(), "Connecting...", "Please wait!", true, false);
    }

    /**
     * Overrides the <code>AsyncTask.doInBackground(Params...)</code> method of the
     * Android API.
     *
     * Performs a computation on a background thread.
     *
     * @param serverInfos {@link ServerInfo} object(s).
     * @return {@link project.android.security.EKEProvider} object.
     * @see project.android.net.ConnectionTask#onPreExecute()
     * @see project.android.net.ConnectionTask#onPostExecute(EKEProvider)
     * @see project.android.net.ServerInfo
     */
    @Override
    protected EKEProvider doInBackground(ServerInfo... serverInfos) {
        return new EKEProvider(serverInfos[0].getPairingKey().getBytes(), serverInfos[0].getServerPubKey());
    }

    /**
     * Overrides the <code>AsyncTask.onPostExecute(Result)</code> method of the Android API.
     *
     * This method won't be invoked if the task was cancelled.
     *
     * @param ekeProvider {@link project.android.security.EKEProvider} object returned by
     *                    the {@link ConnectionTask#doInBackground(ServerInfo...)} method.
     * @see project.android.net.ConnectionTask#doInBackground(ServerInfo...)
     * @see project.android.net.ConnectionTask#onPreExecute()
     * @see project.android.net.ClientIOThread
     * @see project.android.security.EKEProvider
     */
    @Override
    protected void onPostExecute(EKEProvider ekeProvider) {
        try {
            mProgessDialog.dismiss();
            ClientIOThread clientIOThread = new ClientIOThread(mConnectionFragment.getActivity(), mConnectionFragment.mServerInfo, ekeProvider);
            new Thread(clientIOThread).start();
        } catch (IOException ignored) {}
    }
}
