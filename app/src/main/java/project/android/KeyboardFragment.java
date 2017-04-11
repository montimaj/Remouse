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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View view = getView();
        if(mInput != null && view != null) {
            mInput.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(mKeyboardThread != null) mKeyboardThread.setStopFlag();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

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

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL && mLastInput == null && event.getAction() == KeyEvent.ACTION_DOWN) {
            mKeyboardThread.addToBuffer("\b");
            return true;
        }
        return false;
    }
}