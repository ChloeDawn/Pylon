package net.insomniakitten.pylon.logging;

import javax.annotation.Nonnull;
import javax.annotation.processing.Messager;
import java.util.function.Supplier;

public final class LoggerFactory {
    private LoggerFactory() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @Nonnull
    public static PylonLogger newSimpleLogger(final String topic, final Supplier<Messager> messager) {
        return new SimpleLogger(topic, messager);
    }
}
