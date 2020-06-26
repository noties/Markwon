package io.noties.markwon.app.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.noties.markwon.app.sample.Sample;

public abstract class SampleUtils {

    @NonNull
    public static List<Sample> readSamples(@NonNull Context context) {

        final Gson gson = new Gson();

        try (InputStream inputStream = context.getAssets().open("samples.json")) {
            return gson.fromJson(
                    new InputStreamReader(inputStream),
                    new TypeToken<List<Sample>>() {
                    }.getType()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SampleUtils() {
    }
}
