# Custom extension

This module provides a simple implementation for icons that are bundled in your application resources using custom `DelimiterProcessor`. It can be used as a reference when dealing with new functionality based on _delimiters_. 

```markdown
# Hello @ic-android-black-24

**Please** click @ic-home-green-24 (home icon) if you want to go home.
```

Here we will substitute elements starting with `@ic-` for icons that we have in our resources:

* `@ic-android-black-24` -> `R.drawable.ic_android_black_24dp`
* `@ic-home-green-24` -> `R.drawable.ic_home_green_24dp`

In order to provide reliable parsing we need to have delimiters _around_ desired content. So, `@ic-home-green-24` would become `@ic-home-green-24@`. This is current limitation of [commonmark-java](https://github.com/atlassian/commonmark-java) library that Markwon uses underneath. There is an ongoing [issue](https://github.com/atlassian/commonmark-java/issues/113) that might change this in future thought.

But as we known the pattern beforehand it's pretty easy to pre-process raw markdown and make it the way we want it. Please refer to `IconProcessor#process` method for the reference.

So, the our steps would be:

* prepare raw markdown (wrap icons with `@` if it's not already)
* construct a Parser with our registered delimiter processor
* parse markdown and obtain a `Node`
* create a node visitor that will additionally visit custom node (`IconNode`)
* use markdown
