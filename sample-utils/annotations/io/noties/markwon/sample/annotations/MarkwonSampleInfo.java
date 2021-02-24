package io.noties.markwon.sample.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// NB! if class was just removed if won't be removed from samples.json
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MarkwonSampleInfo {
    /**
     * Actual format is not important, but this key must be set in order to persist sample.
     * This key should not change during lifetime of sample
     * <p>
     * {@code id} date in `YYYYMMDDHHmmss` format (UTC),
     * a simple live template can be used:
     * {@code
     * groovyScript("new Date().format('YYYYMMDDHHmmss', TimeZone.getTimeZone('UTC'))")
     * }
     */
    String id();

    String title();

    String description() default "";

    MarkwonArtifact[] artifacts();

    Tag[] tags();
}
