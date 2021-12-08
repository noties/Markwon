package io.noties.markwon.recycler;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class CustomMovementMethod extends LinkMovementMethod {
    @Override
    public boolean canSelectArbitrarily () {
        return true;
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        Selection.setSelection(text, text.length());
    }

    @Override
    public void onTakeFocus(TextView view, Spannable text, int dir) {
        if ((dir & (View.FOCUS_FORWARD | View.FOCUS_DOWN)) != 0) {
            if (view.getLayout() == null) {
                // This shouldn't be null, but do something sensible if it is.
                Selection.setSelection(text, text.length());
            }
        } else {
            Selection.setSelection(text, text.length());
        }
    }
}

