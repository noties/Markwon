# Linkify

<MavenBadge4 :artifact="'linkify'" />

A plugin to automatically add links to your markdown. Currently autolinking works for:
* email (`me@web.com`)
* phone numbers (`+10000000`)
* web URLS

:::warning
`Linkify` plugin is based on `android.text.util.Linkify` which can lead to significant performance 
drop due to its implementation based on regex.
:::

:::danger
Do not use `autolink` XML attribute on your `TextView` as it will remove 
all links except autolinked ones ¯\\\_(ツ)_/¯
:::

```java
final Markwon markwon = Markwon.builder(context)
        // will autolink all supported types
        .usePlugin(LinkifyPlugin.create())
        // the same as above
        .usePlugin(LinkifyPlugin.create(
                Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS | Linkify.WEB_URLS
        ))
        // only emails
        .usePlugin(LinkifyPlugin.create(Linkify.EMAIL_ADDRESSES))
        .build();
```