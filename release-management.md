# Release management

There are 2 core branches:
* `master`
* `develop`

`master` represents currently released version. In most cases its `HEAD` must also
point to a tag with release version name. 

`develop` represents version that is currently in development. It always should have
`-SNAPSHOT` suffix in `VERSION_NAME` variable (defined in `gradle.properties`).
Ideally each push to `develop` should also publish a SNAPSHOT version to MavenCentral (pending resolution).

Before releasing a new version a new branch is created. It's name should follow
the `v4.1.1` pattern (where `4.1.1` is upcoming release version name). In this branch
should all release preparations be done (removing all mentions of SNAPSHOT and updating
version name). Then a pull-request is issued from this branch to `master`.

After a pull-request is resolved (merged to `master`) all changes must be reflected in `develop`
branch (merge with `master`), next `VERSION_NAME` must be assigned with `-SNAPSHOT` suffix and published to snapshot Maven repo 
(snapshot users will see an update available).
The issuer branch (with version name) should be deleted.

A new version must be pushed to MavenCentral and new git-tag with version name must be
created in the repository.

Rinse and repeat.

## `@since` annotation

Although it is not required it is a nice thing to do: add `@since $VERSION` comment to the code
whenever it is possible (at least for publicly accessible code - API). This would help
navigating the project without the need to checkout the full VCS history. As keeping track of
current and/or upcoming version can be error-prone it is better to insert a generic `@since code`
that can be properly substituted upon a release.

For example, `@since $nap` seems like a good candidate. For this a live template can be created and used
whenever a new API method/field/functionality-change is introduced (`snc`):

```
// semicolon with a space so this one is not accedentally replaced with release version
@since $nap ;
```

This live template would be possible to use in both inline comment and javadoc comment.

## documentation

If there are updates to documentation web site, do not forget to publish it