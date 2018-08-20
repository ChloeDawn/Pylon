package net.insomniakitten.pylon;

/**
 * Reports any illegal annotations found in source during annotation
 * parsing within the annotation processor execution lifecycle.
 * @author InsomniaKitten
 * @since 0.1.0
 */
public final class IllegalAnnotationException extends IllegalStateException {
    public IllegalAnnotationException(final String message) {
        super(message);
    }
}
