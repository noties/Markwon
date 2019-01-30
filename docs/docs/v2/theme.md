<LegacyWarning />

# Theme

Here is the list of properties that can be configured via `SpannableTheme#builder` factory
method. If you wish to control what is out of this list, you can use [SpannableFactory](/docs/v2/factory.md)
abstraction which lets you to gather full control of Spans that are used to display markdown.

* factory methods

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

<ThemeProperty name="codeMultilineMargin" type="@Px int" defaults="Width of the space character" />

### Code typeface

Typeface of code content

<ThemeProperty name="codeTypeface" type="android.graphics.Typeface" defaults="Typeface.MONOSPACE" />

### Code text size

Text size of code content

<ThemeProperty name="codeTextSize" type="@Px int" defaults="(Content text size) * 0.87 if no custom <a href='#code-typeface'>Typeface</a> was set, otherwise (content text size)" />

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

## Script ratio

Ratio to be applied for `sup` (super script) &amp; `sub` (sub script)

<ThemeProperty name="scriptTextSizeRatio" type="float" defaults="0.75F" />

## Thematic break

### Color

Color of a thematic break

<ThemeProperty name="thematicBreakColor" type="@ColorInt int" defaults="(text color) with 25 (0-255) alpha" />

### Height

Height of a thematic break

<ThemeProperty name="thematicBreakHeight" type="@Px int" defaults="Stroke width of context TextPaint" />

## Table

### Cell padding

Padding inside a table cell

<ThemeProperty name="tableCellPadding" type="@Px int" defaults="0" />

### Border color

The color of table borders

<ThemeProperty name="tableBorderColor" type="@ColorInt int" defaults="(text color) with 75 (0-255) alpha" />

### Border width

The width of table borders

<ThemeProperty name="tableBorderWidth" type="@Px int" defaults="Stroke with of context TextPaint" />

### Odd row background

Background of an odd table row

<ThemeProperty name="tableOddRowBackgroundColor" type="@ColorInt int" defaults="(text color) with 22 (0-255) alpha" />

### Even row background <Badge text="1.1.1" />

Background of an even table row

<ThemeProperty name="tableEventRowBackgroundColor" type="@ColorInt int" defaults="0" />

### Header row background <Badge text="1.1.1" />

Background of header table row

<ThemeProperty name="tableHeaderRowBackgroundColor" type="@ColorInt int" defaults="0" />

## Task list drawable <Badge text="1.0.1" />

Drawable of task list item

<ThemeProperty name="taskListDrawable" type="android.graphics.drawable.Drawable" defaults="ru.noties.markwon.spans.TaskListDrawable" />
