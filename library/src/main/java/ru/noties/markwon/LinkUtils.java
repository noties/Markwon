package ru.noties.markwon;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pa.gulko zTrap (26.10.2017)
 * @since 1.0.1
 */
public class LinkUtils {

    private LinkUtils() {
        //no instance
    }

    public static String cropImageSizes(@NonNull String link){
        Matcher matcher = Pattern.compile("(.*)/(\\d+)\\$(\\d+)").matcher(link);
        if (matcher.matches()){
            return matcher.group(1);
        } else {
            return link;
        }
    }
}
