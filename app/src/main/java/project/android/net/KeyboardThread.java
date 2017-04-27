package project.android.net;

import android.util.Pair;

import java.util.concurrent.LinkedBlockingQueue;

import static project.android.ConnectionFragment.sSecuredClient;

/**
 *
 * Created by sudipto on 23/3/17.
 */
public class KeyboardThread implements Runnable {

    private boolean mStopFlag;
    private LinkedBlockingQueue<Pair<String, Boolean>> mBuffer;

    public KeyboardThread() {
        mStopFlag = false;
        mBuffer = new LinkedBlockingQueue<>();
    }

    public void addToBuffer(String data, boolean isSpecialKey) {
        try {
            mBuffer.put(Pair.create(data, isSpecialKey));
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void setStopFlag() { mStopFlag = true; }

    @Override
    public void run() {
        while(!mStopFlag) {
            try {
                Pair<String, Boolean> data = mBuffer.take();
                if(data.second) {
                    sSecuredClient.sendData(data.first);
                } else sSecuredClient.sendData("Key", data.first);
//                Log.d("Keyboard sent", data.first + "__>" + data.first.length());
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
