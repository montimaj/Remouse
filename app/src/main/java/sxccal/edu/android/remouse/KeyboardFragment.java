package sxccal.edu.android.remouse;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;

/**
 * @author Sayantan Majumdar
 */

public class KeyboardFragment extends Fragment implements View.OnKeyListener, TextWatcher {

    private String mLastInput;
    private InputMethodManager mInput;
    private String mLastWord;

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

        if(count>after) {     //backspace ... less chars in future
            String data = "";
//            Log.d ("Keyboard before", count-after+" backspaces : "+s.subSequence(start+after,start+count));
            for (int i = 0; i < count - after; ++i) // send (count-after) backspaces.
                 data = data + "\b";
            sendKeyboardData(data);
        }
        int space = Math.max(s.toString().lastIndexOf(' '), s.toString().lastIndexOf('\n'));
        mLastWord = s.subSequence(space+1, s.length()).toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        int diff = count - before;
        if(diff>0) {          //'s' contains more chars after text change
            String input = s.subSequence(start + before, start + count).toString();
//            Log.d("Keyboard on", input);
            int space = Math.max(s.toString().lastIndexOf(' ', s.length()-2),
                                    s.toString().lastIndexOf('\n', s.length()-2));
            String currLastWord = s.subSequence(space+1, s.length()).toString();
//            Log.d("Keyboard on", mLastWord);
//            Log.d("Keyboard on", currLastWord);
            if(input.equals(" ") && !(currLastWord.equals(mLastWord+" "))) {
                String data = "";
                for (int i = 0; i < mLastWord.length(); ++i)
                    data = data + "\b";
                sendKeyboardData(data + currLastWord);
//                sendKeyboardData();
            }
            else
                sendKeyboardData(input);
        }
        mLastInput = (s.length() == 0) ? null : s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL && mLastInput == null && event.getAction() == KeyEvent.ACTION_DOWN) {
            sendKeyboardData("\b"); //sends backspace
            return true;
        }
        return false;
    }

    private void sendKeyboardData(final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sSecuredClient.sendData("Key", data);
            }
        }).start();
    }
}