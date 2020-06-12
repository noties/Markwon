package io.noties.markwon.sample.processor;

import androidx.annotation.NonNull;

import java.util.Set;

import io.noties.markwon.sample.annotations.MarkwonArtifact;

@SuppressWarnings("WeakerAccess")
public class MarkwonSample {
    // represents full (package + class) name to be use in reflective lookup
    final String javaClassName;

    final String id;
    final String title;
    final String description;
    final Set<MarkwonArtifact> artifacts;
    final Set<String> tags;

    public MarkwonSample(
            @NonNull String javaClassName,
            @NonNull String id,
            @NonNull String title,
            @NonNull String description,
            @NonNull Set<MarkwonArtifact> artifacts,
            @NonNull Set<String> tags
    ) {
        this.javaClassName = javaClassName;
        this.id = id;
        this.title = title;
        this.description = description;
        this.artifacts = artifacts;
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarkwonSample sample = (MarkwonSample) o;

        if (!javaClassName.equals(sample.javaClassName)) return false;
        if (!id.equals(sample.id)) return false;
        if (!title.equals(sample.title)) return false;
        if (!description.equals(sample.description)) return false;
        if (!artifacts.equals(sample.artifacts)) return false;
        return tags.equals(sample.tags);
    }

    @Override
    public int hashCode() {
        int result = javaClassName.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + artifacts.hashCode();
        result = 31 * result + tags.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MarkwonSample{" +
                "javaClassName='" + javaClassName + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", artifacts=" + artifacts +
                ", tags=" + tags +
                '}';
    }
}
