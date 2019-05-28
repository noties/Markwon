package ru.noties.markwon.priority;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.noties.markwon.MarkwonPlugin;

// a small dependency graph also
// what if plugins cannot be constructed into a graph? for example they depend on something
// but not overlap? then it would be hard to sort them (but this doesn't make sense, if
// they do not care about other components, just put them in whatever order, no?)

/**
 * @see MarkwonPlugin#priority()
 * @since 3.0.0
 */
@Deprecated
public abstract class Priority {

    @NonNull
    public static Priority none() {
        return builder().build();
    }

    @NonNull
    public static Priority after(@NonNull Class<? extends MarkwonPlugin> plugin) {
        return builder().after(plugin).build();
    }

    @NonNull
    public static Priority after(
            @NonNull Class<? extends MarkwonPlugin> plugin1,
            @NonNull Class<? extends MarkwonPlugin> plugin2) {
        return builder().after(plugin1).after(plugin2).build();
    }

    @NonNull
    public static Builder builder() {
        return new Impl.BuilderImpl();
    }

    public interface Builder {

        @NonNull
        Builder after(@NonNull Class<? extends MarkwonPlugin> plugin);

        @NonNull
        Priority build();
    }

    @NonNull
    public abstract List<Class<? extends MarkwonPlugin>> after();


    static class Impl extends Priority {

        private final List<Class<? extends MarkwonPlugin>> after;

        Impl(@NonNull List<Class<? extends MarkwonPlugin>> after) {
            this.after = after;
        }

        @NonNull
        @Override
        public List<Class<? extends MarkwonPlugin>> after() {
            return after;
        }

        @Override
        public String toString() {
            return "Priority{" +
                    "after=" + after +
                    '}';
        }

        static class BuilderImpl implements Builder {

            private final List<Class<? extends MarkwonPlugin>> after = new ArrayList<>(0);

            @NonNull
            @Override
            public Builder after(@NonNull Class<? extends MarkwonPlugin> plugin) {
                after.add(plugin);
                return this;
            }

            @NonNull
            @Override
            public Priority build() {
                return new Impl(Collections.unmodifiableList(after));
            }
        }
    }
}
