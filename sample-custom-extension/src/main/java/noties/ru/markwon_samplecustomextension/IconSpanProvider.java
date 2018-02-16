package noties.ru.markwon_samplecustomextension;

import android.support.annotation.NonNull;

public interface IconSpanProvider {

    @NonNull
    IconSpan provide(@NonNull String name, @NonNull String color, @NonNull String size);
}
