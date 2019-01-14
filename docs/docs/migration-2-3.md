# Migration 2.x.x -> 3.x.x

* strikethrough moved to standalone module
* tables moved to standalone module
* core functionality of `AsyncDrawableLoader` moved to `core` module
* * Handling of GIF and SVG media moved to standalone modules (`gif` and `svg` respectively)
* * OkHttpClient to download images moved to standalone module
* HTML no longer _implicitly_ added to core functionality, it must be specified __explicitly__ (as an artifact)