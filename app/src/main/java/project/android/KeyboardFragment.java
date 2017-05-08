package project.android;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import project.android.net.KeyboardThread;

import static project.android.ConnectionFragment.sConnectionAlive;

/**
 * Class representing the <code>Fragment</code> for providing the
 * GUI frontend for the keyboard module.
 *
 * <p>
 *     The following keyboard features are provided:
 *     <ul>
 *         <li>Predictive text input.</li>
 *         <li>Auto-correct feature.</li>
 *         <li>Hide sensitive text.</li>
 *         <li>Support for special keys like Ctrl, Alt etc. </li>
 *     </ul>
 * </p>
 *
 * @see project.android.net.KeyboardThread
 */
public class KeyboardFragment extends Fragment implements View.OnKeyListener, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, TextWatcher {

    private String mLastInput;
    private InputMethodManager mInput;
    private String mLastWord;
    private KeyboardThread mKeyboardThread;
    private EditText mKeyboardInput;

    private static final int[] SPECIAL_KEY_ID = {
            R.id.Ctrl,   //0
            R.id.Alt,    //1
            R.id.Shift,  //2
            R.id.Show_Hide, //3
            R.id.Home,      //4
            R.id.End,       //5
            R.id.Page_Up,   //6
            R.id.Page_Down,  //7
            R.id.Del,    //8
            R.id.Esc,    //9
            R.id.Tab,    //10
            R.id.f1,     //11
            R.id.f2,     //12
            R.id.f3,     //13
            R.id.f4,     //14
            R.id.f5,     //15
            R.id.f6,     //16
            R.id.f7,     //17
            R.id.f8,     //18
            R.id.f9,     //19
            R.id.f10,    //20
            R.id.f11,    //21
            R.id.f12,    //22
            R.id.Insert, //23
            R.id.upArrow,     //24
            R.id.downArrow,  //25
            R.id.leftArrow,  //26
            R.id.rightArrow  //27
    };

