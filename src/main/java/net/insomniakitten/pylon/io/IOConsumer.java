package net.insomniakitten.pylon.io;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {
    void accept(final T t) throws IOException;
}
