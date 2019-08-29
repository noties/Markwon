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
branch (merge with `master`) and `-SNAPSHOT` suffix must be added to the `VERSION_NAME`.

A new version must be pushed to MavenCentral and new git-tag with version name must be
created in the repository.

Rinse and repeat.