package net.insomniakitten.pylon.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.util.GradleVersion

/**
 * Gradle plugin for the Pylon annotation processor, enabling automatic setup
 * of dependencies, and external modification of processor behaviour
 * @author InsomniaKitten
 * @since 0.1.0
 */
final class PylonGradlePlugin implements Plugin<Project> {
  @Override
  void apply(final Project project) {
    project.extensions.create PylonGradleExtension.NAME, PylonGradleExtension
    project.plugins.withType(JavaPlugin) {
      project.afterEvaluate { setupDependencies project }
    }
  }

  private void setupDependencies(final Project project) {
    this.addPylonDependency project, JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME
    this.addPylonDependency project, JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME
    if (GradleVersion.version(project.gradle.gradleVersion) >= GradleVersion.version('4.6')) {
      this.addPylonDependency project, JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME
      this.addPylonDependency project, JavaPlugin.TEST_ANNOTATION_PROCESSOR_CONFIGURATION_NAME
    }
  }

  private void addPylonDependency(final Project project, final String type) {
    project.dependencies.add type, "com.github.InsomniaKitten:Pylon:${project.pylon.version}", { transitive = false }
  }
}
