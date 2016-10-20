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
        if(s.length() != 0 ) {
            int currentInputLength = s.length();
            int lastInputLength = mLastInput.length();
            int diff = currentInputLength - lastInputLength;
            System.out.println("curlen= " + currentInputLength);
            System.out.println("curStr= " + s);
            System.out.println("Laststr= " + mLastInput);
            System.out.println("lenlast= " + lastInputLength);
            System.out.println("diff= " + diff);
            if(diff > 0) {
                String string = s.subSequence(currentInputLength - diff, currentInputLength).toString();
                try {
                    sClient.sendKeyboardData(string);
                } catch (IOException e) {}
            }else if(diff < 0) {
                try {
                    sClient.sendKeyboardData("backspace");
                } catch (IOException e) {}
            }
        } else mLastInput = null;
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("KeyboardFrag After: ", s.toString());
        if(mLastInput == null) {
            try {
                sClient.sendKeyboardData("backspace");
            } catch (IOException e) {}
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        try {
            if(keyCode == KeyEvent.KEYCODE_DEL && mLastInput == null && event.getAction() == KeyEvent.ACTION_DOWN) {
                sClient.sendKeyboardData("backspace");
                return true;
            }
        } catch (IOException e) {}
        return false;
    }
}