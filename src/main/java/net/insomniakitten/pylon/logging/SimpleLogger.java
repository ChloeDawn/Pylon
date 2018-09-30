package net.insomniakitten.pylon.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.text.MessageFormat;
import java.util.function.Supplier;

final class SimpleLogger implements PylonLogger {
    private final String topic;
    private final Supplier<Messager> messager;

    SimpleLogger(final String topic, final Supplier<Messager> messager) {
        this.topic = topic;
        this.messager = messager;
    }

    @Override
    public String getTopic() {
        return this.topic;
    }

    @Override
    public void note(@Nullable final CharSequence message) {
        this.messager.get().printMessage(Diagnostic.Kind.NOTE, this.format(message));
    }

    @Override
    public void warn(@Nullable final CharSequence message) {
        this.messager.get().printMessage(Diagnostic.Kind.WARNING, this.format(message));
    }

    @Override
    public void error(@Nullable final CharSequence message) {
        this.messager.get().printMessage(Diagnostic.Kind.ERROR, this.format(message));
    }

    @Override
    public void note(@Nullable final CharSequence message, final Element element) {
        this.messager.get().printMessage(Diagnostic.Kind.NOTE, this.format(message), element);
    }

    @Override
    public void warn(@Nullable final CharSequence message, final Element element) {
        this.messager.get().printMessage(Diagnostic.Kind.WARNING, this.format(message), element);
    }

    @Override
    public void error(@Nullable final CharSequence message, final Element element) {
        this.messager.get().printMessage(Diagnostic.Kind.ERROR, this.format(message), element);
    }

    @Override
    public void note(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror) {
        this.messager.get().printMessage(Diagnostic.Kind.NOTE, this.format(message), element, mirror);
    }

    @Override
    public void warn(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror) {
        this.messager.get().printMessage(Diagnostic.Kind.WARNING, this.format(message), element, mirror);
    }

    @Override
    public void error(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror) {
        this.messager.get().printMessage(Diagnostic.Kind.ERROR, this.format(message), element, mirror);
    }

    @Override
    public void note(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value) {
        this.messager.get().printMessage(Diagnostic.Kind.NOTE, this.format(message), element, mirror, value);
    }

    @Override
    public void warn(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value) {
        this.messager.get().printMessage(Diagnostic.Kind.WARNING, this.format(message), element, mirror, value);
    }

    @Override
    public void error(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value) {
        this.messager.get().printMessage(Diagnostic.Kind.ERROR, this.format(message), element, mirror, value);
    }

    @Nonnull
    private CharSequence format(@Nullable final CharSequence message) {
        if (message == null || message.length() == 0) {
            return MessageFormat.format("[{0}] null", this.getTopic());
        }
        return MessageFormat.format("[{0}] {1}", this.getTopic(), message);
    }
}
