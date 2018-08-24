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

        private static final String ATTRS = "attrs";
        private static final String TEXT = "text";

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
                throw new RuntimeException(String.format("Test case file `%s` is missing " +
                        "input parameter", file));
            }

            final TestConfig testConfig = testConfig(jsonObject.get("config"));

            final List<TestNode> testNodes = testNodes(jsonObject.get("output").getAsJsonArray());
            if (testNodes.size() == 0) {
                throw new RuntimeException(String.format("Test case file `%s` has no " +
                        "output specified", file));
            }

            return new TestData(
                    description,
                    input,
                    testConfig,
                    testNodes
            );
        }

        @NonNull
        private List<TestNode> testNodes(@NonNull JsonArray array) {
            return testNodes(null, array);
        }

        @NonNull
        private List<TestNode> testNodes(@Nullable TestNode parent, @NonNull JsonArray array) {

            // an item in array is a JsonObject

            // it can be "b": "bold" -> means Span(name="b", children=[Text(bold)]
            //  or b:
            //      - text: "bold" -> which is the same as above

            // it can additionally contain "attrs" key which is the attributes
            // b:
            //  - text: "bold"
            //  attrs:
            //      href: "my-href"

            final int size = array.size();

            final List<TestNode> testNodes = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {

                final JsonObject object = array.get(i).getAsJsonObject();

                String name = null;
                Map<String, String> attributes = null;

                for (String key : object.keySet()) {
                    if (ATTRS.equals(key)) {
                        attributes = attributes(object.get(key));
                    } else if (name == null) {
                        name = key;
                    } else {
                        // we allow only 2 keys: span and/or attributes and no more
                        throw new RuntimeException("Unexpected key in object: " + object);
                    }
                }

                if (name == null) {
                    throw new RuntimeException("Object is missing tag name: " + object);
                }

                if (attributes == null) {
                    attributes = Collections.emptyMap();
                }

                final JsonElement element = object.get(name);

                if (TEXT.equals(name)) {
                    testNodes.add(new TestNode.Text(parent, element.getAsString()));
                } else {

                    final List<TestNode> children = new ArrayList<>(1);
                    final TestNode.Span span = new TestNode.Span(parent, name, children, attributes);

                    // if it's primitive string -> just append text node
                    if (element.isJsonPrimitive()) {
                        children.add(new TestNode.Text(span, element.getAsString()));
                    } else if (element.isJsonArray()) {
                        children.addAll(testNodes(span, element.getAsJsonArray()));
                    } else {
                        throw new RuntimeException("Unexpected element: " + object);
                    }

                    testNodes.add(span);
                }
            }

            return testNodes;
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
                    final String value;
                    final JsonElement valueElement = object.get(key);
                    if (valueElement.isJsonNull()) {
                        value = null;
                    } else {
                        value = valueElement.getAsString();
                    }
                    attributes.put(key, value);
                }
            } else {
                attributes = Collections.emptyMap();
            }

            return attributes;
        }
    }
}
