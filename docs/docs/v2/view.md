# MarkwonView

<MavenBadge2xx artifact="markwon-view" />

This is simple library containing 2 views that are able to display markdown:
* MarkwonView - extends `android.view.TextView`
* MarkwonViewCompat - extends `android.support.v7.widget.AppCompatTextView`

Both of them implement common `IMarkwonView` interface:
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

Both views support layout-preview in Android Studio (with some exceptions, for example, bold span is not rendered due to some limitations of layout preview).
These are XML attributes:

```xml
app:mv_markdown="string"
app:mv_configurationProvider="string"
```

`mv_markdown` accepts a string and represents raw markdown

`mv_configurationProvider` accepts a string and represents a full class name of a class of type `ConfigurationProvider`,
for example: `com.example.my.package.MyConfigurationProvider` (this class must have an empty constructor
in order to be instantiated via reflection).

Please note that those views parse markdown in main thread, so their usage must be for relatively small markdown portions only
