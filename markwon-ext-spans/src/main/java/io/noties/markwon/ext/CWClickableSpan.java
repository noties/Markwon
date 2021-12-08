package io.noties.markwon.span.ext;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.campuswire.android.messenger.interfaces.MessageItemTapListener;

public class CWClickableSpan extends ClickableSpan {
    String userId;
    MessageItemTapListener messageItemTapListener;
    public CWClickableSpan(String text, MessageItemTapListener listener) {
        super();
        userId = text;
        messageItemTapListener = listener;
    }
    @Override
    public void onClick(@NonNull View view) {
        messageItemTapListener.onClickUser(userId);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setUnderlineText(false);
    }
}
