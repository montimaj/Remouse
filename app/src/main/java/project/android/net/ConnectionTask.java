package project.android.net;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;

import project.android.ConnectionFragment;
import project.android.MainActivity;
import project.android.security.EKEProvider;

/**
 * Class representing the <code>android.os.AsyncTask</code> used for the following operations:<br/>
 *
 * <p>
 *     <ul>
 *         <li>
 *             Displaying a <code>android.app.ProgressDialog</code> in {@link ConnectionTask#onPreExecute()}.
 *         </li>
 *         <li>
 *             Constructing {@link project.android.security.EKEProvider#EKEProvider(byte[], byte[])} object
 *             in {@link ConnectionTask#doInBackground(ServerInfo...)} that will be used for
 *             encryption and decryption of TCP packets.
 *         </li>
 *         <li>
 *             Running {@link ClientIOThread} in {@link ConnectionTask#onPostExecute(EKEProvider)}
 *             after {@link ConnectionTask#doInBackground(ServerInfo...)} has been completed.
 *         </li>
 *     </ul>
 * </p>
 * @see project.android.ConnectionFragment
 * @see project.android.net.ClientIOThread
 * @see project.android.net.ServerInfo
 * @see project.android.security.EKEProvider
 */

public class ConnectionTask extends AsyncTask<ServerInfo, Void, EKEProvider> {

    private ProgressDialog mProgessDialog;
    private ConnectionFragment mConnectionFragment;

    /**
     * Overrides the <code>android.os.AsyncTask.onPreExecute()</code> method of the Android API.
     *
     * Runs on the UI thread before {@link ConnectionTask#doInBackground(ServerInfo...)}.
     * @see project.android.net.ConnectionTask#doInBackground(ServerInfo...)
     * @see project.android.net.ConnectionTask#onPostExecute(EKEProvider)
     */
    @Override
    protected void onPreExecute() {
        mConnectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();
        mProgessDialog = ProgressDialog.show(mConnectionFragment.getContext(), "Connecting...", "Please wait!", true, false);
    }

    /**
     * Overrides the <code>android.os.AsyncTask.doInBackground(Params...) method of the Android API.
     *
     * Performs a computation on a background thread
     * @param serverInfos {@link ServerInfo} object(s)
     * @return {@link project.android.security.EKEProvider#EKEProvider(byte[], byte[])} object
     * @see project.android.net.ConnectionTask#onPreExecute()
     * @see project.android.net.ConnectionTask#onPostExecute(EKEProvider)
     * @see project.android.net.ServerInfo
     */
    @Override
    protected EKEProvider doInBackground(ServerInfo... serverInfos) {
        return new EKEProvider(serverInfos[0].getPairingKey().getBytes(), serverInfos[0].getServerPubKey());
    }

    /**
     * Overrides the <code>android.os.AsyncTask.onPostExecute(Result)</code> of the Android API.
     *
     * This method won't be invoked if the task was cancelled.
     * @param ekeProvider {@link project.android.security.EKEProvider} object returned by {@link ConnectionTask#doInBackground(ServerInfo...)}.
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
