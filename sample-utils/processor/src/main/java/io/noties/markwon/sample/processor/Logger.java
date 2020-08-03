package io.noties.markwon.sample.processor;

import androidx.annotation.NonNull;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

class Logger {
    private final Messager messager;

    Logger(@NonNull Messager messager) {
        this.messager = messager;
    }

    void error(@NonNull String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, "\n[Markwon] " + String.format(message, args) + "\n\u00a0");
    }

    void info(@NonNull String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, "\n[Markwon] " + String.format(message, args) + "\n\u00a0");
    }
}
