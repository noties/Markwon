#!/usr/bin/env sh

# abort on errors
set -e

# build
../gradlew :app-sample:clean
../gradlew :app-sample:assembleDebug

# navigate into the build output directory
cd ./build/outputs/apk/debug/

revision=$(git rev-parse --short HEAD)

echo "output.json" > ./.gitignore
echo "$revision" > ./version

git init
git add -A
git commit -m "sample $revision"

git push -f git@github.com:noties/Markwon.git master:sample-store

cd -