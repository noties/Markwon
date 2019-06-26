# Changelog

# 3.0.2
* Fix `latex` plugin (#136)
* Add `#create(Call.Factory)` factory method to `OkHttpImagesPlugin` (#129)
  thanks to @ZacSweers 

## 3.0.1
* Add `AsyncDrawableLoader.Builder#implementation` method (#109)
* AsyncDrawable allow placeholder to have independent size (#115)
* `addFactory` method for MarkwonSpansFactory
* Add optional spans for list blocks (bullet and ordered)
* AsyncDrawable placeholder bounds fix
* SpannableBuilder setSpans allow array of arrays
* Add `requireFactory` method to MarkwonSpansFactory
* Add DrawableUtils

## 3.0.0
* Plugins, plugins, plugins
* Split basic functionality blocks into standalone modules
* Maven artifacts group changed to `ru.noties.markwon` (previously had been `ru.noties`)
* removed `markwon`, `markwon-image-loader`, `markwon-html-pareser-api`, `markwon-html-parser-impl`, `markwon-view` modules
* new module system: `core`, `ext-latex`, `ext-strikethrough`, `ext-tables`, `ext-tasklist`, `html`, `image-gif`, `image-okhttp`, `image-svg`, `recycler`, `recycler-table`, `syntax-highlight`
* Add BufferType option for Markwon configuration
* Fix typo in AsyncDrawable waitingForDimensions
* New tests format
* `Markwon.render` returns `Spanned` instance of generic `CharSequence`
* LinkMovementMethod is applied implicitly if not set on a TextView explicitly
* Split code and codeBlock spans and factories
* Add CustomTypefaceSpan 
* Add NoCopySpansFactory
* Add placeholder to image loading

Generally speaking there are a lot of changes. Most of them are not backwards-compatible.
The main point of this release is the `Plugin` system that allows more fluent configuration
and opens the possibility of extending `Markwon` with 3rd party functionality in a simple
and intuitive fashion. Please refer to the [documentation web-site](https://noties.github.io/Markwon)
that has information on how to start migration.

The shortest excerpt of this release can be expressed like this:

```java
// previous v2.x.x way
Markwon.setMarkdown(textView, "**Hello there!**");
```

```java
// 3.x.x
Markwon.create(context)
        .setMarkdown(textView, "**Hello there!**");
```

But there is much more to it, please visit documentation web-site
to get the full picture of latest changes.

## 2.0.1
* `SpannableMarkdownVisitor` Rename blockQuoteIndent to blockIndent
* Fixed block new lines logic for block quote and paragraph (#82)
* AsyncDrawable fix no dimensions bug (#81)
* Update SpannableTheme to use Px instead of Dimension annotation
* Allow TaskListSpan isDone mutation
* Updated commonmark-java to 0.12.1
* Add OrderedListItemSpan measure utility method (#78)
* Add SpannableBuilder#getSpans method
* Fix DataUri scheme handler in image-loader (#74)
* Introduced a "copy" builder for SpannableThem
  Thanks @c-b-h

## 2.0.0
* Add `html-parser-api` and `html-parser-impl` modules
* Add `HtmlEmptyTagReplacement`
* Implement Appendable and CharSequence in SpannableBuilder
* Renamed library modules to reflect maven artifact names
* Rename `markwon-syntax` to `markwon-syntax-highlight`
* Add HtmlRenderer asbtraction
* Add CssInlineStyleParser
* Fix Theme#listItemColor and OL
* Fix task list block parser to revert parsing state when line is not matching
* Defined test format files
* image-loader add datauri parser
* image-loader add support for inline data uri image references
* Add travis configuration
* Fix image with width greater than canvas scaled
* Fix blockquote span
* Dealing with white spaces at the end of a document
* image-loader add SchemeHandler abstraction
* Add sample-latex-math module

## v1.1.1
* Fix OrderedListItemSpan text position (baseline) (#55)
* Add softBreakAddsNewLine option for SpannableConfiguration (#54)
* Paragraph text can now explicitly be spanned (#58)
  Thanks to @c-b-h
* Fix table border color if odd background is specified (#56)
* Add table customizations (even and header rows)

## v1.1.0
* Update commonmark to 0.11.0 and android-gif to 1.2.14
* Add syntax highlight functionality (`library-syntax` module and `markwon-syntax` artifact)
* Add headingTypeface, headingTextSizes to SpannableTheme
  Thanks to @edenman
* Introduce `MediaDecoder` abstraction to `image-loader` module
* Introduce `SpannableFactory`
  Thanks for idea to @c-b-h
* Update sample application to use syntax-highlight
* Update sample application to use clickable placeholder for GIF media

## v1.0.6
* Fix bullet list item size (depend on text size and not top-bottom arguments)
* Add ability to specify MovementMethod when applying markdown to a TextView
* Markdown images size is also resolved via ImageSizeResolver
* Moved `ImageSize`, `ImageSizeResolver` and `ImageSizeResolverDef` 
to `ru.noties.markwon.renderer` package (one level up, previously `ru.noties.markwon.renderer.html`)

## v1.0.5
* Change LinkSpan to extend URLSpan. Allow default linkColor (if not set explicitly)
* Fit an image without dimensions to canvas width (and keep ratio)
* Add support for separate color for code blocks (#37)
  Thanks to @Arcnor

## v1.0.4
* Fixes #28 (tables are not rendered when at the end of the markdown)
* Adds support for `indented code blocks`
  Thanks to @dlew

## v1.0.3
* Fixed ordered lists (when number width is greater than block margin)

## v1.0.2
* Fixed additional white spaces at the end of parsed markdown
* Fixed headings with no underline (levels 1 &amp; 2)
* Tables can have no borders

## v1.0.1
* Support for task-lists (#2)
* Spans now are applied in reverse order (#5 #10)
* Added `SpannableBuilder` to follow the reverse order of spans
* Updated `commonmark-java` to `0.10.0`
* Fixes #1

## v1.0.0

Initial release