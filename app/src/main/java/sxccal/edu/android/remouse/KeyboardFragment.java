package sxccal.edu.android.remouse;

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

import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;

/**
 * @author Sayantan Majumdar
 */

public class KeyboardFragment extends Fragment implements View.OnKeyListener, TextWatcher {

    private String mLastInput;
    private InputMethodManager mInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        mInput = null;
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
        mLastInput = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() != 0 ) {
            int currentInputLength = s.length();
            int lastInputLength = mLastInput.length();
            int diff = currentInputLength - lastInputLength;
            if(diff > 0) {
                String string = s.subSequence(currentInputLength - diff, currentInputLength).toString();
                sendKeyboardData(string);
            } else if(diff < 0) {
                sendKeyboardData("\b"); //sends backspace
            }
        } else mLastInput = null;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(mLastInput == null) {
            sendKeyboardData("\b"); //sends backspace
        }
    }

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