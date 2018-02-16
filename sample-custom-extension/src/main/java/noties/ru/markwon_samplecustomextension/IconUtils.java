package noties.ru.markwon_samplecustomextension;

import android.support.annotation.NonNull;

public abstract class IconUtils {

    private static final String TO_FIND = "@material-icon-";

    public static void prepare(@NonNull StringBuilder builder) {

        int start = builder.indexOf(TO_FIND);
        int end;

        while (start > -1) {
            end = iconDefinitionEnd(start + TO_FIND.length(), builder);
            builder.insert(end, '@');
            start = builder.indexOf(TO_FIND, end);
        }
    }

    private static int iconDefinitionEnd(int index, @NonNull StringBuilder builder) {

        // all spaces, new lines, non-words or digits,

        char c;

        int end = -1;
        for (int i = index; i < builder.length(); i++) {
            c = builder.charAt(i);
            if (Character.isWhitespace(c)
                    || !(Character.isLetterOrDigit(c) || c == '-' || c == '_')) {
                end = i;
                break;
            }
        }

        if (end == -1) {
            end = builder.length();
        }

        return end;
    }

    private IconUtils() {
    }
}
