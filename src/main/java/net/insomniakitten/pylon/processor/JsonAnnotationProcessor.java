package net.insomniakitten.pylon.processor;

import com.google.gson.stream.JsonWriter;
import net.insomniakitten.pylon.io.IOConsumer;

import javax.annotation.Nonnull;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public abstract class JsonAnnotationProcessor extends PylonAnnotationProcessor {
    /**
     * Opens an {@link JsonWriter} and feeds to the given {@link IOConsumer}
     * @param file The name of the file to write to
     * @param consumer The consumer to accept the writer
     * @throws IOException If the file cannot be written to
     * @since 0.3.0
     */
    protected final void openJsonWriter(final String file, final IOConsumer<JsonWriter> consumer) throws IOException {
        try (final JsonWriter jsonWriter = this.createJsonWriter(this.createFileAtRoot(file).openWriter())) {
            consumer.accept(jsonWriter);
        }
    }

    /**
     * Generates a new {@link FileObject} for the given file in the root
     * @param file The name of the file to be created
     * @return A reference to the newly created file
     * @throws IOException If the file could not be created
     * @since 0.3.0
     */
    @Nonnull
    private FileObject createFileAtRoot(final String file) throws IOException {
        return this.getEnvironment().getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", file);
    }

    /**
     * Creates a new {@link JsonWriter} for the given {@link Writer}
     * @param delegate The writer to be delegated to
     * @return A preconfigured Json writer instance
     * @since 0.1.0
     */
    @Nonnull
    private JsonWriter createJsonWriter(final Writer delegate) {
        final BufferedWriter bufferedWriter;

        if (delegate instanceof BufferedWriter) {
            bufferedWriter = (BufferedWriter) delegate;
        } else {
            bufferedWriter = new BufferedWriter(delegate);
        }

        final JsonWriter writer = new JsonWriter(bufferedWriter);

        writer.setIndent("  ");
        writer.setHtmlSafe(true);
        return writer;
    }
}
