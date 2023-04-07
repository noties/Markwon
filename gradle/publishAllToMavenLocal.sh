#!/bin/bash

./gradlew clean \
&& ./gradlew :markwon-core:publishToMavenLocal \
&& ./gradlew :markwon-ext-latex:publishToMavenLocal \
&& ./gradlew :markwon-ext-strikethrough:publishToMavenLocal \
&& ./gradlew :markwon-ext-tables:publishToMavenLocal \
&& ./gradlew :markwon-ext-tasklist:publishToMavenLocal \
&& ./gradlew :markwon-html:publishToMavenLocal \
&& ./gradlew :markwon-image:publishToMavenLocal \
&& ./gradlew :markwon-image-coil:publishToMavenLocal \
&& ./gradlew :markwon-image-glide:publishToMavenLocal \
&& ./gradlew :markwon-image-picasso:publishToMavenLocal \
&& ./gradlew :markwon-linkify:publishToMavenLocal \
&& ./gradlew :markwon-recycler:publishToMavenLocal \
&& ./gradlew :markwon-recycler-table:publishToMavenLocal \
&& ./gradlew :markwon-simple-ext:publishToMavenLocal \
&& ./gradlew :markwon-syntax-highlight:publishToMavenLocal \
&& ./gradlew :markwon-editor:publishToMavenLocal \
&& ./gradlew clean