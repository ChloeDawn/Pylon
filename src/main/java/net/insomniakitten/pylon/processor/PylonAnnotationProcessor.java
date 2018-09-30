package net.insomniakitten.pylon.processor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.insomniakitten.pylon.logging.PylonLogger;

import javax.annotation.Nonnull;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PylonAnnotationProcessor implements Processor {
    private final Set<String> supportedAnnotationTypes;
    private ProcessingEnvironment environment;
    private final PylonLogger logger = PylonLogger.of(
        this.getProcessorName(), () -> this.getEnvironment().getMessager()
    );

    private boolean initialized;

    {
        final ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<>();
        this.getSupportedAnnotations(builder);
        this.supportedAnnotationTypes = builder.build();
    }

    protected abstract String getProcessorName();

    protected abstract void getSupportedAnnotations(final ImmutableSet.Builder<String> builder);

    protected abstract boolean onProcessAnnotations(final RoundEnvironment environment);

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return this.supportedAnnotationTypes;
    }

    public ProcessingEnvironment getEnvironment() {
        return this.environment;
    }

    private void setEnvironment(final ProcessingEnvironment environment) {
        this.environment = environment;
    }

    public PylonLogger getLogger() {
        return this.logger;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    private void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.emptySet();
    }

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public void init(final ProcessingEnvironment environment) {
        if (this.isInitialized()) {
            throw new IllegalStateException("Already initialized");
        }
        this.setEnvironment(environment);
        this.setInitialized(true);
    }

    @Override
    public final boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment environment) {
        return this.onProcessAnnotations(environment);
    }

    @Override
    public Iterable<? extends Completion> getCompletions(final Element element, final AnnotationMirror annotation, final ExecutableElement member, final String userText) {
        return Collections.emptySet();
    }

    @Nonnull
    protected final <T extends Annotation> Map<Element, T> collectAnnotationsFor(final RoundEnvironment environment, final Class<T> type, final Predicate<Element> filter) {
        return environment.getElementsAnnotatedWith(type).stream().filter(filter)
            .collect(ImmutableMap.toImmutableMap(Function.identity(), it -> it.getAnnotation(type)));
    }

    @Nonnull
    protected final <T extends Annotation> Map<Element, T> collectAnnotationsFor(final RoundEnvironment environment, final Class<T> type, final Predicate<Element> filter, final Comparator<Element> sorter) {
        return environment.getElementsAnnotatedWith(type).stream().filter(filter).sorted(sorter)
            .collect(ImmutableMap.toImmutableMap(Function.identity(), it -> it.getAnnotation(type)));
    }
}
