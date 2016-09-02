package sxccal.edu.android.remouse.net;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;


/**
 * @author Sayantan Majumdar <monti.majumdar@gmail.com>
 */
public class ClientIOThread implements Runnable {
    private static Client sClient;
    private Activity mActivity;
    private String mAddress;

    public ClientIOThread(Activity activity, String address) {
        mActivity = activity;
        mAddress = address;
    }

    @Override
    public void run() {
        try {
            sClient = new Client(mAddress, 1234);
            if (!sClient.getConfirmation()) {
                Log.d("ClientConnection: ", "Declined by server. Aborting !!!");
            } else {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Connected to " + mAddress, Toast.LENGTH_LONG).show();
                    }
                });
                int x = 0, y = 0, k = 0;
                while (k++ < 10) {
                    sClient.send(k, x, y);
                    x += 50;
                    y += 50;
                    Log.d("ClientConnection: ", "" + x + " " + y);
                    Thread.sleep(2000);
                }
                sClient.close();
            }
        } catch (IOException | InterruptedException e) { e.printStackTrace(); }
    }
}
