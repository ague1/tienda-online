package com.example.myapplication.user_auth.resetPassword;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class OtpTextWatcher implements TextWatcher {
    private final EditText current;
    private final EditText next;

    public OtpTextWatcher(EditText current, EditText next) {
        this.current = current;
        this.next = next;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 1 && next != null) {
            next.requestFocus();
        } else if (s.length() == 0 && current != null) {
            current.requestFocus();
        }
    }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
