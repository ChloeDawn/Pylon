package net.insomniakitten.pylon.logging;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import java.util.function.Supplier;

/**
 * Logging interface for printing messages with context on
 * the given elements, annotations, and their values
 * @author InsomniaKitten
 * @see LoggerFactory#newSimpleLogger(String, Supplier)
 * @since 0.3.0
 */
public interface PylonLogger {
    /**
     * The topic of this logger, used as an output prefix
     */
    String getTopic();

    /**
     * Prints a note message
     * @param message The message to print
     */
    void note(@Nullable final CharSequence message);

    /**
     * Prints a warning message
     * @param message The message to print
     */
    void warn(@Nullable final CharSequence message);

    /**
     * Prints an error message
     * @param message The message to print
     */
    void error(@Nullable final CharSequence message);

    /**
     * Prints a note message with a position hint for the given element
     * @param message The message to print
     * @param element The element to provide a position hint for
     */
    void note(@Nullable final CharSequence message, final Element element);

    /**
     * Prints a warning message with a position hint for the given element
     * @param message The message to print
     * @param element The element to provide a position hint for
     */
    void warn(@Nullable final CharSequence message, final Element element);

    /**
     * Prints an error message with a position hint for the given element
     * @param message The message to print
     * @param element The element to provide a position hint for
     */
    void error(@Nullable final CharSequence message, final Element element);

    /**
     * Prints a note message with a position hint for the given annotation
     * @param message The message to print
     * @param element The element containing the annotation
     * @param mirror The annotation to provide a position hint for
     */
    void note(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror);

    /**
     * Prints a warning message with a position hint for the given annotation
     * @param message The message to print
     * @param element The element containing the annotation
     * @param mirror The annotation to provide a position hint for
     */
    void warn(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror);

    /**
     * Prints an error message with a position hint for the given annotation
     * @param message The message to print
     * @param element The element containing the annotation
     * @param mirror The annotation to provide a position hint for
     */
    void error(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror);

    /**
     * Prints a note message with a position hint for the given annotation value
     * @param message The message to print
     * @param element The element containing the annotation
     * @param mirror The annotation containing the value
     * @param value The value to provide a position hint for
     */
    void note(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value);

    /**
     * Prints a warning message with a position hint for the given annotation value
     * @param message The message to print
     * @param element The element containing the annotation
     * @param mirror The annotation containing the value
     * @param value The value to provide a position hint for
     */
    void warn(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value);

    /**
     * Prints an error message with a position hint for the given annotation value
     * @param message The message to print
     * @param element The element containing the annotation
     * @param mirror The annotation containing the value
     * @param value The value to provide a position hint for
     */
    void error(@Nullable final CharSequence message, final Element element, final AnnotationMirror mirror, final AnnotationValue value);
}
