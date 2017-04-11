package project.android;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import project.android.net.KeyboardThread;
import project.edu.android.remouse.R;

import static project.android.ConnectionFragment.sSecuredClient;

/**
 * @author Sayantan Majumdar
 */

public class KeyboardFragment extends Fragment implements View.OnKeyListener, TextWatcher {

    private String mLastInput;
    private InputMethodManager mInput;
    private String mLastWord;
    private KeyboardThread mKeyboardThread;

    /**
     * Overrides the <code>android.support.v4.app.Fragment.onCreateView(LayoutInflater, ViewGroup, Bundle)</code>.<br/>
     *
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The <code>LayoutInflater</code> object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate
     *                  the <code>LayoutParams</code> of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return the <code>View</code> for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        mInput = null;
        mLastInput = null;
        if(sSecuredClient != null) {
            mInput = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInput.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            EditText keyboardInput = (EditText) view.findViewById(R.id.keyboard);
            keyboardInput.setText("");
            keyboardInput.setTextSize(18);
            keyboardInput.setOnKeyListener(this);
            keyboardInput.addTextChangedListener(this);
            mKeyboardThread = new KeyboardThread();
            new Thread(mKeyboardThread).start();
            mLastWord = "";
        }
        return view;
    }

    /**
     * Overrides the <code>android.support.v4.app.Fragment.onDestroyView()</code>.<br/>
     *
     * Called when the view previously created by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has been detached from the fragment.
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View view = getView();
        if(mInput != null && view != null) {
            mInput.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(mKeyboardThread != null) mKeyboardThread.setStopFlag();
    }

    /**
     * Overrides the <code>android.support.v4.app.Fragment.onResume()</code>.<br/>
     *
     * Called when the activity will start interacting with the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Overrides the <code>android.support.v4.app.Fragment.onPause()</code>.<br/>
     *
     * Called when the system is about to start resuming a previous activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    /**
     * Overrides the <code>android.text.TextWatcher.beforeTextChanged(CharSequence, int, int, int)</code>.<br/>
     *
     * Called to notify that, within <code>s</code>, the <code>count</code> characters
     * beginning at <code>start</code> are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from this callback.
     * @param s the <code>CharSequence</code>.
     * @param start start index of the current word.
     * @param count number of characters to be replaced.
     * @param after new length.
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        Log.d("Keyboard before", s.length()+" "+start+" "+count+" "+after);
        mLastWord = s.subSequence(start, start+count).toString();
        if(count>after) {     //backspace ... less chars in future
            String data = "";
//            Log.d ("Keyboard before", count-after+" backspaces : "+s.subSequence(start+after,start+count));
            for (int i = 0; i < count - after; ++i) // send (count-after) backspaces.
                 data = data + "\b";
            mKeyboardThread.addToBuffer(data);
        }
    }

    /**
     * Overrides the <code>android.text.TextWatcher.onTextChanged(CharSequence, int, int, int)</code>.<br/>
     *
     * Called to notify that, within <code>s</code>, the <code>count</code> characters beginning at
     * <code>start</code> have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from this callback.
     * @param s the <code>CharSequence</code>.
     * @param start start index of the current word.
     * @param before number of characters that are replaced.
     * @param count new length.
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        Log.d("Keyboard on", s.length()+" "+start+" "+before+" "+count);
        int diff = count - before;
        if(diff>0) {          //'s' contains more chars after text change
            String currWord = s.subSequence(start, start+count).toString();

            // insertion of chars
            if(mLastWord.equals(s.subSequence(start, start+before).toString())) {
                String input = s.subSequence(start + before, start + count).toString();
//                Log.d("Keyboard on", input);
                mKeyboardThread.addToBuffer(input);
            }
            // replace old with new text
            else {
                String data = "";
                for(int i=0;i<before;++i)
                    data = data + "\b";
//                Log.d("Keyboard on", data+currWord);
                mKeyboardThread.addToBuffer(data + currWord);
            }
        }
        mLastInput = (s.length() == 0) ? null : s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {}

    /**
     * Overrides the <code>android.view.View.onKeyListener.onKey(View, int, KeyEvent)</code>.<br/>
     *
     * Called when a hardware key is dispatched to a view. This allows listeners to get a chance to
     * respond before the target view. This has been used for detecting backpress down event.
     * @param v The view the key has been dispatched to.
     * @param keyCode The code for the physical key that was pressed.
     * @param event The <code>KeyEvent</code> object containing full information about the event.
     * @return <code>true</code> if the listener has consumed the event,<br/>
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL && mLastInput == null && event.getAction() == KeyEvent.ACTION_DOWN) {
            mKeyboardThread.addToBuffer("\b");
            return true;
        }
        return false;
    }
}