package project.android.net;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.concurrent.LinkedBlockingQueue;

import static project.android.ConnectionFragment.sSecuredClient;

/**
 * Implementation of the <code>java.lang.Runnable</code> for the thread responsible
 * for sending keyboard data to the server.
 *
 * <p>
 *     This thread maintains a buffer to store the data to be sent to the server. It
 *     is implemented as a <code>java.util.concurrent.LinkedBlockingQueue</code>. The
 *     data contains two fields :
 *     <ul>
 *         <li>
 *             A data field which represents the data to be sent, and
 *         </li>
 *         <li>
 *             A boolean flag which determines if the data represents a special key.
 *         </li>
 *     </ul>
 * </p>
 * <p>
 *     The data is inserted to the buffer by the listener methods defined in the
 *     {@link project.android.KeyboardFragment} class. This thread continuously
 *     monitors the buffer to check if data is available. If available, it sends
 *     the data.
 * </p>
 * <p>
 *     This thread is started when the keyboard UI is loaded and the
 *     {@link project.android.KeyboardFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
 *     method is called. It is stopped by invoking the {@link #setStopFlag()}
 *     method when the keyboard UI is destroyed and the
 *     {@link project.android.KeyboardFragment#onDestroyView()} method is called.
 * </p>
 *
 * @see java.lang.Runnable
 * @see java.util.concurrent.LinkedBlockingQueue
 * @see project.android.KeyboardFragment
 * @see project.android.KeyboardFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
 * @see project.android.KeyboardFragment#onDestroyView()
 * @see #setStopFlag()
 */
public class KeyboardThread implements Runnable {

    private boolean mStopFlag;
    private LinkedBlockingQueue<Pair<String, Boolean>> mBuffer;

    /**
     * Constructor.
     *
     * Initializes this thread.
     */
    public KeyboardThread() {
        mStopFlag = false;
        mBuffer = new LinkedBlockingQueue<>();
    }

    /**
     * Inserts keyboard data to the buffer.
     *
     * This data is sent to the server by this <code>KeyboardThread</code>.
     *
     * @param data the data to be sent.
     * @param isSpecialKey <code>true</code>, if <code>data</code> represents
     *                     special key, <br/>
     *                     <code>false</code>, otherwise.
     */
    public void addToBuffer(String data, boolean isSpecialKey) {
        try {
            mBuffer.put(Pair.create(data, isSpecialKey));
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    /**
     * Sets stop flag.
     *
     * Used to exit from the {@link #run()} method.
     */
    public void setStopFlag() { mStopFlag = true; }

    /**
     * Performs the sending of keyboard data in the buffer to the server.
     *
     * <p>
     *     It checks if data is available in the buffer.
     *     <ul>
     *         <li>If available, it sends the data.</li>
     *         <li>Otherwise, it waits for the data to be available.</li>
     *     </ul>
     * </p>
     */
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
