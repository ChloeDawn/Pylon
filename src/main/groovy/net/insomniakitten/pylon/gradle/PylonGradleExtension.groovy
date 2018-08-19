package net.insomniakitten.pylon.gradle

import net.insomniakitten.pylon.PylonAnnotationProcessor

/**
 * Extension class for the Pylon Gradle plugin, providing configuration properties
 * @author InsomniaKitten
 * @since 0.1.0
 */
class PylonGradleExtension {
  public static final NAME = 'pylon'

  /**
   * Represents the Pylon annotation processor version
   */
  public final version = PylonAnnotationProcessor.VERSION

  /**
   * Determines if Pylon should allow empty name elements in the Mod annotation
   */
  public allowEmptyName = true // NYI

  /**
   * Determines if Pylon should generate a marker comment in the JSON file
   */
  public generateMarker = true // NYI
}
