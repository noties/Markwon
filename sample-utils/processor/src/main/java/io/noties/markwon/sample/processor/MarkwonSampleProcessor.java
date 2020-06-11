package io.noties.markwon.sample.processor;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import io.noties.markwon.sample.annotations.MarkwonSample;

public class MarkwonSampleProcessor extends AbstractProcessor {

    private static final String KEY_SAMPLES_FILE = "markwon.samples.file";

    private Messager messager;
    private String samplesFilePath;

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton(KEY_SAMPLES_FILE);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(MarkwonSample.class.getName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        messager = processingEnvironment.getMessager();
        samplesFilePath = processingEnvironment.getOptions().get(KEY_SAMPLES_FILE);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            final Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(MarkwonSample.class);
            if (elements != null) {
                for (Element element : elements) {
                    process(element);
                }
            }
        }
        return false;
    }

    private void process(@NonNull Element element) {
        messager.printMessage(Diagnostic.Kind.WARNING, samplesFilePath, element);
    }
}
