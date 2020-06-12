package io.noties.markwon.app.utils;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

public abstract class TextWatcherAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public interface AfterTextChanged {
        void afterTextChanged(Editable s);
    }

    @NonNull
    public static TextWatcher afterTextChanged(@NonNull AfterTextChanged afterTextChanged) {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                afterTextChanged.afterTextChanged(s);
            }
        };
    }
}
