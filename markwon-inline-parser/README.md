# Inline parser

**Experimental** due to usage of internal (but still visible) classes of commonmark-java:

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

`StaggeredDelimiterProcessor` class source is copied (required for InlineParser)