    /**
     * Overrides the
     * <code>Fragment.onCreateView(LayoutInflater, ViewGroup, Bundle)</code>
     * of the Android API.
     *
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The <code>LayoutInflater</code> object that can be
     *                 used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the
     *                  fragment's UI is attached to. The fragment should
     *                  not add the view itself, but this can be used to
     *                  generate the <code>LayoutParams</code> of the view.
     * @param savedInstanceState If non-null, this fragment is being
     *                           re-constructed from a previous saved
     *                           state as given here.
     * @return the <code>View</code> for the fragment's UI, or <code>null</code>.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        mInput = null;
        mLastInput = null;
        if(sConnectionAlive.containsValue(true)) {
            mInput = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInput.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            mKeyboardInput = (EditText) view.findViewById(R.id.keyboard);
            mKeyboardInput.setCursorVisible(false);
            mKeyboardInput.setLongClickable(false);
            mKeyboardInput.setOnClickListener(this);
            mKeyboardInput.setText("");
            mKeyboardInput.setTextSize(18);
            mKeyboardInput.setOnKeyListener(this);
            mKeyboardInput.addTextChangedListener(this);
            mKeyboardThread = new KeyboardThread();
            new Thread(mKeyboardThread).start();
            mLastWord = "";
            setListeners(view);
        }
        return view;
    }

    /**
     * Overrides the <code>Fragment.onDestroyView()</code> method
     * of the Android API.
     *
     * Called when the view previously created by
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
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
     * Overrides the <code>Fragment.onResume()</code> method
     * of the Android API.
     *
     * Called when the activity will start interacting with
     * the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Overrides the <code>Fragment.onPause()</code> method
     * of the Android API.
     *
     * Called when the system is about to start resuming a
     * previous activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    /**
     * Overrides the
     * <code>TextWatcher.beforeTextChanged(CharSequence, int, int, int)</code>
     * method of the Android API.
     *
     * <p>
     *     Called to notify that, within <code>s</code>, the <code>count</code>
     *     characters beginning at <code>start</code> are about to be replaced
     *     by new text with length <code>after</code>. It is an error to attempt
     *     to make changes to <code>s</code> from this callback.
     * </p>
     *
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
//            Log.d ("Keyboard before", count-after+" backspaces : "+s.subSequence(start+after,start+count));
            String data = "";
            for (int i = 0; i < count - after; ++i) // send (count-after) backspaces.
                 data = data + "\b";
            mKeyboardThread.addToBuffer(data, false);
        }
    }

    /**
     * Overrides the
     * <code>onTextChanged(CharSequence, int, int, int)</code>
     * method of the Android API.
     *
     * <p>
     *     Called to notify that, within <code>s</code>, the <code>count</code>
     *     characters beginning at <code>start</code> have just replaced old
     *     text that had length <code>before</code>. It is an error to attempt
     *     to make changes to <code>s</code> from this callback.
     * </p>
     *
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
                mKeyboardThread.addToBuffer(input, false);
            }
            // replace old with new text
            else {
                String data = "";
                for(int i=0;i<before;++i)
                    data = data + "\b";
//                Log.d("Keyboard on", data+currWord);
                mKeyboardThread.addToBuffer((data + currWord), false);
            }
        }
        mLastInput = (s.length() == 0) ? null : s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {}

    /**
     * Overrides the
     * <code>View.onKeyListener.onKey(View, int, KeyEvent)</code>
     * method of the Android API.
     *
     * Called when a hardware key is dispatched to a view. This allows
     * listeners to get a chance to respond before the target view.
     * This has been used for detecting back key down event.
     *
     * @param v the view to which the key has been dispatched.
     * @param keyCode the code for the physical key that was pressed.
     * @param event the <code>KeyEvent</code> object containing full
     *              information about the event.
     * @return <code>true</code>, if the listener has consumed the event,<br/>
     *         <code>false</code>, otherwise.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL && mLastInput == null && event.getAction() == KeyEvent.ACTION_DOWN) {
            mKeyboardThread.addToBuffer("\b", false);
            return true;
        }
        return false;
    }

    /**
     * Overrides the
     * <code>CompoundButton.onCheckChanged(CompoundButton, boolean)</code>
     * method of the Android API.
     *
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView the <code>CompoundButton</code> view whose state
     *                   has changed.
     * @param isChecked the new checked state of <code>buttonView</code>.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String data = "";
        switch (buttonView.getId()) {
            case R.id.Ctrl:
                if(isChecked)   data = "Ctrl_On";
                else    data = "Ctrl_Off";
                break;

            case R.id.Alt:
                if(isChecked)   data = "Alt_On";
                else    data = "Alt_Off";
                break;

            case R.id.Shift:
                if(isChecked)   data = "Shift_On";
                else    data = "Shift_Off";
                break;

            case R.id.Show_Hide:
                if(isChecked) {
                    mKeyboardInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else mKeyboardInput.setTransformationMethod(null);
                mKeyboardInput.setSelection(mKeyboardInput.getText().length());
                return;
        }
        mKeyboardThread.addToBuffer(data, true);
    }

    /**
     * Overrides the <code>View.onClickListener.onClick(View)</code>
     * method of the Android API.
     *
     * Called when a view has been clicked.
     *
     * @param v the view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String data = "";
        switch(v.getId()) {
            case R.id.Home:
                data = "Home";
                break;

            case R.id.End:
                data = "End";
                break;

            case R.id.Page_Up:
                data = "Page_Up";
                break;

            case R.id.Page_Down:
                data = "Page_Down";
                break;

            case R.id.Del:
                data = "Del";
                break;

            case R.id.Esc:
                data = "Esc";
                break;

            case R.id.Tab:
                data = "Tab";
                break;

            case R.id.f1:
                data = "F1";
                break;

            case R.id.f2:
                data = "F2";
                break;

            case R.id.f3:
                data = "F3";
                break;

            case R.id.f4:
                data = "F4";
                break;

            case R.id.f5:
                data = "F5";
                break;

            case R.id.f6:
                data = "F6";
                break;

            case R.id.f7:
                data = "F7";
                break;

            case R.id.f8:
                data = "F8";
                break;

            case R.id.f9:
                data = "F9";
                break;

            case R.id.f10:
                data = "F10";
                break;

            case R.id.f11:
                data = "F11";
                break;

            case R.id.f12:
                data = "F12";
                break;

            case R.id.Insert:
                data = "Insert";
                break;

            case R.id.upArrow:
                data = "Up";
                break;

            case R.id.downArrow:
                data = "Down";
                break;

            case R.id.leftArrow:
                data = "Left";
                break;

            case R.id.rightArrow:
                data = "Right";
                break;

            case R.id.keyboard:
                mKeyboardInput.setSelection(mKeyboardInput.getText().length());
                return;
        }
        mKeyboardThread.addToBuffer(data, true);
    }

    private void setListeners(View view) {
        for(int numSpecialButtons = 0; numSpecialButtons < SPECIAL_KEY_ID.length; ++numSpecialButtons) {
            if(numSpecialButtons >=0 && numSpecialButtons <=3) {
                CheckBox checkBox = (CheckBox) view.findViewById(SPECIAL_KEY_ID[numSpecialButtons]);
                checkBox.setOnCheckedChangeListener(this);
            } else if(numSpecialButtons >=4 && numSpecialButtons <=23) {
                Button button = (Button) view.findViewById(SPECIAL_KEY_ID[numSpecialButtons]);
                button.setOnClickListener(this);
            } else {
                ImageButton imageButton = (ImageButton) view.findViewById(SPECIAL_KEY_ID[numSpecialButtons]);
                imageButton.setOnClickListener(this);
            }
        }
    }
}