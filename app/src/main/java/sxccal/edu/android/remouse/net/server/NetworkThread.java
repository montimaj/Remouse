package sxccal.edu.android.remouse.net.server;

/**
 * @author Sudipto Bhattacharjee
 */
public class NetworkThread implements Runnable {

    private NetworkManager manager;

    public NetworkThread(NetworkManager manager) { this.manager = manager; }

    @Override
    public void run() {
        try {
            manager.startBroadcast();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
