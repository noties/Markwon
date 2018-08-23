package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ix.Ix;
import ix.IxFunction;
import ix.IxPredicate;

abstract class TestDataReader {

    private static final String FOLDER = "tests/";

    @NonNull
    static Collection<Object> testFiles() {

        final InputStream in = TestDataReader.class.getClassLoader().getResourceAsStream(FOLDER);
        if (in == null) {
            throw new RuntimeException("Cannot access test cases folder");
        }

        try {
            //noinspection unchecked
            return (Collection) Ix.from(IOUtils.readLines(in, StandardCharsets.UTF_8))
                    .filter(new IxPredicate<String>() {
                        @Override
                        public boolean test(String s) {
                            return s.endsWith(".yaml");
                        }
                    })
                    .map(new IxFunction<String, String>() {
                        @Override
                        public String apply(String s) {
                            return FOLDER + s;
                        }
                    })
                    .map(new IxFunction<String, Object[]>() {
                        @Override
                        public Object[] apply(String s) {
                            return new Object[]{
                                    s
                            };
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    static TestData readTest(@NonNull String file) {
        return new Reader(file).read();
    }

    private TestDataReader() {
    }

    static class Reader {

        private final String file;

        Reader(@NonNull String file) {
            this.file = file;
        }

        @NonNull
        TestData read() {
            return testData(jsonObject());
        }

        @NonNull
        private JsonObject jsonObject() {
            try {
                final String input = IOUtils.resourceToString(file, StandardCharsets.UTF_8, TestDataReader.class.getClassLoader());
                final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
                final Object object = objectMapper.readValue(input, Object.class);
                final ObjectMapper jsonWriter = new ObjectMapper();
                final String json = jsonWriter.writeValueAsString(object);
                return new Gson().fromJson(json, JsonObject.class);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @NonNull
        private TestData testData(@NonNull JsonObject jsonObject) {

            final String description;
            {
                final JsonElement element = jsonObject.get("description");
                if (element != null
                        && element.isJsonPrimitive()) {
                    description = element.getAsString();
                } else {
                    description = null;
                }
            }

            final String input = jsonObject.get("input").getAsString();
            if (TextUtils.isEmpty(input)) {
                throw new RuntimeException(String.format("Test case file `%s` is missing input parameter", file));
            }

            final List<TestEntry> testSpans = testEntries(jsonObject.get("output").getAsJsonArray());
            if (testSpans.size() == 0) {
                throw new RuntimeException(String.format("Test case file `%s` has no output specified", file));
            }

            return new TestData(
                    description,
                    input,
                    testConfig(jsonObject.get("config")),
                    testSpans
            );
        }

        // todo: rename TestNode -> it's not a node... but... what?

        @NonNull
        private List<TestEntry> testEntries(@NonNull JsonArray array) {

            // all items are contained in this array
            // key is the name of an item
            // item can be defined like this:
            //  link: "text-content-of-the-link"
            //  link:
            //      attributes:
            //      - href: "my-href-attribute"
            //      text: "and here is the text content"
            // text node can be found only at root level

            final int length = array.size();

            final List<TestEntry> testSpans = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {

                final JsonElement element = array.get(i);

                if (element.isJsonObject()) {

                    final JsonObject object = element.getAsJsonObject();

                    // objects must have exactly 1 key: name of the node
                    // it's value can be different: JsonPrimitive (String) or an JsonObject (with attributes and text)

                    final String name = object.keySet().iterator().next();
                    final JsonElement value = object.get(name);

                    final String text;
                    final Map<String, String> attributes;

                    if (value.isJsonObject()) {

                        final JsonObject valueObject = value.getAsJsonObject();
                        text = valueObject.get("text").getAsString();
                        attributes = attributes(valueObject.get("attrs"));

                    } else {

                        final JsonPrimitive primitive;

                        if (value.isJsonPrimitive()) {
                            primitive = value.getAsJsonPrimitive();
                        } else {
                            primitive = null;
                        }

                        if (primitive == null
                                || !primitive.isString()) {
                            throw new RuntimeException(String.format("Unexpected json element at index: `%d` in array: `%s`",
                                    i, array
                            ));
                        }

                        text = primitive.getAsString();
                        attributes = Collections.emptyMap();
                    }

                    testSpans.add(new TestEntry(name, text, attributes));

                } else {
                    throw new RuntimeException(String.format("Unexpected json element at index: `%d` in array: `%s`",
                            i, array
                    ));
                }
            }

            return testSpans;
        }

        @NonNull
        private TestConfig testConfig(@Nullable JsonElement element) {

            final JsonObject object = element != null && element.isJsonObject()
                    ? element.getAsJsonObject()
                    : null;

            final Map<String, Boolean> map;

            if (object != null) {

                map = new HashMap<>(object.size());

                for (String key : object.keySet()) {

                    final JsonElement value = object.get(key);

                    if (value.isJsonPrimitive()) {

                        final JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();

                        Boolean b = null;

                        if (jsonPrimitive.isBoolean()) {
                            b = jsonPrimitive.getAsBoolean();
                        } else if (jsonPrimitive.isString()) {
                            final String s = jsonPrimitive.getAsString();
                            if ("true".equalsIgnoreCase(s)) {
                                b = Boolean.TRUE;
                            } else if ("false".equalsIgnoreCase(s)) {
                                b = Boolean.FALSE;
                            }
                        }

                        if (b != null) {
                            map.put(key, b);
                        }
                    }
                }
            } else {
                map = Collections.emptyMap();
            }

            return new TestConfig(map);
        }

        @NonNull
        private static Map<String, String> attributes(@NonNull JsonElement element) {

            final JsonObject object = element.isJsonObject()
                    ? element.getAsJsonObject()
                    : null;

            final Map<String, String> attributes;

            if (object != null) {
                attributes = new HashMap<>(object.size());
                for (String key : object.keySet()) {
                    attributes.put(key, object.get(key).getAsString());
                }
            } else {
                attributes = Collections.emptyMap();
            }

            return attributes;
        }
    }
}
