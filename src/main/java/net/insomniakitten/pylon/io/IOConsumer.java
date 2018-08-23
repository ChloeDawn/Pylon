package net.insomniakitten.pylon.io;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {
    void accept(T t) throws IOException;
}
