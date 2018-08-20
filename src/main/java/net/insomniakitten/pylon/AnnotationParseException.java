package net.insomniakitten.pylon;

/**
 * Used to report any invalid elements discovered during annotation
 * parsing within the annotation processor execution lifecycle.
 * @author InsomniaKitten
 * @since 0.1.0
 */
public final class AnnotationParseException extends RuntimeException {
    public AnnotationParseException(final String message) {
        super(message);
    }
}
