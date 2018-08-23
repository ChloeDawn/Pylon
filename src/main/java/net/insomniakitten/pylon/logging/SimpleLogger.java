package net.insomniakitten.pylon.logging;

import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.text.MessageFormat;
import java.util.function.Supplier;

@Value
final class SimpleLogger implements PylonLogger {
    private final String topic;
    private final Supplier<Messager> messager;

    @Override
    public void note(@Nullable final CharSequence message) {
        this.getMessager().get().printMessage(Diagnostic.Kind.NOTE, this.format(message));
    }

    @Override
    public void warn(@Nullable final CharSequence message) {
        this.getMessager().get().printMessage(Diagnostic.Kind.WARNING, this.format(message));
    }

    @Override
    public void error(@Nullable final CharSequence message) {
        this.getMessager().get().printMessage(Diagnostic.Kind.ERROR, this.format(message));
    }

    @Override
    public void note(@Nullable final CharSequence message, @Nonnull final Element element) {
        this.getMessager().get().printMessage(Diagnostic.Kind.NOTE, this.format(message), element);
    }

    @Override
    public void warn(@Nullable final CharSequence message, @Nonnull final Element element) {
        this.getMessager().get().printMessage(Diagnostic.Kind.WARNING, this.format(message), element);
    }

    @Override
    public void error(@Nullable final CharSequence message, @Nonnull final Element element) {
        this.getMessager().get().printMessage(Diagnostic.Kind.ERROR, this.format(message), element);
    }

    @Override
    public void note(@Nullable final CharSequence message, @Nonnull final Element element, @Nonnull final AnnotationMirror mirror) {
        this.getMessager().get().printMessage(Diagnostic.Kind.NOTE, this.format(message), element, mirror);
    }

    @Override
    public void warn(@Nullable final CharSequence message, @Nonnull final Element element, @Nonnull final AnnotationMirror mirror) {
        this.getMessager().get().printMessage(Diagnostic.Kind.WARNING, this.format(message), element, mirror);
    }

    @Override
    public void error(@Nullable final CharSequence message, @Nonnull final Element element, @Nonnull final AnnotationMirror mirror) {
        this.getMessager().get().printMessage(Diagnostic.Kind.ERROR, this.format(message), element, mirror);
    }

    @Override
    public void note(@Nullable final CharSequence message, @Nonnull final Element element, @Nonnull final AnnotationMirror mirror, @Nonnull final AnnotationValue value) {
        this.getMessager().get().printMessage(Diagnostic.Kind.NOTE, this.format(message), element, mirror, value);
    }

    @Override
    public void warn(@Nullable final CharSequence message, @Nonnull final Element element, @Nonnull final AnnotationMirror mirror, @Nonnull final AnnotationValue value) {
        this.getMessager().get().printMessage(Diagnostic.Kind.WARNING, this.format(message), element, mirror, value);
    }

    @Override
    public void error(@Nullable final CharSequence message, @Nonnull final Element element, @Nonnull final AnnotationMirror mirror, @Nonnull final AnnotationValue value) {
        this.getMessager().get().printMessage(Diagnostic.Kind.ERROR, this.format(message), element, mirror, value);
    }

    @Nonnull
    private CharSequence format(@Nullable final CharSequence message) {
        if (message == null || message.length() == 0) {
            return MessageFormat.format("[{0}] null", this.getTopic());
        }
        return MessageFormat.format("[{0}] {1}", this.getTopic(), message);
    }
}
