package ru.noties.markwon.priority;

import android.support.annotation.NonNull;

import java.util.List;

import ru.noties.markwon.MarkwonPlugin;

public abstract class PriorityProcessor {

    @NonNull
    public static PriorityProcessor create() {
        return new PriorityProcessorImpl();
    }

    @NonNull
    public abstract List<MarkwonPlugin> process(@NonNull List<MarkwonPlugin> plugins);
}
