package project.android.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;

import project.android.ConnectionFragment;
import project.android.MainActivity;
import project.android.security.EKEProvider;

import static project.android.ConnectionFragment.sSecuredClient;
import static project.android.ConnectionFragment.sConnectionAlive;
import static project.android.MouseFragment.sMouseAlive;

/**
 * Class representing the <code>android.app.Service</code> that runs upon successful client-server connection.<br/>
 *
 * <p>
 *     {@link ClientIOThread} starts this service when a successful client-server
 *     connection has been established. The <code>android.app.Service</code> is destroyed when {@link NetworkService#onDestroy()}
 *     is invoked upon app closing.
 * </p>
 * @see project.android.net.ConnectionTask#onPostExecute(EKEProvider)
 * @see project.android.net.ClientIOThread#recieveData()
 * @see project.android.MainActivity#onDestroy()
 */
public class NetworkService extends Service {

    private static String sAddress;

    /**
     * Overrides the <code>android.app.Service.onBind(Intent)</code>.<br/>
     *
     * Return the communication channel to the service. May return null if clients can not bind to the service.
     * @param intent The Intent that was used to bind to this service, as given to <code>Context.bindService</code>.
     *               Note that any extras that were included with the Intent at that point will not be seen here.
     * @return <code>IBinder</code> through which clients can call on to the service.
     */
    @Override
    public IBinder onBind(Intent intent) { return null; }

    /**
     * Overrides the <code>android.app.Service.onStartCommand(Intent, int, int)</code>.<br/>
     *
     * Called by the system every time a client explicitly starts the service by calling <code>startService(Intent)</code>,
     * providing the arguments it supplied and a unique integer token representing the start request.
     * @param intent The <code>Intent</code> supplied to <code>startService(Intent)</code>, as given.
     *               This may be null if the service is being restarted after its process has gone away,
     *               and it had previously returned anything except <code>START_STICKY_COMPATIBILITY</code>.
     * @param flags Additional data about this start request. Currently either 0, <code>START_FLAG_REDELIVERY</code>,
     *              or <code>START_FLAG_RETRY</code>.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value indicates what semantics the system should use for the service's current started state.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ConnectionFragment connectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();
        connectionFragment.setIcon();
        sAddress = intent.getStringExtra("Server");
        Toast.makeText(this, "Connected to " + sAddress + "\nOpen either Mouse " +
                        "or Keyboard Tabs from the navigation bar",
                Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    /**
     * Overrides the <code>android.app.Service.onDestroy()</code>.<br/>
     * <p>
     *     Called by the system to notify a Service that it is no longer used and is being removed.
     *     The service should clean up any resources it holds (threads, registered receivers, etc) at this point.
     *     Upon return, there will be no more calls in to this <code>android.app.Service</code> object and it is effectively dead.
     * </p>
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sConnectionAlive.get(sAddress)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sSecuredClient.sendStopSignal(true);
                    sMouseAlive = false;
                    try {
                        sSecuredClient.close();
                        sConnectionAlive.put(sAddress, false);
                    } catch (IOException ignored) {}
                }
            }).start();
            Toast.makeText(this, sAddress + " disconnected successfully", Toast.LENGTH_SHORT).show();
        }
    }
}