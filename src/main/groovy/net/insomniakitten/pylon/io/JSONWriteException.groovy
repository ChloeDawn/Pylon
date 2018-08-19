package net.insomniakitten.pylon.io

/**
 * Propagates caught {@link IOException}s during JSON file
 * writing within the annotation processor execution lifecycle.
 * @author InsomniaKitten
 * @since 0.1.0
 */
final class JSONWriteException extends RuntimeException {
  JSONWriteException(final String message, final IOException cause) {
    super(message, cause)
  }
}

