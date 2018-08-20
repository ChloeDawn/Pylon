package net.insomniakitten.pylon;

import java.io.IOException;

/**
 * Used to propagate caught {@link IOException}s during file writing
 * @author InsomniaKitten
 * @since 0.1.0
 */
public final class JsonWriteException extends RuntimeException {
    public JsonWriteException(final String message, final IOException cause) {
        super(message, cause);
    }
}
