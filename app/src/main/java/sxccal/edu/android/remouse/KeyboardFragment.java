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

import static sxccal.edu.android.remouse.ConnectionFragment.sSecuredClient;

/**
 * @author Sayantan Majumdar
 */

public class KeyboardFragment extends Fragment implements View.OnKeyListener, TextWatcher {

    private String mLastInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        if(sSecuredClient != null) {
            EditText keyboardInput = (EditText) view.findViewById(R.id.keyboard);
            keyboardInput.setText("");
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
                sSecuredClient.sendData("Key", string);
            } else if(diff < 0) {
                sSecuredClient.sendData("Key", "backspace");
            }
        } else mLastInput = null;
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("KeyboardFrag After: ", s.toString());
        if(mLastInput == null) {
            sSecuredClient.sendData("Key", "backspace");
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL && mLastInput == null && event.getAction() == KeyEvent.ACTION_DOWN) {
            sSecuredClient.sendData("Key ", "backspace");
            return true;
        }
        return false;
    }
}