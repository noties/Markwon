# Markwon View Compat

[![maven|markwon-view](https://img.shields.io/maven-central/v/ru.noties/markwon-view.svg?label=maven%7Cmarkwon-view)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon-view%22)

This library allows for rendering markdown through a widget `MarkwonViewCompat` and supports the following XML attributes:
```
app:mv_markdown="string"
app:mv_configurationProvider="string"
```

`MarkdownViewCompat` extends Android's support library's `AppCompatTextView`. Prefer this library when you benefit from features in `AppCompatTextView`.

Android Studio's layout-preview supports rendering (with some exceptions, for example, bold span is not rendered due to some limitations of layout preview) markdown text.

`mv_markdown` accepts a string and represents raw markdown

`mv_configurationProvider` accepts a string and represents a full class name of a class of type `ConfigurationProvider`,
for example: `com.example.my.package.MyConfigurationProvider` (this class must have an empty constructor
in order to be instantiated via reflection).

Please note that those views parse markdown in main thread, so their usage must be for relatively small markdown portions only