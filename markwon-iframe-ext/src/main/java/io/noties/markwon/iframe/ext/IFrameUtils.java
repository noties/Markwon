package io.noties.markwon.iframe.ext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IFrameUtils {
    private final static String youtube_expression = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
    public static String getYoutubeVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().length() <= 0){
            return null;
        }
        Pattern pattern = Pattern.compile(youtube_expression);
        Matcher matcher = pattern.matcher(videoUrl);
        try {
            if (matcher.find())
                return matcher.group();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private final static String vimeo_expression = "https?:\\/\\/(?:www\\.)?vimeo.com\\/(?:channels\\/(?:\\w+\\/)?|groups\\/([^\\/]*)\\/videos\\/|album\\/(\\d+)\\/video\\/|)(\\d+)(?:$|\\/|\\?)";
    public static String getVimeoVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().length() <= 0){
            return null;
        }
        Pattern pattern = Pattern.compile(vimeo_expression);
        Matcher matcher = pattern.matcher(videoUrl);
        try {
            if (matcher.find())
                return matcher.group(3);
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private final static String desmos_expression = "https?:\\/\\/(www\\.)?desmos.com\\/calculator\\/(\\w+)($|\\/)";
    public static String getDesmosId(String desmosURL) {
        if (desmosURL == null || desmosURL.trim().length() <= 0){
            return null;
        }
        Pattern pattern = Pattern.compile(desmos_expression);
        Matcher matcher = pattern.matcher(desmosURL);
        try {
            if (matcher.find())
                return matcher.group(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
