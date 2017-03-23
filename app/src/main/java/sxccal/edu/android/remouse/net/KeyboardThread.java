package sxccal.edu.android.remouse.net;

import android.util.Log;
import java.util.LinkedList;

import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;

/**
 *
 * Created by sudipto on 23/3/17.
 */
public class KeyboardThread implements Runnable {

    private boolean mStopFlag;
    private boolean mWait;
    private LinkedList<String> mBuffer;

    public KeyboardThread() {
        mStopFlag = false;
        mWait = false;
        mBuffer = new LinkedList<>();
    }

    public void addToBuffer(String data) {
        while(mWait == true);
        setWait();
        mBuffer.addLast(data);
        clearWait();
    }

    public void setStopFlag() { mStopFlag = true; }
    public void setWait() { mWait = true; }
    public void clearWait() { mWait = false; }

    @Override
    public void run() {
        while(!mStopFlag) {
            if(mWait == false) {
                setWait();
                if (!mBuffer.isEmpty()) {
//                    Log.d("Keyboard Status", mBuffer.size() + "");
                    String data = mBuffer.removeFirst();
                    sSecuredClient.sendData("Key", data);
//                    Log.d("Keyboard sent", data + "__>" + data.length());
                }
                clearWait();
            }
        }
    }
}
