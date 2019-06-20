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
                        // @since 4.0.0 - change to provider
                        .backgroundProvider(() -> new MyDrawable()))
                        // @since 4.0.0 - optional, by default cached-thread-pool will be used
                        .executorService(Executors.newCachedThreadPool());
            }
        }))
        .build();
```


:::tip
Since <Badge text="4.0.0" /> `JLatexMathPlugin` operates independently of `ImagesPlugin`
:::