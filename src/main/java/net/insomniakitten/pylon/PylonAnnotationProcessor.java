package net.insomniakitten.pylon;

import com.google.common.collect.Iterables;
import com.google.gson.stream.JsonWriter;
import net.insomniakitten.pylon.annotation.Listener;
import net.insomniakitten.pylon.annotation.Mod;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

/**
 * The core annotation processor class for Pylon. Processes the {@link Mod} and {@link Listener}
 * annotations discovered in the source at compile time, appends their data to a {@link JsonWriter},
 * and writes it to the target file {@link Constants#FILE} in the jar root.
 * @author InsomniaKitten
 * @since 0.1.0
 */
@ParametersAreNonnullByDefault
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("net.insomniakitten.pylon.annotation.*")
public final class PylonAnnotationProcessor extends AbstractProcessor {
    public static final String VERSION = "%VERSION%";

    private final LoggingProxy logger = new LoggingProxy();

    /**
     * Queries and processes elements annotated by {@link Mod} and {@link Listener} in the environment
     * @param annotations All annotations discovered by the processor
     * @param env A context of the source environment
     * @return True if annotations were present and the file was generated
     * @since 0.1.0
     */
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
        @Nonnull final Collection<? extends Element> mods = env.getElementsAnnotatedWith(Mod.class);
        @Nonnull final Collection<? extends Element> listeners = env.getElementsAnnotatedWith(Listener.class);

        if (mods.isEmpty() && listeners.isEmpty()) {
            this.logger.error("No annotations discovered, exiting...");
            return false;
        }

        if (mods.isEmpty()) {
            throw new IllegalStateException("No @Mod annotation discovered in environment");
        }

        if (mods.size() > 1) {
            throw new IllegalStateException("More than one @Mod annotation discovered in environment");
        }

        try (@Nonnull final Writer writer = this.createOutputFile().openWriter()) {
            try (@Nonnull final JsonWriter json = this.createJsonWriter(writer)) {
                json.beginObject();

                json.name(Constants.COMMENT).value(Constants.GENERATED + PylonAnnotationProcessor.VERSION);

                this.appendModToWriter(Iterables.getOnlyElement(mods), json);

                if (!listeners.isEmpty()) {
                    json.name("listeners");
                    json.beginArray();

                    for (@Nonnull final Element element : listeners) {
                        this.appendListenerToWriter(element, json);
                    }

                    json.endArray();
                }

                json.endObject();
            }
        } catch (@Nonnull final IOException e) {
            throw new JsonWriteException("Failed to write information to " + Constants.FILE, e);
        }

