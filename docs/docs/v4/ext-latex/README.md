# LaTeX extension

<MavenBadge4 :artifact="'ext-latex'" />

This is an extension that will help you display LaTeX content in your markdown.
Since <Badge text="4.3.0" /> supports both blocks and inlines markdown structures (blocks only before `4.3.0`).

## Blocks
Start a line with 2 (or more) `$` symbols followed by a new line:
```markdown
$$
\\text{A long division \\longdiv{12345}{13}
$$
```
LaTeX block content will be considered ended when a starting sequence of `$` is found on
a new line. If block was started with `$$$` it must be ended with `$$$` symbols.

## Inline
Exactly `$$` before and after _inline_ LaTeX content:
```markdown
$$\\text{A long division \\longdiv{12345}{13}$$
```

:::warning
By default inline nodes are disabled and must be enabled explicitly:
```java
final Markwon markwon = Markwon.builder(this)
        // required plugin to support inline parsing
        .usePlugin(MarkwonInlineParserPlugin.create())
        .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
            @Override
            public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                // ENABLE inlines
                builder.inlinesEnabled(true);
            }
        }))
        .build();
```
Please note that usage of inline nodes **require** [MarkwonInlineParserPlugin](../inline-parser/)
:::

This extension uses [jlatexmath-android](https://github.com/noties/jlatexmath-android) artifact to create LaTeX drawable. 

## Config

```java
// create default instance of plugin and use specified text size for both blocks and inlines
JLatexMathPlugin.create(textView.getTextSize());

// create default instance of plugin and use specified text sizes
JLatexMathPlugin.create(inlineTextSize, blockTextSize);

JLatexMathPlugin.create(textView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
    @Override
    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
        // enable inlines (require `MarkwonInlineParserPlugin`), by default `false`
        builder.inlinesEnabled(true);
        
        // use pre-4.3.0 LaTeX block parsing (by default `false`)
        builder.blocksLegacy(true);
        
        // by default true
        builder.blocksEnabled(true);
        
        // @since 4.3.0
        builder.errorHandler(new JLatexMathPlugin.ErrorHandler() {
            @Nullable
            @Override
            public Drawable handleError(@NonNull String latex, @NonNull Throwable error) {
                // Receive error and optionally return drawable to be displayed instead
                return null;
            }
        });
        
        // executor on which parsing of LaTeX is done (by default `Executors.newCachedThreadPool()`)
        builder.executorService(Executors.newCachedThreadPool());
    }
});
```

## Theme

```java
JLatexMathPlugin.create(textView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
    @Override
    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {

        // background provider for both inlines and blocks
        //  or more specific: `inlineBackgroundProvider` & `blockBackgroundProvider`
        builder.theme().backgroundProvider(new JLatexMathTheme.BackgroundProvider() {
            @NonNull
            @Override
            public Drawable provide() {
                return new ColorDrawable(0xFFff0000);
            }
        });

        // should block fit the whole canvas width, by default true
        builder.theme().blockFitCanvas(true);

        // horizontal alignment for block, by default ALIGN_CENTER 
        builder.theme().blockHorizontalAlignment(JLatexMathDrawable.ALIGN_CENTER);

        // padding for both inlines and blocks
        builder.theme().padding(JLatexMathTheme.Padding.all(8));
        
        // padding for inlines
        builder.theme().inlinePadding(JLatexMathTheme.Padding.symmetric(16, 8));
        
        // padding for blocks
        builder.theme().blockPadding(new JLatexMathTheme.Padding(0, 1, 2, 3));
        
        // text color of LaTeX content for both inlines and blocks
        //  or more specific: `inlineTextColor` & `blockTextColor`
        builder.theme().textColor(Color.RED);
    }
});
```

:::tip
Sometimes it is enough to use rendered to an image LaTeX formula and 
inline it directly in your markdown document. For this markdown references can be useful. For example:
```markdown
<!-- your mardown -->
![markdown-reference] of a solution...

<!-- then reference prerendered and converted to base64 SVG/PNG/GIF/etc -->
[markdown-reference]: data:image/svg+xml;base64,base64encodeddata==
```
For this to work an image loader that supports data uri and base64 must be used. Default `Markwon` [image-loader](../image/) supports it out of box (including SVG support)
:::