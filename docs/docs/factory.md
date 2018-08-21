# Factory <Badge text="1.1.0" />

:::tip
`SpannableFactory` can be used to ignore some kinds of text markup. If, for example,
you do not wish to apply _emphasis_ styling to your final result, just return `null`
from `emphasis` factory method:
```java
@Nullable
@Override
public Object emphasis() {
    return null;
}
```
:::