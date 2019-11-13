# Inline Parser <Badge text="4.2.0" />

**Experimental** commonmark-java inline parser that allows customizing 
core features and/or extend with own. 

Usage of _internal_ classes:
```java
import org.commonmark.internal.Bracket;
import org.commonmark.internal.Delimiter;
import org.commonmark.internal.ReferenceParser;
import org.commonmark.internal.util.Escaping;
import org.commonmark.internal.util.Html5Entities;
import org.commonmark.internal.util.Parsing;
import org.commonmark.internal.inline.AsteriskDelimiterProcessor;
import org.commonmark.internal.inline.UnderscoreDelimiterProcessor;
```

---

```java
// all default (like current commonmark-java InlineParserImpl) 
final InlineParserFactory factory = MarkwonInlineParser.factoryBuilder()
        .includeDefaults()
        .build();
```

```java
// disable images (current markdown images will be considered as links):
final InlineParserFactory factory = MarkwonInlineParser.factoryBuilder()
        .includeDefaults()
        .excludeInlineProcessor(BangInlineProcessor.class)
        .build();
```

```java
// disable core delimiter processors for `*`|`_` and `**`|`__`
final InlineParserFactory factory = MarkwonInlineParser.factoryBuilder()
        .includeDefaults()
        .excludeDelimiterProcessor(AsteriskDelimiterProcessor.class)
        .excludeDelimiterProcessor(UnderscoreDelimiterProcessor.class)
        .build();
```

```java
// disable _all_ markdown inlines except for links (open and close bracket handling `[` & `]`)
final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
        // note that there is no `includeDefaults` method call
        .referencesEnabled(true)
        .addInlineProcessor(new OpenBracketInlineProcessor())
        .addInlineProcessor(new CloseBracketInlineProcessor())
        .build();
```

To use custom InlineParser:
```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureParser(@NonNull Parser.Builder builder) {
                builder.inlineParserFactory(inlineParserFactory);
            }
        })
        .build();
```

---

The list of available inline processors:

* `AutolinkInlineProcessor` (`<` =&gt; `<me@mydoma.in>`)
* `BackslashInlineProcessor` (`\\`)
* `BackticksInlineProcessor` (<code>&#96;</code> =&gt; <code>&#96;code&#96;</code>)
* `BangInlineProcessor` (`!` =&gt; `![alt](#src)`)
* `CloseBracketInlineProcessor` (`]` =&gt; `[link](#href)`, `![alt](#src)`)
* `EntityInlineProcessor` (`&` =&gt; `&amp;`)
* `HtmlInlineProcessor` (`<` =&gt; `<html></html>`)
* `NewLineInlineProcessor` (`\n`)
* `OpenBracketInlineProcessor` (`[` =&gt; `[link](#href)`)