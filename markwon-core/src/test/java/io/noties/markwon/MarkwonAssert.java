package io.noties.markwon;

import android.support.annotation.NonNull;

import org.junit.Assert;

public abstract class MarkwonAssert {

    public static void assertMessageContains(@NonNull Throwable t, @NonNull String contains) {
        Assert.assertTrue(t.getMessage(), t.getMessage().contains(contains));
    }

    private MarkwonAssert() {
    }
}
