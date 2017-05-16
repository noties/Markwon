![logo](https://github.com/noties/Scrollable/raw/master/art/scrollable_big_logo.png)

[![Maven Central](https://img.shields.io/maven-central/v/ru.noties/scrollable.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22scrollable%22)

---

**Scrollable** is a library for an Android application to implement various scrolling technicks. It's all started with **scrolling tabs**, but now much more can be done with it. Scrollable supports all scrolling and non-scrolling views, including: **RecyclerView**, **ScrollView**, **ListView**, **WebView**, etc and any combination of those inside a **ViewPager**. Library is designed to let developer implement desired effect without imposing one solution. Library is small and has no dependencies.

## Preview

All GIFs here are taken from `sample` application module.


<img src="https://github.com/noties/Scrollable/raw/master/art/scrollable_colorful.gif" width="30%" alt="colorful_sample"/> <img src="https://github.com/noties/Scrollable/raw/master/art/scrollable_custom_overscroll.gif" width="30%" alt="custom_overscroll_sample"/> <img src="https://github.com/noties/Scrollable/raw/master/art/scrollable_dialog.gif" width="30%" alt="dialog_sample"/>

<sup>*Serving suggestion</sup>

## Installation
```groovy
compile 'ru.noties:scrollable:1.3.0`
```

## Usage

To start using this library `ScrollableLayout` must be aded to your layout.

```xml
<?xml version="1.0" encoding="utf-8"?>
<ru.noties.scrollable.ScrollableLayout
    android:id="@+id/scrollable_layout"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:scrollable_autoMaxScroll="true"
    app:scrollable_defaultCloseUp="true">

    <ru.noties.scrollable.sample.SampleHeaderView
        style="@style/HeaderStyle"
        app:shv_title="@string/sample_title_fragment_pager"/>

    <ru.noties.scrollable.sample.TabsLayout
        android:id="@+id/tabs"
        android:layout_width="match_parent"
        android:layout_height="@dimen/tabs_height"
        android:background="@color/md_teal_500"/>

    <android.support.v4.view.ViewPager
        android:id="@+id/view_pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="@dimen/tabs_height"/>

</ru.noties.scrollable.ScrollableLayout>
```

Please note, that `ScrollableLayout` positions its children like vertical `LinearLayout`, but measures them like a `FrameLayout`. It is crucial that scrolling content holder dimentions must be set to `match_parent` (minus possible *sticky* view that should be extracted from it, for example, by specifying `android:layoutMarginTop="height_of_sticky_view"`).

Next, `ScrollableLayout` must be initialized in code:

```java
final ScrollableLayout scrollableLayout = findView(R.id.scrollable_layout);

// this listener is absolute minimum that is required for `ScrollableLayout` to function
scrollableLayout.setCanScrollVerticallyDelegate(new CanScrollVerticallyDelegate() {
    @Override
    public boolean canScrollVertically(int direction) {
        // Obtain a View that is a scroll container (RecyclerView, ListView, ScrollView, WebView, etc)
        // and call its `canScrollVertically(int) method.
        // Please note, that if `ViewPager is used, currently displayed View must be obtained
        // because `ViewPager` doesn't delegate `canScrollVertically` method calls to it's children
        final View view = getCurrentView();
        return view.canScrollVertically(direction);
    }
});
```


### Draggable View

This is a View, that can be *dragged* to change `ScrollableLayout` scroll state. For example, to expand header if tabs are dragged. To add this simply call:
```java
// Please note that `tabsLayout` must be a child (direct or indirect) of a ScrollableLayout
scrollableLayout.setDraggableView(tabsLayout);
```


### OnScrollChangedListener

In order to apply custom logic for different scroll state of a `ScrollableLayout` `OnScrollChangedListener` can be used (to change color of a header, create parallax effect, sticky tabs, etc)

```java
scrollableLayout.addOnScrollChangedListener(new OnScrollChangedListener() {
    @Override
    public void onScrollChanged(int y, int oldY, int maxY) {

        // `ratio` of current scroll state (from 0.0 to 1.0)
        // 0.0 - means fully expanded
        // 1.0 - means fully collapsed
        final float ratio = (float) y / maxY;

        // for example, we can hide header, if we are collapsed
        // and show it when we are expanded (plus intermediate state)
        header.setAlpha(1.F - ratio);

        // to create a `sticky` effect for tabs this calculation can be used:
        final float tabsTranslationY;
        if (y < maxY) {
            // natural position
            tabsTranslationY = .0F;
        } else {
            // sticky position
            tabsTranslationY = y - maxY;
        }
        tabsLayout.setTranslationY(tabsTranslationY);
    }
});
```


### OnFlingOverListener

To *continue* a fling event for a scrolling container `OnFlingOverListener` can be used.

```java
scrollableLayout.setOnFlingOverListener(new OnFlingOverListener() {
    @Override
    public void onFlingOver(int y, long duration) {
        recyclerView.smoothScrollBy(0, y);
    }
});
```


### OverScrollListener

To create custom *overscroll* handler (for example, like in `SwipeRefreshLayout` for loading, or to zoom-in header when cannot scroll further) `OverScrollListener` can be used

```java
scrollableLayout.setOverScrollListener(new OverScrollListener() {
    @Override
    public void onOverScrolled(ScrollableLayout layout, int overScrollY) {

    }

    @Override
    public boolean hasOverScroll(ScrollableLayout layout, int overScrollY) {
        return false;
    }

    @Override
    public void onCancelled(ScrollableLayout layout) {

    }

    @Override
    public void clear() {

    }
});
```

`OverScrollListener` gives you full controll of overscrolling, but it implies a lot of handling. For a simple case `OverScrollListenerBase` can be used

```java
scrollableLayout.setOverScrollListener(new OverScrollListenerBase() {
    @Override
    protected void onRatioChanged(ScrollableLayout layout, float ratio) {

    }
});
```

For example, this is `onRatioChanged` method from `ZoomInHeaderOverScrollListener` from `sample` application:
```java
@Override
protected void onRatioChanged(ScrollableLayout layout, float ratio) {
    final float scale = 1.F + (.33F * ratio);
    mHeader.setScaleX(scale);
    mHeader.setScaleY(scale);

    final int headerHeight = mHeader.getHeight();
    mContent.setTranslationY(((headerHeight * scale) - headerHeight) / 2.F);
}
```

### Scrolling Header

There is support for scrolling header. This means that if header can scroll, it will scroll first to the final position and only after that scroll event will be redirected. There are no extra steps to enable this feature if scrolling header is the first view in `ScrollableLayout`. Otherwise a XML attribute `app:scrollable_scrollingHeaderId` can be used, it accepts an id of a view.


## Various customizations

### CloseUpAlgorithm

In order to *close-up* `ScrollableLayout` (do not leave in intermediate state, allow only two scrolling states: collapsed & expanded, etc), `CloseUpAlgorithm` can be used. Its signature is as follows:

```java
public interface CloseUpAlgorithm {

    int getFlingFinalY(ScrollableLayout layout, boolean isScrollingBottom, int nowY, int suggestedY, int maxY);

    int getIdleFinalY(ScrollableLayout layout, int nowY, int maxY);
}
```

And usage is like this:

```java
scrollableLayout.setCloseUpAlgorithm(new MyCloseUpAlgorithm());
```

Library provides a `DefaultCloseUpAlgorithm` for a most common usage (to allow `ScrollableLayout` only 2 scrolling states: collapsed and expanded). It can be set via java code: `scrollableLayout.setCloseUpAlgorithm(new DefaultCloseUpAlgorithm())`  and via XML with `app:scrollable_defaultCloseUp="true"`.

Also, there is an option to set duration after which CloseUpAlgorithm should be evaluated (idle state - no touch events). Java: `scrollableLayout.setConsiderIdleMillis(100L)` and XML: `app:scrollable_considerIdleMillis="100"`. `100L` is the default value and may be omitted.

If *close-up* need to have different animation times, `CloseUpIdleAnimationTime` can be used. Its signature:

```java
public interface CloseUpIdleAnimationTime {
    long compute(ScrollableLayout layout, int nowY, int endY, int maxY);
}
```
If animation time is constant (do not depend on current scroll state), `SimpleCloseUpIdleAnimationTime` can be used. Java: `scrollableLayout.setCloseUpIdleAnimationTime(new SimpleCloseUpIdleAnimationTime(200L))`, XML: `app:app:scrollable_closeUpAnimationMillis="200"`. `200L` is default value and can be omitted.

If one want to get control of `ValueAnimator` that is used to animate between scroll states, `CloseUpAnimatorConfigurator` can be used. Its signature:

```java
public interface CloseUpAnimatorConfigurator {
    // when called will already have a duration set
    void configure(ValueAnimator animator);
}
```

If only `Interpolator` must be configured, a `InterpolatorCloseUpAnimatorConfigurator` can be used. Java: `scrollableLayout.setCloseAnimatorConfigurator(new InterpolatorCloseUpAnimatorConfigurator(interpolator))`, XML: `app:scrollable_closeUpAnimatorInterpolator="app:scrollable_closeUpAnimatorInterpolator="@android:interpolator/decelerate_cubic"`


### Auto Max Scroll

If you layout has a header with dynamic height, or it's height should be obtained at runtime, there is an option to automatically obtain it. Java: `scrollableLayout.setAutoMaxScroll(true)`, XML: `app:scrollable_autoMaxScroll="true"`. With this option there is no need manually set `maxScrollY`. Please note, that if not specified explicitly this option will be set to `true` if `maxScroll` option is not set (equals `0`). Also, if at runtime called `scrollableLayout.setMaxScroll(int)`, `autoMaxScroll` if set to `true`, will be set to `false`.

By default the first View will be used to calculate height, but if different one must be used, there is an option to specify `id` of this view. XML: `app:scrollable_autoMaxScrollViewId="@id/header"`


### Disable Handling

If `ScrollableLayout` must not evaluate its scrolling logic (skip all touch events), `scrollableLayout.setSelfUpdateScroll(boolean)` can be used. Pass `true` to disable all handling, `false` to enable it.


### Animate Scroll

To animate scroll state of a `ScrollableLayout`, `animateScroll(int)` can be used:

```java
// returns ValueAnimator, that can be configured as desired
// `0` - expand fully
// `scrollableLayout.getMaxScroll()` - collapse
scrollableLayout.animateScroll(0)
        .setDuration(250L)
        .start();
```

Please note that `ScrollableLayout` caches returned `ValueAnimator` and reuses it. First of all because it doesn't make sense to have two different scrolling animations on one `ScrollableLayout`. So, it's advisable to clear all possible custom state before running animation (just like `View` handles `ViewPropertyAnimator`)


## License

```
  Copyright 2015 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```
