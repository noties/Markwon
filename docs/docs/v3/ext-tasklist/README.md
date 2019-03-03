# Task list extension

<MavenBadge :artifact="'ext-tasklist'" />

Adds support for GFM (Github-flavored markdown) task-lists:

```java
Markwon.builder(context)
        .usePlugin(TaskListPlugin.create(context));
```