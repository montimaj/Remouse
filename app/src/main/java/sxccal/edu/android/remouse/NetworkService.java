package sxccal.edu.android.remouse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;

import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;
import static sxccal.edu.android.remouse.ConnectionFragment.sConnectionAlive;
import static sxccal.edu.android.remouse.MainActivity.sFragmentList;
import static sxccal.edu.android.remouse.MouseFragment.sMouseAlive;

public class NetworkService extends Service {

    private static String sAddress;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ConnectionFragment connectionFragment = (ConnectionFragment) sFragmentList.get(0);
        connectionFragment.setImage(connectionFragment.listItemPos, R.mipmap.connect_icon);
        sAddress = intent.getStringExtra("Server");
        Toast.makeText(this, "Connected to " + sAddress + "\nOpen either Mouse " +
                        "or Keyboard Tabs from the navigation bar",
                Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

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