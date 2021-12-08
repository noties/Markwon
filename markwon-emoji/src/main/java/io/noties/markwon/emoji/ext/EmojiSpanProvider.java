package io.noties.markwon.emoji.ext;
import android.content.Context;
import android.graphics.drawable.Drawable;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public abstract class EmojiSpanProvider {

    @SuppressWarnings("SameParameterValue")
    @NonNull
    public static EmojiSpanProvider create(@NonNull Context context, float textSize) {
        return new Impl(context, textSize);
    }

    @NonNull
    public abstract Object provide(@NonNull String text);


    private static class Impl extends EmojiSpanProvider {

        private final Context context;
        private float textSize;
        private Map<String, EmojiCode> emojiMap;
        Impl(@NonNull Context context, float textSize) {
            this.context = context;
            this.textSize = textSize;
            this.emojiMap = loadEmojiMapFromAssets();
        }

    private Map<String, EmojiCode> loadEmojiMapFromAssets(){
        try{
            InputStream is = this.context.getAssets().open("emoji.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, EmojiCode>>(){}.getType();
            Map<String, EmojiCode> map = gson.fromJson(jsonString, mapType);
            return map;
        }catch (IOException e){
            e.printStackTrace();
            return  null;
        }
    }

        @NonNull
        @Override
        public Object provide(@NonNull String colonCode) {
            return new CenterSpan(getDrawable(colonCode));
        }

        @NonNull
        private Drawable getDrawable(String colonCode) {
            EmojiCode emojiCode = emojiMap.get(colonCode);
            int drawbleId = -1;
            if (emojiCode != null){
                String imageName = emojiCode.getImageName();
                try{
                    drawbleId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            Drawable emojiDrawable;
            if (drawbleId != -1) {
                emojiDrawable = context.getResources().getDrawable(drawbleId);
            } else {
                emojiDrawable = context.getResources().getDrawable(R.drawable.tw_1f30d);
            }

            int emojiHeight = emojiDrawable.getIntrinsicHeight();
            int emojiWidth = emojiDrawable.getIntrinsicWidth();
            int requiredWidth = emojiWidth * (int)textSize / emojiHeight;
            emojiDrawable.setBounds(0, 0, requiredWidth, (int) textSize);
            return emojiDrawable;
        }
    }
}
