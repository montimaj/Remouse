package project.android.net;


import java.util.concurrent.LinkedBlockingQueue;

import static project.android.ConnectionFragment.sSecuredClient;

/**
 *
 * Created by sudipto on 23/3/17.
 */
public class KeyboardThread implements Runnable {

    private boolean mStopFlag;
    private LinkedBlockingQueue<String> mBuffer;

    public KeyboardThread() {
        mStopFlag = false;
        mBuffer = new LinkedBlockingQueue<>();
    }

    public void addToBuffer(String data) {
        try {
            mBuffer.put(data);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void setStopFlag() { mStopFlag = true; }

    @Override
    public void run() {
        while(!mStopFlag) {
            try {
                String data = mBuffer.take();
                sSecuredClient.sendData("Key", data);
//                Log.d("Keyboard sent", data + "__>" + data.length());
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
