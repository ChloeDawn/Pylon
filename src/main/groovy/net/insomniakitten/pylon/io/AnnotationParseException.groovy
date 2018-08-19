package net.insomniakitten.pylon.io

/**
 * Reports any invalid elements discovered during annotation
 * parsing within the annotation processor execution lifecycle.
 * @author InsomniaKitten
 * @since 0.1.0
 */
final class AnnotationParseException extends RuntimeException {
  AnnotationParseException(final String message) {
    super(message)
  }
}
