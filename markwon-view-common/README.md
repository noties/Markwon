# Markwon View Common

[![maven|markwon-view](https://img.shields.io/maven-central/v/ru.noties/markwon-view.svg?label=maven%7Cmarkwon-view)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon-view%22)

This library is the backbone of `MarkwonView` and `MarkwonViewCompat` and acts as glue code to `Markwon` by exposing `IMarkwonView`:
```java
public interface IMarkwonView {

    interface ConfigurationProvider {
        @NonNull
        SpannableConfiguration provide(@NonNull Context context);
    }

    void setConfigurationProvider(@NonNull ConfigurationProvider provider);

    void setMarkdown(@Nullable String markdown);
    void setMarkdown(@Nullable SpannableConfiguration configuration, @Nullable String markdown);

    @Nullable
    String getMarkdown();
}
```