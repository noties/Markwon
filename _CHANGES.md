* `Markwon.builder` won't require CorePlugin registration (it is done automatically)
  to create a builder without CorePlugin - use `Markwon#builderNoCore`
* `JLatex` plugin now is not dependent on ImagesPlugin
  also accepts a ExecutorService (optional, by default cachedThreadPool is used)
* AsyncDrawableScheduler now can be called by multiple plugins without penalty
  internally caches latest state and skips scheduling if drawables are already processed
* configure with registry
* removed priority
* images-plugin moved to standalone again
* removed MarkwonPlugin#configureHtmlRenderer -> now part of HtmlPlugin
* TagHandler now has `supportedTags()` method
* html is moved completely to html-plugin
* OnTextAddedListener