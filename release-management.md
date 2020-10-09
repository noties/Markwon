# Release checklist
- [ ] CHANGELOG.md has proper version for released changes
- [ ] Code occurrences of `@since $SNAPSHOT;` are replaced with proper version (`@deprecated` also)
- [ ] `gradle.properties` has proper version (NOT SNAPSHOT)
- [ ] documentation web site is published (if has changes)


# Release management

Development happens in the `master` branch. Although CI (via Github workflows) is triggered
with each push, all releases are happening in manual mode (stable and snapshot versions 
of the library and the sample app).

```
# Stable and snapshot library release (depending on the version specified in `gradle.properties`)
./gradlew upA -Prelease

# Sample app release
./app-sample/deploy.sh
```

Tests must be run before releasing (either locally or via CI).

## `@since` annotation

All code changes should have a documentation comment with version of the library specified.
For changes before _stable_ release (snapshots) a special `$SNAPSHOT;` version can be used
(useful when the next version name of the library is still not known).

```java
/**
 * @since $SNAPSHOT;
 */

// @since $SNAPSHOT;
```

These changes must also be placed in according section of the `CHANGELOG.md` file:
* `Added`
* `Changed`
* `Fixed`
* `Deprecated`
* `Removed`

Stable release must replace all `$SNAPSHOT;` occurrences with proper library version name.

## Documentation

If there are updates to documentation web site these should be published