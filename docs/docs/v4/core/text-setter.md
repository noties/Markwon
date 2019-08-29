# TextSetter <Badge text="4.1.0" />

Since <Badge text="4.1.0" /> it is possible to control how text is applied to a `TextView`.
This is done via `Markwon.TextSetter` interface.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(/**/)
        .textSetter(PrecomputedTextSetterCompat.create(Executors.newCachedThreadPool()))
        .build();
```

```java
public interface TextSetter {
    /**
     * @param textView   TextView
     * @param markdown   prepared markdown
     * @param bufferType BufferType specified when building {@link Markwon} instance
     *                   via {@link Builder#bufferType(TextView.BufferType)}
     * @param onComplete action to run when set-text is finished (required to call in order
     *                   to execute {@link MarkwonPlugin#afterSetText(TextView)})
     */
    void setText(
            @NonNull TextView textView,
            @NonNull Spanned markdown,
            @NonNull TextView.BufferType bufferType,
            @NonNull Runnable onComplete);
}
```

Primary target for this functionality is to use [PrecomputedText] and [PrecomputedTextCompat].
`Markwon` comes with `PrecomputedTextSetterCompat` implementation.

:::tip Note
Please note that `PrecomputedTextCompat` belongs to the `androidx.core:core` artifact. Make
sure that you have it in your project's dependencies (explicitly or implicitly)
:::

[PrecomputedText]: https://developer.android.com/reference/android/text/PrecomputedText
[PrecomputedTextCompat]: https://developer.android.com/reference/androidx/core/text/PrecomputedTextCompat