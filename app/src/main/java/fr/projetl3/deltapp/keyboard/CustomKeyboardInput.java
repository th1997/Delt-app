package fr.projetl3.deltapp.keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.example.projetl3.R;

public class CustomKeyboardInput extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    @Override
    public View onCreateInputView() {
        KeyboardView keyboardView = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        Keyboard keyboard = new Keyboard(this, R.xml.keys);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if(inputConnection != null){
            switch (primaryCode){
                case Keyboard.KEYCODE_DELETE:
                    CharSequence sei = inputConnection.getSelectedText(0);
                    if(TextUtils.isEmpty(sei)){ inputConnection.deleteSurroundingText(1, 0); }
                    else { inputConnection.commitText("", 1); }
                    break;
                case 10000:
                    inputConnection.commitText("tan()", 4);
                    break;
                case 10001:
                    inputConnection.commitText("exp()", 4);
                    break;
                case 10002:
                    inputConnection.commitText("ln()", 3);
                    break;
                case 10003:
                    inputConnection.commitText("log()", 4);
                    break;
                case 10004:
                    inputConnection.commitText("arctan()", 7);
                    break;
                case 10005:
                    inputConnection.commitText("cos()", 4);
                    break;
                case 10006:
                    inputConnection.commitText("sqrt()", 5);
                    break;
                case 10007:
                    inputConnection.commitText("sin()", 4);
                    break;
                case 10008:
                    inputConnection.commitText("arccos()", 7);
                    break;
                case 10009:
                    inputConnection.commitText("int()", 4);
                    break;
                case 10010:
                    inputConnection.commitText("arcsin()", 1);
                    break;
                default:
                    char code = (char)primaryCode;
                    inputConnection.commitText(String.valueOf(code), 1);
            }
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
