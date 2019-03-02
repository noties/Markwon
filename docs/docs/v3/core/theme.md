# Theme

Here is the list of properties that can be configured via `MarkwonTheme.Builder` class. 

:::tip
Starting with <Badge text="3.0.0" /> there is no need to manually construct a `MarkwonTheme`.
Instead a `Plugin` should be used:
```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                builder
                        .codeTextColor(Color.BLACK)
                        .codeBackgroundColor(Color.GREEN);
            }
        })
        .build();
```
:::

## Link color

Controls the color of a [link](#)

<ThemeProperty name="linkColor" type="@ColorInt int" defaults="Default link color of a context where markdown is displayed <sup>*</sup>" />

<sup>*</sup> `TextPaint#linkColor` will be used to determine linkColor of a context

## Block margin

Starting margin before text content for the:
* lists
* blockquotes
* task lists

<ThemeProperty name="blockMargin" type="@Px int" defaults="24dp" />

## Block quote

Customizations for the `blockquote` stripe

> Quote

### Stripe width

Width of a blockquote stripe

<ThemeProperty name="blockQuoteWidth" type="@Px int" defaults="1/4 of the <a href='#block-margin'>block margin</a>" />

### Stripe color

Color of a blockquote stripe

<ThemeProperty name="blockQuoteColor" type="@ColorInt int" defaults="textColor with <code>25</code> (0-255) alpha value" />

## List

### List item color

Controls the color of a list item. For ordered list: leading number,
for unordered list: bullet.

* UL
1. OL

<ThemeProperty name="listItemColor" type="@ColorInt int" defaults="Text color" />

### Bullet item stroke width

Border width of a bullet list item (level 2)

* First
* * Second
* * * Third

<ThemeProperty name="bulletListItemStrokeWidth" type="@Px int" defaults="Stroke width of TextPaint" />

### Bullet width

The width of the bullet item

* First
  * Second
    * Third

<ThemeProperty name="bulletWidth" type="@Px int" defaults="min(<a href='#block-margin'>blockMargin</a>, lineHeight) / 2" />

## Code

### Inline code text color

The color of the `code` content

<ThemeProperty name="codeTextColor" type="@ColorInt int" defaults="Content text color" />

### Inline code background color

The color of `background` of a code content

<ThemeProperty name="codeBackgroundColor" type="@ColorInt int" defaults="<a href='#inline-code-text-color'>inline code text color</a> with 25 (0-255) alpha" />

### Block code text color

```
The color of code block text
```

<ThemeProperty name="codeBlockTextColor" type="@ColorInt int" defaults="<a href='#inline-code-text-color'>inline code text color</a>" />

### Block code background color

```
The color of background of code block text
```

<ThemeProperty name="codeBlockBackgroundColor" type="@ColorInt int" defaults="<a href='#inline-code-background-color'>inline code background color</a>" />

### Block code leading margin

Leading margin for the block code content

<ThemeProperty name="codeMultilineMargin" type="@Px int" defaults="8dip" />

### Code typeface

Typeface of code content

<ThemeProperty name="codeTypeface" type="android.graphics.Typeface" defaults="Typeface.MONOSPACE" />

### Block code typeface <Badge text="3.0.0" />

Typeface of block code content

<ThemeProperty name="codeBlockTypeface" type="android.graphics.Typeface" defaults="<code>codeTypeface</code> if set or Typeface.MONOSPACE" />

### Code text size

Text size of code content

<ThemeProperty name="codeTextSize" type="@Px int" defaults="(Content text size) * 0.87 if no custom <a href='#code-typeface'>Typeface</a> was set, otherwise (content text size)" />

### Block code text size <Badge text="3.0.0" />

Text size of block code content

<ThemeProperty name="codeBlockTextSize" type="@Px int" defaults="<code>codeTextSize</code> if set or (content text size) * 0.87 if no custom <a href='#code-typeface'>Typeface</a> was set, otherwise (content text size)" />

## Heading

### Break height

The height of a brake under H1 &amp; H2

<ThemeProperty name="headingBreakHeight" type="@Px int" defaults="Stroke width of context TextPaint" />

### Break color

The color of a brake under H1 &amp; H2

<ThemeProperty name="headingBreakColor" type="@ColorInt int" defaults="(text color) with 75 (0-255) alpha" />

### Typeface <Badge text="1.1.0" />

The typeface of heading elements

<ThemeProperty name="headingTypeface" type="android.graphics.Typeface" defaults="default text Typeface" />

### Text size <Badge text="1.1.0" />

Array of heading text sizes _ratio_ that is applied to text size

<ThemeProperty name="headingTextSizeMultipliers" type="float[]" defaults="<code>{2.F, 1.5F, 1.17F, 1.F, .83F, .67F}</code> (HTML spec)" />

## Thematic break

### Color

Color of a thematic break

<ThemeProperty name="thematicBreakColor" type="@ColorInt int" defaults="(text color) with 25 (0-255) alpha" />

### Height

Height of a thematic break

<ThemeProperty name="thematicBreakHeight" type="@Px int" defaults="Stroke width of context TextPaint" />
