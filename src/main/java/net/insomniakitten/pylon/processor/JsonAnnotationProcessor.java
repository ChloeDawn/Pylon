package net.insomniakitten.pylon.processor;

import com.google.gson.stream.JsonWriter;
import lombok.val;
import net.insomniakitten.pylon.io.IOConsumer;

import javax.annotation.Nonnull;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public abstract class JsonAnnotationProcessor extends PylonAnnotationProcessor {
    protected final void openJsonWriter(@Nonnull final String file, @Nonnull final IOConsumer<JsonWriter> consumer) {
        try (@Nonnull val fileWriter = this.createFileAtRoot(file).openWriter()) {
            try (@Nonnull val jsonWriter = this.createJsonWriter(fileWriter)) {
                consumer.accept(jsonWriter);
            }
        } catch (@Nonnull final IOException e) {
            throw new RuntimeException(e);
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
    private FileObject createFileAtRoot(@Nonnull final String file) throws IOException {
        return this.getEnvironment().getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", file);
    }

    /**
     * Creates a new {@link JsonWriter} for the given {@link Writer}
     * @param delegate The writer to be delegated to
     * @return A preconfigured Json writer instance
     * @since 0.1.0
     */
    @Nonnull
    private JsonWriter createJsonWriter(@Nonnull final Writer delegate) {
        @Nonnull final BufferedWriter bufferedWriter;

        if (delegate instanceof BufferedWriter) {
            bufferedWriter = (BufferedWriter) delegate;
        } else {
            bufferedWriter = new BufferedWriter(delegate);
        }

        @Nonnull val writer = new JsonWriter(bufferedWriter);

        writer.setIndent("  ");
        writer.setHtmlSafe(true);
        return writer;
    }
}
