package sxccal.edu.android.remouse.net;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;

import sxccal.edu.android.remouse.ConnectionFragment;
import sxccal.edu.android.remouse.MainActivity;
import sxccal.edu.android.remouse.security.EKEProvider;

/**
 * @author Sayantan Majumdar
 */

public class ConnectionTask extends AsyncTask<ServerInfo, Void, EKEProvider> {

    private ProgressDialog mProgessDialog;
    private ConnectionFragment mConnectionFragment;

    @Override
    protected void onPreExecute() {
        mConnectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();
        mProgessDialog = ProgressDialog.show(mConnectionFragment.getContext(), "Connecting...", "Please wait!", true, false);
    }

    @Override
    protected EKEProvider doInBackground(ServerInfo... serverInfos) {
        return new EKEProvider(serverInfos[0].getPairingKey().getBytes(), serverInfos[0].getServerPubKey());
    }

    @Override
    protected void onPostExecute(EKEProvider ekeProvider) {
        try {
            mProgessDialog.dismiss();
            ClientIOThread clientIOThread = new ClientIOThread(mConnectionFragment.getActivity(), mConnectionFragment.mServerInfo, ekeProvider);
            new Thread(clientIOThread).start();
        } catch (IOException ignored) {}
    }
}
