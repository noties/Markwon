package io.noties.markwon.test;

import androidx.annotation.NonNull;

public class TestSpanEnumerator {

    public interface Listener {
        void onNext(int start, int end, @NonNull TestSpan span);
    }

    public void enumerate(@NonNull TestSpan.Document document, @NonNull Listener listener) {
        visit(0, document, listener);
    }

    private int visit(int start, @NonNull TestSpan span, @NonNull Listener listener) {

        if (span instanceof TestSpan.Text) {
            final int end = start + ((TestSpan.Text) span).length();
            listener.onNext(start, end, span);
            return end;
        }

        // yeah, we will need end... and from recursive call also -> children can have text inside
        int s = start;

        for (TestSpan child : span.children()) {
            s = visit(s, child, listener);
        }

        listener.onNext(start, s, span);

        return s;
    }
}
