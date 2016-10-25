package sxccal.edu.android.remouse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;

import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;
import static sxccal.edu.android.remouse.MouseFragment.sMouseAlive;
import static sxccal.edu.android.remouse.net.ClientIOThread.sConnectionAlive;

public class NetworkService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Connected to " + intent.getStringExtra("Server") +
                        "\nOpen either Mouse or Keyboard Tabs from the navigation bar",
                Toast.LENGTH_LONG).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sConnectionAlive) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sSecuredClient.sendStopSignal(true);
                    sMouseAlive = false;
                    try {
                        sSecuredClient.close();
                        sConnectionAlive = false;
                    } catch (IOException e) {}
                }
            }).start();
            Toast.makeText(this, "Disconnected Successfully", Toast.LENGTH_LONG).show();
        }
    }
}
