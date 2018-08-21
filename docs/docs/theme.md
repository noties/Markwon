# Theme

Here is the list of properties that can be configured via `SpannableTheme#builder` factory
method. If you wish to control what is out of this list, you can use [SpannableFactory](/docs/factory.md)
abstraction which lets you to gather full control of Spans that are used to display markdown.

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