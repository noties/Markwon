# LaTeX extension

<MavenBadge4 :artifact="'ext-latex'" />

This is an extension that will help you display LaTeX formulas in your markdown.
Syntax is pretty simple: pre-fix and post-fix your latex with `$$` (double dollar sign).
`$$` should be the first characters in a line.

```markdown
$$
\\text{A long division \\longdiv{12345}{13}
$$
```

```markdown
$$\\text{A long division \\longdiv{12345}{13}$$
```

```java
Markwon.builder(context)
    .use(JLatexMathPlugin.create(textSize))
    .build();
```

This extension uses [jlatexmath-android](https://github.com/noties/jlatexmath-android) artifact to create LaTeX drawable. 

## Config

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(JLatexMathPlugin.create(textSize, new BuilderConfigure() {
            @Override
            public void configureBuilder(@NonNull Builder builder) {
                builder
                        .align(JLatexMathDrawable.ALIGN_CENTER)
                        .fitCanvas(true)
                        .padding(paddingPx)
                        // @since 4.0.0 - horizontal and vertical padding
                        .padding(paddingHorizontalPx, paddingVerticalPx)
                        // @since 4.0.0 - change to provider
                        .backgroundProvider(() -> new MyDrawable()))
                        // @since 4.0.0 - optional, by default cached-thread-pool will be used
                        .executorService(Executors.newCachedThreadPool());
            }
        }))
        .build();
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