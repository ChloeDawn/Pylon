package net.insomniakitten.pylon.logging;

import com.google.common.base.Preconditions;

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
    private final Supplier<Messager> messagerSupplier;

    @Nullable
    private Messager messager;

    SimpleLogger(final String topic, final Supplier<Messager> messagerSupplier) {
        this.topic = topic;
        this.messagerSupplier = messagerSupplier;
    }

    @Override
    public String getTopic() {
        return this.topic;
    }

    @Override
    public void note(@Nullable final CharSequence message) {
        this.getMessager().printMessage(Diagnostic.Kind.NOTE, this.format(message));
    }

    @Override
    public void warn(@Nullable final CharSequence message) {
        this.getMessager().printMessage(Diagnostic.Kind.WARNING, this.format(message));
    }

    @Override
    public void error(@Nullable final CharSequence message) {
        this.getMessager().printMessage(Diagnostic.Kind.ERROR, this.format(message));
    }

    @Override
    public void note(@Nullable final CharSequence message, final Element element) {
        this.getMessager().printMessage(Diagnostic.Kind.NOTE, this.format(message), element);
    }

    @Override
    public void warn(@Nullable final CharSequence message, final Element element) {
        this.getMessager().printMessage(Diagnostic.Kind.WARNING, this.format(message), element);
    }

    @Override
    public void error(@Nullable final CharSequence message, final Element element) {
        this.getMessager().printMessage(Diagnostic.Kind.ERROR, this.format(message), element);
    }

    @Override
    public void note(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror) {
        this.getMessager().printMessage(Diagnostic.Kind.NOTE, this.format(message), element, mirror);
    }

    @Override
    public void warn(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror) {
        this.getMessager().printMessage(Diagnostic.Kind.WARNING, this.format(message), element, mirror);
    }

    @Override
    public void error(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror) {
        this.getMessager().printMessage(Diagnostic.Kind.ERROR, this.format(message), element, mirror);
    }

    @Override
    public void note(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value) {
        this.getMessager().printMessage(Diagnostic.Kind.NOTE, this.format(message), element, mirror, value);
    }

    @Override
    public void warn(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value) {
        this.getMessager().printMessage(Diagnostic.Kind.WARNING, this.format(message), element, mirror, value);
    }

    @Override
    public void error(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value) {
        this.getMessager().printMessage(Diagnostic.Kind.ERROR, this.format(message), element, mirror, value);
    }

    @Override
    public String toString() {
        return String.format("SimpleLogger['%s', %s]", this.topic, this.messager);
    }

    @Nonnull
    private CharSequence format(@Nullable final CharSequence message) {
        if (message == null || message.length() == 0) {
            return MessageFormat.format("[{0}] null", this.getTopic());
        }
        return MessageFormat.format("[{0}] {1}", this.getTopic(), message);
    }

    private Messager getMessager() {
        if (this.messager == null) {
            this.messager = this.messagerSupplier.get();
        }
        return Preconditions.checkNotNull(this.messager);
    }
}