        return true;
    }

    /**
     * Creates a new {@link JsonWriter} for the given {@link Writer}
     * @param writer The file writer to be encapsulated
     * @return A preconfigured Json writer instance
     * @since 0.1.0
     */
    @Nonnull
    private JsonWriter createJsonWriter(final Writer writer) {
        final JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setIndent("    ");
        jsonWriter.setHtmlSafe(true);
        return jsonWriter;
    }

    /**
     * Generates a new {@link FileObject} for {@link Constants#FILE} in the root
     * @return A reference to the newly created file
     * @throws IOException If the file could not be created
     * @since 0.1.0
     */
    @Nonnull
    private FileObject createOutputFile() throws IOException {
        @Nonnull final ProcessingEnvironment env = this.processingEnv;
        @Nonnull final Filer filer = env.getFiler();
        return filer.createResource(StandardLocation.CLASS_OUTPUT, "", Constants.FILE);
    }

    /**
     * Appends the information from the {@link Mod} annotated {@link Element} to the open {@link JsonWriter}
     * @param element The annotated element
     * @param json The Json writer to be appended to
     * @throws IOException If the writer fails to append
     */
    private void appendModToWriter(final Element element, final JsonWriter json) throws IOException {
        if (!(element instanceof TypeElement) && !(element instanceof PackageElement)) {
            final String kind = element.getKind().name().toLowerCase(Locale.ROOT);
            throw new IllegalAnnotationException("@Mod applied to non-type/non-package element '" + kind + "'");
        }

        @Nonnull final String className = ((QualifiedNameable) element).getQualifiedName().toString();

        if (element instanceof TypeElement && !(element.getEnclosingElement() instanceof PackageElement)) {
            if (!element.getModifiers().contains(Modifier.STATIC)) {
                throw new IllegalAnnotationException("@Listener '" + className + "' is not top-level or static");
            }
        }

        @Nonnull final Mod mod = element.getAnnotation(Mod.class);

        if (mod.id().isEmpty()) {
            throw new AnnotationParseException("Empty value 'id' in @Mod");
        }

        json.name(Constants.ID).value(mod.id());

        if (mod.name().isEmpty()) {
            this.logger.note("Empty value 'name' in @Mod, substituting '" + mod.id() + "'");
            json.name(Constants.NAME).value(mod.id());
        } else {
            json.name(Constants.NAME).value(mod.name());
        }

        if (mod.version().isEmpty()) {
            throw new AnnotationParseException("Empty value 'version' in @Mod");
        }

        json.name(Constants.VERSION).value(mod.version());

        if (mod.authors().length > 0) {
            json.name(Constants.AUTHORS);
            json.beginArray();

            for (final String author : mod.authors()) {
                if (author.isEmpty()) {
                    throw new AnnotationParseException("Empty element in value 'authors' in @Mod");
                }
                json.value(author);
            }

            json.endArray();
        }
    }

    /**
     * Appends the information from the {@link Listener} annotated {@link Element} to the open {@link JsonWriter}
     * @param element The annotated element
     * @param json The Json writer to be appended to
     * @throws IOException If the writer fails to append
     */
    private void appendListenerToWriter(final Element element, final JsonWriter json) throws IOException {
        if (!(element instanceof TypeElement)) {
            @Nonnull final String kind = element.getKind().name().toLowerCase(Locale.ROOT);
            throw new IllegalAnnotationException("@Listener applied to non-type element '" + kind + "'");
        }

        @Nonnull final TypeElement type = (TypeElement) element;
        @Nonnull final String className = type.getQualifiedName().toString();

        if (!(element.getEnclosingElement() instanceof PackageElement)) {
            if (!element.getModifiers().contains(Modifier.STATIC)) {
                throw new IllegalAnnotationException("@Listener '" + className + "' is not top-level or static");
            }
        }

        if (type.getInterfaces().isEmpty()) {
            this.logger.warn("@Listener '" + className + "' does not implement any interfaces");
        }

        @Nonnull final Listener listener = element.getAnnotation(Listener.class);

        json.beginObject();

        json.name(Constants.CLASS).value(className);
        json.name(Constants.PRIORITY).value(listener.priority());
        json.name(Constants.SIDE).value(listener.side().getName());

        json.endObject();
    }

    /**
     * Static references to the target file name and all JSON keys used during processing
     * @author InsomniaKitten
     * @since 0.1.0
     */
    private static final class Constants {
        private static final String FILE = "riftmod.json";

        private static final String COMMENT = "comment";
        private static final String GENERATED = "Generated with Pylon ";

        private static final String ID = "id";
        private static final String NAME = "name";
        private static final String VERSION = "version";
        private static final String AUTHORS = "authors";

        private static final String CLASS = "class";
        private static final String PRIORITY = "side";
        private static final String SIDE = "side";
    }

    /**
     * Utility class used to proxy {@link CharSequence} messages
     * to the processing environment's {@link Messager} implementation
     * @author InsomniaKitten
     * @since 0.1.0
     */
    @SuppressWarnings("SameParameterValue")
    private final class LoggingProxy {
        private void note(final CharSequence msg) {
            PylonAnnotationProcessor.this.processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.NOTE, msg);
        }

        private void warn(final CharSequence msg) {
            PylonAnnotationProcessor.this.processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.WARNING, msg);
        }

        private void error(final CharSequence msg) {
            PylonAnnotationProcessor.this.processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, msg);
        }
    }
}
