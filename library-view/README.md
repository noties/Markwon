# Markwon View

[![maven|markwon-view](https://img.shields.io/maven-central/v/ru.noties/markwon-view.svg?label=maven%7Cmarkwon-view)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon-view%22)

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
```
app:mv_markdown="string"
app:mv_configurationProvider="string"

app:mv_H1Style="reference"
app:mv_H2Style="reference"
app:mv_H3Style="reference"
app:mv_H4Style="reference"
app:mv_H5Style="reference"
app:mv_H6Style="reference"

app:mv_EmphasisStyle="reference"
app:mv_StrongEmphasisStyle="reference"
app:mv_BlockQuoteStyle="reference"
app:mv_CodeSpanStyle="reference"
app:mv_MultilineCodeSpanStyle="reference"
app:mv_OrderedListItemStyle="reference"
app:mv_BulletListItemStyle="reference"
app:mv_TaskListItemStyle="reference"
app:mv_TableRowStyle="reference"
app:mv_ParagraphStyle="reference"
app:mv_LinkStyle="reference"
```

`mv_markdown` accepts a string and represents raw markdown

`mv_configurationProvider` accepts a string and represents a full class name of a class of type `ConfigurationProvider`,
for example: `com.example.my.package.MyConfigurationProvider` (this class must have an empty constructor
in order to be instantiated via reflection).

Please note that those views parse markdown in main thread, so their usage must be for relatively small markdown portions only.

An `mv_*Style` may refer to an actual style or a theme attribute that resolves to a style as well as support for `android:textAppearance` which is basically a sub-style. Currently the following attributes are supported:
* `android:textColor`
* `android:textColorLink`
* `android:textSize`
* `android:textStyle`
* `android:fontFamily`
* `fontFamily`
* `android:typeface`
* `android:textAppeance`
  * `android:textColor`
  * `android:textColorLink`
  * `android:textSize`
  * `android:textStyle`
  * `android:fontFamily`
  * `fontFamily`
  * `android:typeface`

A theme level style may be set through the theme attribute `markwonViewStyle`.

Note that, just like `TextView`, values included in `textAppearance` are canceled by values in the root style. Also mimicking `AppCompatTextView`, `android:fontFamily` takes precedence over `fontFamily`.

## Example
```XML
<ru.noties.markwon.view.MarkwonViewCompat
    style="?android:textAppearanceSmall"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:mv_H1Style="@style/MyH1Style"
    app:mv_H2Style="?myThemesH2Style"
    app:mv_H3Style="@style/MyH3Style"
    app:mv_H4Style="?android:textAppearanceLarge"
    app:mv_H5Style="?android:textAppearanceMediumInverse"
    app:mv_H6Style="?android:textAppearanceButton"
    app:mv_EmphasisStyle="@style/MyEmphasisStyleWithCustomFont"
    app:mv_StrongEmphasisStyle="?myThemesStrongEmphasisStyleWithCustomFont"
    app:mv_BlockQuoteStyle="@null"
    app:mv_CodeSpanStyle="@style/MyCodeSpanStyle"
    app:mv_MultilineCodeSpanStyle="?myThemesMultilineCodeSpan"
    app:mv_OrderedListItemStyle="?android:textAppearanceListItem"
    app:mv_BulletListItemStyle="?android:textAppearanceListItemSmall"
    app:mv_TaskListItemStyle="?android:textAppearanceSearchResultSubtitle"
    app:mv_TableRowStyle="?android:textAppearanceSearchResultTitle"
    app:mv_LinkStyle="?android:textAppearanceSmallPopupMenu"
    app:mv_markdown="@string/some_markdown_string_resource" />
```
In the above example, the paragraph text will get the style of the widget itself `style="?android:textAppearanceSmall"`. If a particular markup style is not specified or specified as `@null` Markwon's original spans will be applied for text with that markup. Otherwise the styling will be based on the resolved attributes. By supplying a valid `app:mv_ParagraphStyle` the default text (paragraph) will be spanned explicitly.