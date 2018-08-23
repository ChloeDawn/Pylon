package net.insomniakitten.pylon.processor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.insomniakitten.pylon.logging.LoggerFactory;
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

@Getter
@Setter(AccessLevel.PRIVATE)
public abstract class PylonAnnotationProcessor implements Processor {
    private final Set<String> supportedAnnotationTypes;
    private ProcessingEnvironment environment;
    private final PylonLogger logger = LoggerFactory.newSimpleLogger(this.getProcessorName(), this.getEnvironment()::getMessager);

    private boolean initialized;

    {
        @Nonnull val builder = new ImmutableSet.Builder<String>();
        this.getSupportedAnnotations(builder);
        this.supportedAnnotationTypes = builder.build();
    }

    protected abstract String getProcessorName();

    protected abstract void getSupportedAnnotations(@Nonnull final ImmutableSet.Builder<String> builder);

    protected abstract boolean onProcessAnnotations(@Nonnull final RoundEnvironment environment);

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.emptySet();
    }

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public void init(@Nonnull final ProcessingEnvironment environment) {
        if (this.isInitialized()) {
            throw new IllegalStateException("Already initialized");
        }
        this.setEnvironment(environment);
        this.setInitialized(true);
    }

    @Override
    public final boolean process(@Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment environment) {
        return this.onProcessAnnotations(environment);
    }

    @Override
    public Iterable<? extends Completion> getCompletions(@Nonnull final Element element, @Nonnull final AnnotationMirror annotation, @Nonnull final ExecutableElement member, @Nonnull final String userText) {
        return Collections.emptySet();
    }

    @Nonnull
    protected final <T extends Annotation> Map<Element, T> collectAnnotationsFor(@Nonnull final RoundEnvironment environment, @Nonnull final Class<T> type, @Nonnull final Predicate<Element> filter) {
        return environment.getElementsAnnotatedWith(type).stream().filter(filter)
            .collect(ImmutableMap.toImmutableMap(Function.identity(), it -> it.getAnnotation(type)));
    }

    @Nonnull
    protected final <T extends Annotation> Map<Element, T> collectAnnotationsFor(@Nonnull final RoundEnvironment environment, @Nonnull final Class<T> type, @Nonnull final Predicate<Element> filter, @Nonnull final Comparator<Element> sorter) {
        return environment.getElementsAnnotatedWith(type).stream().filter(filter).sorted(sorter)
            .collect(ImmutableMap.toImmutableMap(Function.identity(), it -> it.getAnnotation(type)));
    }
}