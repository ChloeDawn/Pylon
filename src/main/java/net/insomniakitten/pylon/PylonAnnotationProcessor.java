package net.insomniakitten.pylon;

import com.google.common.collect.Iterables;
import com.google.gson.stream.JsonWriter;
import net.insomniakitten.pylon.annotation.Listener;
import net.insomniakitten.pylon.annotation.Mod;

import javax.annotation.Nonnull;
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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

/**
 * The core annotation processor class for Pylon. Processes the {@link Mod} and {@link Listener}
 * annotations discovered in the source at compile time, appends their data to a {@link JsonWriter},
 * and writes it to the target file {@link Constants#FILE} in the jar root.
 * @author InsomniaKitten
 * @since 0.1.0
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("net.insomniakitten.pylon.annotation.*")
public final class PylonAnnotationProcessor extends AbstractProcessor {
    public static final String VERSION = "0.2.0";

    private final LoggerImpl logger = new LoggerImpl();

    private boolean firstRun = true;

    /**
     * Queries and processes elements annotated by {@link Mod} and {@link Listener} in the environment
     * @param annotations All annotations discovered by the processor
     * @param env A context of the source environment
     * @return True if annotations were present and the file was generated
     * @since 0.1.0
     */
    @Override
    public boolean process(@Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment env) {
        if (this.firstRun) {
            this.logger.note("Pylon Annotation Processor " + PylonAnnotationProcessor.VERSION);
            this.firstRun = false;
        } else {
            return false;
        }

        @Nonnull final Collection<? extends Element> mods = env.getElementsAnnotatedWith(Mod.class);
        @Nonnull final Collection<? extends Element> listeners = env.getElementsAnnotatedWith(Listener.class);

        if (mods.isEmpty() && listeners.isEmpty()) {
            this.logger.warn("No annotations discovered in environment");
            return false;
        }

        if (mods.isEmpty()) {
            this.logger.error("No @Mod annotation discovered in environment");
            return false;
        }

        if (mods.size() > 1) {
            this.logger.error("More than one @Mod annotation discovered in environment");
            return false;
        }

        try (@Nonnull final Writer writer = this.createOutputFile().openWriter()) {
            try (@Nonnull final JsonWriter json = this.createJsonWriter(writer)) {
                json.beginObject();

                json.name(Constants.COMMENT).value(Constants.GENERATED + PylonAnnotationProcessor.VERSION);

                this.appendModToWriter(Iterables.getOnlyElement(mods), json);

                if (listeners.isEmpty()) {
                    this.logger.note("No @Listener annotations discovered");
                } else {
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
            throw new RuntimeException("Failed to write information to " + Constants.FILE, e);
        }

        return true;
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
        return filer.createResource(StandardLocation.SOURCE_OUTPUT, Constants.RESOURCES, Constants.FILE);
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

        @Nonnull final JsonWriter writer = new JsonWriter(bufferedWriter);

        writer.setIndent(Constants.INDENT);
        writer.setHtmlSafe(true);
        return writer;
    }

    /**
     * Appends the information from the {@link Mod} annotated {@link Element} to the open {@link JsonWriter}
     * @param element The annotated element
     * @param json The Json writer to be appended to
     * @throws IOException If the writer fails to append
     */
    private void appendModToWriter(@Nonnull final Element element, @Nonnull final JsonWriter json) throws IOException {
        if (!(element instanceof TypeElement) && !(element instanceof PackageElement)) {
            this.logger.error("@Mod cannot be applied to non-type/non-package element", element);
            return;
        }

        if (element instanceof TypeElement && !(element.getEnclosingElement() instanceof PackageElement)) {
            if (!element.getModifiers().contains(Modifier.STATIC)) {
                this.logger.error("@Mod cannot be applied to non-static type element", element);
                return;
            }
        }

        @Nonnull final Mod mod = element.getAnnotation(Mod.class);

        if (mod.id().isEmpty()) {
            this.logger.error("Empty value 'id' in @Mod", element);
            return;
        }

        if (mod.version().isEmpty()) {
            this.logger.error("Empty value 'version' in @Mod", element);
            return;
        }

        if (mod.name().isEmpty()) {
            this.logger.note("Empty value 'name' in @Mod, substituting '" + mod.id() + "'");
        }

        json.name(Constants.ID).value(mod.id());
        json.name(Constants.NAME).value(mod.name().isEmpty() ? mod.id() : mod.name());
        json.name(Constants.VERSION).value(mod.version());
        json.name(Constants.SIDE).value(mod.side().getName());

        if (mod.authors().length > 0) {
            json.name(Constants.AUTHORS);
            json.beginArray();

            for (@Nonnull final String author : mod.authors()) {
                if (author.isEmpty()) {
                    this.logger.error("Empty element in value 'authors' in @Mod", element);
                    continue;
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
    private void appendListenerToWriter(@Nonnull final Element element, @Nonnull final JsonWriter json) throws IOException {
        if (!(element instanceof TypeElement)) {
            this.logger.error("@Listener applied to non-type element", element);
            return;
        }

        if (!(element.getEnclosingElement() instanceof PackageElement)) {
            if (!element.getModifiers().contains(Modifier.STATIC)) {
                this.logger.error("@Listener is not top-level or static", element);
                return;
            }
        }

        @Nonnull final TypeElement type = (TypeElement) element;

        if (type.getInterfaces().isEmpty()) {
            this.logger.warn("@Listener does not implement any interfaces", element);
        }

        @Nonnull final Listener listener = element.getAnnotation(Listener.class);

        json.beginObject();

        json.name(Constants.CLASS).value(type.getQualifiedName().toString());
        json.name(Constants.PRIORITY).value(listener.priority());
        json.name(Constants.SIDE).value(listener.side().getName());

        json.endObject();

    }

    /**
     * Static references to the String constants used during processing
     * @author InsomniaKitten
     * @since 0.1.0
     */
    private static final class Constants {
        private static final String RESOURCES = "resources/";
        private static final String FILE = "riftmod.json";

        private static final String INDENT = "  ";

        private static final String COMMENT = "__comment";
        private static final String GENERATED = "Generated with Pylon ";

        private static final String ID = "id";
        private static final String NAME = "name";
        private static final String VERSION = "version";
        private static final String AUTHORS = "authors";

        private static final String CLASS = "class";
        private static final String PRIORITY = "priority";
        private static final String SIDE = "side";
    }

    /**
     * Utility class used to proxy {@link CharSequence} messages
     * to the processing environment's {@link Messager} implementation
     * @author InsomniaKitten
     * @since 0.1.0
     */
    @SuppressWarnings("SameParameterValue")
    private final class LoggerImpl {
        private void note(@Nonnull final CharSequence message) {
            this.messager().printMessage(Diagnostic.Kind.NOTE, message);
        }

        private void warn(@Nonnull final CharSequence message) {
            this.messager().printMessage(Diagnostic.Kind.WARNING, message);
        }

        private void error(@Nonnull final CharSequence message) {
            this.messager().printMessage(Diagnostic.Kind.ERROR, message);
        }

        private void note(@Nonnull final CharSequence message, @Nonnull final Element element) {
            this.messager().printMessage(Diagnostic.Kind.NOTE, message, element);
        }

        private void warn(@Nonnull final CharSequence message, @Nonnull final Element element) {
            this.messager().printMessage(Diagnostic.Kind.WARNING, message, element);
        }

        private void error(@Nonnull final CharSequence message, @Nonnull final Element element) {
            this.messager().printMessage(Diagnostic.Kind.ERROR, message, element);
        }

        @Nonnull
        private Messager messager() {
            return PylonAnnotationProcessor.this.processingEnv.getMessager();
        }
    }
}
