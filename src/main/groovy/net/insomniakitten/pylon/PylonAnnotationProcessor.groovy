package net.insomniakitten.pylon

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j2
import net.insomniakitten.pylon.annotation.Listener
import net.insomniakitten.pylon.annotation.Mod
import net.insomniakitten.pylon.io.AnnotationParseException
import net.insomniakitten.pylon.io.JSONWriteException

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.QualifiedNameable
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation

/**
 * The core annotation processor class for Pylon. Processes the {@link Mod} and {@link Listener}
 * annotations discovered in the source at compile time, and exports them as a single JSON object,
 * to the JAR file's root, which is accessed by Rift at runtime for loading the Mod information.
 * @author InsomniaKitten
 * @since 0.1.0
 */
@Log4j2
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes('net.insomniakitten.pylon.annotation.*')
final class PylonAnnotationProcessor extends AbstractProcessor {
  public static final VERSION = '%VERSION%'

  /**
   * Queries and processes elements annotated by {@link Mod} and {@link Listener} in the environment
   * @param annotations All annotations discovered by the processor
   * @param env A context of the source environment
   * @return True if annotations were present and successfully processed
   * @since 0.1.0
   */
  @Override
  boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
    final modElements = env.getElementsAnnotatedWith(Mod.class)
    final listenerElements = env.getElementsAnnotatedWith(Listener.class)

    if (modElements.empty && listenerElements.empty) {
      log.debug 'No annotations discovered, returning...'
      return false
    }

    final mod = [:]

    if (modElements.empty) {
      throw new IllegalStateException('Environment has @Listener annotations but no @Mod annotation')
    }

    if (modElements.size() > 1) {
      throw new IllegalStateException('Environment has more than one @Mod annotation')
    }

    appendGenerationMarkersFor(mod)

    processModElementFor(mod, modElements.iterator().next())

    if (listenerElements.empty) {
      log.debug 'No @Listeners annotations discovered, continuing...'
    }

    final listeners = []

    listenerElements.each { processListenerElementFor(listeners, it) }

    mod << [listeners: listeners]

    try {
      processingEnv.filer.createResource(StandardLocation.CLASS_OUTPUT, '', 'riftmod.json')
        .openWriter().withWriter { it << new JsonBuilder(mod).toPrettyString() }
    } catch (final IOException e) {
      throw new JSONWriteException('Failed to write mod information to file', e)
    }
    return true
  }

  /**
   * Accepts an element and attempts to write its {@link Mod} data to the target
   * @param element The {@link Mod} annotated source element
   * @param target The {@link LinkedHashMap} to be appended to
   * @since 0.1.0
   */
  private processModElementFor(final LinkedHashMap target, final Element element) {
    if (!(element instanceof TypeElement) && !(element instanceof PackageElement)) {
      throw new IllegalStateException("Discovered @Mod applied to non-type/non-package element '${element.kind.name()}'")
    }

    final clazz = (element as QualifiedNameable).qualifiedName.toString()

    if ((element.enclosingElement instanceof TypeElement) && !(Modifier.STATIC in element.modifiers)) {
      throw new IllegalStateException("@Mod '$clazz' is not top-level or static")
    }

    final mod = element.getAnnotation(Mod.class)

    if (mod.id().empty) {
      throw new AnnotationParseException("Empty 'id' in @Mod $clazz")
    }

    target << [id: mod.id()]

    if (mod.name().empty) {
      log.debug("Empty 'name' in @Mod {}, substituting '{}'", clazz, mod.id())
      target << [name: mod.id()]
    } else {
      target << [name: mod.name()]
    }

    if (mod.version().empty) {
      throw new AnnotationParseException("Empty 'version' in @Mod $clazz")
    }

    target << ['version': mod.version()]

    if (mod.authors().length > 0) {
      final authors = []

      mod.authors().each {
        if (it.empty) {
          throw new AnnotationParseException("Empty 'authors' element in @Mod $clazz")
        }
        authors << it
      }

      target << [authors: authors]
    }
  }

  /**
   * Accepts an element and attempts to write its {@link Listener} data to the target
   * @param element The {@link Listener} annotated source element
   * @param target The {@link List} to be appended to
   * @since 0.1.0
   */
  private processListenerElementFor(final List target, final Element element) {
    if (!(element instanceof TypeElement)) {
      throw new IllegalStateException("Discovered @Listener applied to non-type element '${element.kind.name()}'")
    }

    final type = (TypeElement) element
    final clazz = type.qualifiedName.toString()

    if (!(element.enclosingElement instanceof PackageElement) && !(Modifier.STATIC in element.modifiers)) {
      throw new IllegalStateException("@Listener '$clazz' is not top-level or static")
    }

    if (type.interfaces.empty) {
      log.warn "@Listener '{}' does not implement any interfaces", clazz
    }

    final listener = element.getAnnotation Listener.class

    target << [class: clazz, priority: listener.priority(), side: listener.side().name]
  }

  /**
   * Appends a generation marker comment to the given {@link Map}
   * @param target The JSON object to be appended to
   * @since 0.1.0
   */
  private void appendGenerationMarkersFor(final Map target) {
    target['comment'] = "Generated with Pylon $VERSION"
  }
}
