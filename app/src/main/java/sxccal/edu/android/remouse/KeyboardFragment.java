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
    private EditText mKeyboardInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        if(sClient != null) {
            mKeyboardInput = (EditText) view.findViewById(R.id.keyboard);
            mKeyboardInput.setText("");
            mKeyboardInput.setTextSize(18);
            mKeyboardInput.setOnKeyListener(this);
            mKeyboardInput.addTextChangedListener(this);
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
            int currentInputLength = s.length();
            int lastInputLength = mLastInput.length();
            int diff = currentInputLength - lastInputLength;
            if(diff > 0) {
                String string = s.subSequence(currentInputLength - diff, currentInputLength).toString();
                int len = string.length();
                for(int i=0;i<len;++i) {
                    try {
                        sClient.sendKeyboardData("" + string.charAt(i));
                    } catch (IOException e) {}
                }
            } else if(diff < 0) {
                try {
                    sClient.sendKeyboardData("backspace");
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
            if(keyCode == KeyEvent.KEYCODE_DEL) sClient.sendKeyboardData("backspace");
        } catch (IOException e) {}
        return false;
    }
}
