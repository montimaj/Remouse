package sxccal.edu.android.remouse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;

import static sxccal.edu.android.remouse.ConnectionFragment.sClient;

/**
 * @author Sayantan Majumdar
 */

public class KeyboardFragment extends Fragment implements View.OnKeyListener, TextWatcher {

    private String mLastInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        if(sClient != null) {
            EditText keyboardInput = (EditText) view.findViewById(R.id.keyboard);
            keyboardInput.setTextSize(18);
            keyboardInput.setOnKeyListener(this);
            keyboardInput.addTextChangedListener(this);
        }
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d("KeyboardFrag Before: ", s.toString());
        mLastInput = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() !=0 ) {
            int length = s.length();
            String lastChar = s.subSequence(length-1, length).toString();
            if(!lastChar.isEmpty() && length > mLastInput.length()) {
                try {
                    sClient.sendKeyboardData(lastChar);
                } catch (IOException e) {}
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("KeyboardFrag After: ", s.toString());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        try {
            switch(keyCode) {
                case KeyEvent.KEYCODE_DEL:
                    sClient.sendKeyboardData("backspace");
                    break;

                case KeyEvent.META_SHIFT_ON:
                    sClient.sendKeyboardData("shift");
                    break;

                case KeyEvent.META_CTRL_ON:
                    sClient.sendKeyboardData("control");
                    break;

                case KeyEvent.KEYCODE_CAPS_LOCK:
                    sClient.sendKeyboardData("caps");
                    System.out.println("Caps");
                    break;

                case KeyEvent.META_ALT_ON:
                    sClient.sendKeyboardData("alton");
                    break;

                case KeyEvent.KEYCODE_SPACE:
                    sClient.sendKeyboardData("space");
            }
       } catch (IOException e) {}
        return false;
    }
}
