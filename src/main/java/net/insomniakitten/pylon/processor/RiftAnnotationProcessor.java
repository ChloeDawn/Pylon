package net.insomniakitten.pylon.processor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.stream.JsonWriter;
import net.insomniakitten.pylon.Pylon;
import net.insomniakitten.pylon.annotation.rift.Listener;
import net.insomniakitten.pylon.annotation.rift.Mod;
import net.insomniakitten.pylon.ref.Side;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public final class RiftAnnotationProcessor extends JsonAnnotationProcessor {
    @Override
    protected String getProcessorName() {
        return "pylon.rift";
    }

    @Override
    protected void getSupportedAnnotations(final ImmutableSet.Builder<String> builder) {
        builder.add("net.insomniakitten.pylon.annotation.rift.*");
    }

    @Override
    protected boolean onProcessAnnotations(final RoundEnvironment environment) {
        final Map<Element, Mod> modElements = this.collectDiscoveredMods(environment);
        final Map<Element, Listener> listenerElements = this.collectDiscoveredListeners(environment);

        if (modElements.isEmpty() && !listenerElements.isEmpty()) {
            this.getLogger().warn("No @Mod annotation discovered in environment");
            return false;
        }

        if (modElements.isEmpty()) {
            this.getLogger().note("No @Mod annotation discovered in environment");
            return false;
        }

        if (modElements.size() > 1) {
            this.getLogger().note("More than one @Mod annotation discovered in environment");
            return false;
        }

        try {
            this.openJsonWriter(Constants.FILE, json -> {
                json.beginObject();

                json.name(Constants.COMMENT).value(Constants.GENERATED + Pylon.VERSION);

                this.appendModToWriter(Iterables.getOnlyElement(modElements.entrySet()), json);

                if (listenerElements.isEmpty()) {
                    this.getLogger().note("No @Listener annotations discovered in environment");
                } else {
                    json.name(Constants.LISTENERS);
                    json.beginArray();

                    for (final Entry<Element, Listener> entry : listenerElements.entrySet()) {
                        this.appendListenerToWriter(entry, json);
                    }

                    json.endArray();
                }

                json.endObject();
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private Map<Element, Mod> collectDiscoveredMods(final RoundEnvironment environment) {
        return this.collectAnnotationsFor(environment, Mod.class, element -> {
            if (!(element instanceof TypeElement) && !(element instanceof PackageElement)) {
                this.getLogger().error("@Mod applied to non-type/non-package element", element);
                return false;
            }

            if (element instanceof TypeElement && !(element.getEnclosingElement() instanceof PackageElement)) {
                if (!element.getModifiers().contains(Modifier.STATIC)) {
                    this.getLogger().error("@Mod applied to non-static type element", element);
                    return false;
                }
            }

            return true;
        });
    }

    private Map<Element, Listener> collectDiscoveredListeners(final RoundEnvironment environment) {
        return this.collectAnnotationsFor(environment, Listener.class, element -> {
            if (!(element instanceof TypeElement)) {
                this.getLogger().error("@Listener applied to non-type element", element);
                return false;
            }

            if (!(element.getEnclosingElement() instanceof PackageElement)) {
                if (!element.getModifiers().contains(Modifier.STATIC)) {
                    this.getLogger().error("@Listener is not top-level or static", element);
                    return false;
                }
            }

            if (((TypeElement) element).getInterfaces().isEmpty()) {
                this.getLogger().warn("@Listener does not implement any interfaces", element);
            }

            return true;
        }, Comparator.comparingInt(element -> element.getAnnotation(Listener.class).priority()));
    }

    private void appendModToWriter(final Entry<Element, Mod> entry, final JsonWriter writer) throws IOException {
        final Element element = entry.getKey();
        final Mod mod = entry.getValue();

        if (mod.id().isEmpty()) {
            this.getLogger().error("Empty value 'id' in @Mod", element);
            return;
        } else {
            writer.name(Constants.ID).value(mod.id());
        }

        if (mod.name().isEmpty()) {
            this.getLogger().note("Empty value 'name' in @Mod, substituting '" + mod.id() + "'");
            writer.name(Constants.NAME).value(mod.id());
        } else {
            writer.name(Constants.NAME).value(mod.name());
        }

        if (mod.version().isEmpty()) {
            this.getLogger().error("Empty value 'version' in @Mod", element);
        } else {
            writer.name(Constants.VERSION).value(mod.version());
        }

        writer.name(Constants.SIDE).value(mod.side().getName());

        if (mod.authors().length > 0) {
            writer.name(Constants.AUTHORS);
            writer.beginArray();
            for (final String author : mod.authors()) {
                if (author.isEmpty()) {
                    this.getLogger().error("Empty element in value 'authors' in @Mod", element);
                    continue;
                }
                writer.value(author);
            }
            writer.endArray();
        }
    }

    private void appendListenerToWriter(final Entry<Element, Listener> entry, final JsonWriter writer) throws IOException {
        final TypeElement element = (TypeElement) entry.getKey();
        final Listener listener = entry.getValue();

        writer.beginObject();

        final ProcessingEnvironment env = this.getEnvironment();
        final Elements utils = env.getElementUtils();
        final Name name = utils.getBinaryName(element);

        writer.name(Constants.CLASS).value(name.toString());
        writer.name(Constants.SIDE).value(this.getSideName(listener.side()));
        writer.name(Constants.PRIORITY).value(listener.priority());

        writer.endObject();
    }

    /**
     * @param side The {@link Side} constant to be transformed
     * @return The name of the side relative to Rift implementation
     * @see <a href=https://git.io/fp3Wn>DimensionalDevelopment/Rift@2af4885</a>
     */
    private String getSideName(final Side side) {
        return side.isAmbiguous() ? "both" : side.getName();
    }

    private static final class Constants {
        public static final String LISTENERS = "listeners";
        private static final String FILE = "riftmod.json";
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
}
